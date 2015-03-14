package remi.remotecontrol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import remi.remotecontrol.request.RequestTask;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CaptureActivity extends Activity implements OnTouchListener, OnKeyListener, OnClickListener, OnLongClickListener, OnEditorActionListener{

	private static final String DEFAULT_BASE = "http://192.168.0.25:9997/remote";
	private long execTime = 0;
	private Float last_x = null;
	private Float last_y = null;
	private GestureDetector gestureDetector = null;
	private BroadcastReceiver receiver;
	private String baseUrl ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);

		final View remoteView = findViewById(R.id.remoteview);
		View maineditorView = findViewById(R.id.maineditor);
		remoteView.setOnTouchListener(this);
		maineditorView.setOnKeyListener(this);
		//remoteView.setOnClickListener(this);
		//remoteView.setOnLongClickListener(this);

		EditText editText = (EditText) findViewById(R.id.maineditor);
		editText.setOnEditorActionListener(this);

		//		InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//		
		//		getWindow().setSoftInputMode(
		//				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
		//				);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				baseUrl = intent.getStringExtra(ServiceDiscoveryActivity.SERVICE_BASE_URL);
			}
		};
		
		try {
			new RequestTask().execute(getBaseUrl()+"/version/"+Long.toString(getPackageManager().getPackageInfo(getPackageName(), 0).lastUpdateTime));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		this.gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public void onLongPress(MotionEvent e) {
				onLongClick(null);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				new RequestTask().execute(getBaseUrl()+"/doubleclick/");
				return super.onDoubleTap(e);
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {

				float norm_x = (distanceX/remoteView.getMeasuredWidth())*10;

				if(execTime  == 0){
					execTime = System.currentTimeMillis();
				}

				long time = System.currentTimeMillis();
				long lasted = time - execTime;
				if (lasted > 100){
					execTime = System.currentTimeMillis();
					new RequestTask().execute(getBaseUrl()+"/scroll/"+(norm_x));
				}
				return super.onScroll(e1,e2,distanceX,distanceY);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				onClick(null);
				return super.onSingleTapConfirmed(e);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_capture, menu);
		return true;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {

		
		if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN)
		{
			float x = event.getRawX();
			float y = event.getRawY();
			last_x = x;
			last_y = y;

		}else if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			float x = event.getRawX();
			float y = event.getRawY();

			//float norm_x = x/view.getMeasuredWidth();
			//float norm_y = y/view.getMeasuredHeight();

			if(last_x  == null){
				last_x = Float.valueOf(x);
			}
			if(last_y  == null){
				last_y = Float.valueOf(y);
			}

			if(execTime  == 0){
				execTime = System.currentTimeMillis();
			}

			long time = System.currentTimeMillis();
			long lasted = time - execTime;
			if (lasted > 100){
				execTime = System.currentTimeMillis();
				new RequestTask().execute(getBaseUrl()+"/mouse/"+(x-last_x)+"/"+(y-last_y));
				last_x = x;
				last_y = y;
			}

		}
		
		//if(((CheckBox) view.findViewById(R.id.enablegesture)).isChecked()){
			gestureDetector.onTouchEvent(event);
		//}
		

		return true;
	}

	@Override
	public boolean onKey(View view, int key, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP)
		{      
			new RequestTask().execute(getBaseUrl()+"/key/" + Integer.valueOf(key));
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		new RequestTask().execute(getBaseUrl()+"/click/");
	}

	@Override
	public boolean onLongClick(View view) {
		new RequestTask().execute(getBaseUrl()+"/longclick/");
		return false;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_SEND) {
			try {
				new RequestTask().execute(getBaseUrl()+"/type/"+ URLEncoder.encode(v.getText().toString(), "utf-8"));
				v.setText("");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			handled = true;
		}
		return handled;
	}

	private String getBaseUrl(){
		return baseUrl == null ? DEFAULT_BASE : "http://" + baseUrl + "/remote";
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ServiceDiscoveryActivity.SELECTED_SERVICE));
	}

	@Override
	protected void onStop() {
		super.onStop();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}
}
