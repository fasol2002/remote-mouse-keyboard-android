package remi.remotecontrol.adapter;

import java.util.List;
import java.util.Random;

import remi.remotecontrol.R;
import remi.remotecontrol.pojo.RemoteService;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceAdapter extends BaseAdapter {


	private List<RemoteService> list;
	//private Context context;
	private LayoutInflater inflater;



	public ServiceAdapter(Context context, List<RemoteService> list) {
		//this.context = context;
		this.list = list;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = inflater.inflate(R.layout.service_list_item, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
		TextView iconView = (TextView) rowView.findViewById(R.id.icon);

		RemoteService service = list.get(position);
		
		String shortHost = service.getHost().substring(service.getHost().lastIndexOf(".")+1);
		
		textView.setText(service.toString());
		iconView.setText(shortHost);
		Random rand = new Random(System.currentTimeMillis());
		iconView.setBackgroundColor(Color.argb(50, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		return rowView;
	}
	
	public void add(RemoteService service){
		if (!list.contains(service)){
			list.add(service);
			notifyDataSetChanged();
		}
	}
	
	public void remove(RemoteService service){
		list.remove(service);
		notifyDataSetChanged();
	}

}
