package remi.remotecontrol;

import java.util.ArrayList;

import remi.remotecontrol.adapter.ServiceAdapter;
import remi.remotecontrol.pojo.RemoteService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ServiceDiscoveryActivity extends Activity implements OnItemClickListener {

	public static final String SELECTED_SERVICE = "SELECTED_SERVICE";
	public static final String SERVICE_BASE_URL = "SERVICE_BASE_URL";
	LocalBroadcastManager broadcaster;
	private BroadcastReceiver receiver;
	private ListView list;
	private ServiceAdapter adapter; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		broadcaster = LocalBroadcastManager.getInstance(this);

		Intent intent = new Intent(this, DiscoveryService.class);
		startService(intent);

		setContentView(R.layout.activity_service_discovery);

		list = (ListView) findViewById(R.id.discoveryList);
		adapter = new ServiceAdapter(this, new ArrayList<RemoteService>());

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String name = intent.getStringExtra(DiscoveryService.DISCOVERY_SERVICE_NAME);
				String host = intent.getStringExtra(DiscoveryService.DISCOVERY_SERVICE_HOST);
				String port = intent.getStringExtra(DiscoveryService.DISCOVERY_SERVICE_PORT);
				adapter.add(new RemoteService(name, host, port));
			}
		};
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(DiscoveryService.DICOVERY_RESULT));
	}

	@Override
	protected void onStop() {
		super.onStop();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_service_discovery, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
		RemoteService service = (RemoteService)adapter.getItemAtPosition(position);
		if(service != null){
			Intent intent = new Intent(this,CaptureActivity.class);
			startActivity(intent);

			Intent intentBaseUrl = new Intent(SELECTED_SERVICE);
			intentBaseUrl.putExtra(SERVICE_BASE_URL, service.getHost() + ":" + service.getPort());
			broadcaster.sendBroadcast(intentBaseUrl);
		}
	}

}
