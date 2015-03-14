package remi.remotecontrol;

import remi.discovery.Discovery;
import remi.discovery.DiscoveryRequest;
import remi.discovery.OnDiscoveryListener;
import remi.discovery.ServiceDescription;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class DiscoveryService extends Service implements /*DiscoveryListener, ResolveListener,*/ OnDiscoveryListener{

	//private static final String SERVICE_TYPE = "_remotecontrol._tcp";
	public static final String DISCOVERY_SERVICE_NAME = "DISCOVERY_SERVICE_NAME";
	public static final String DISCOVERY_SERVICE_HOST = "DISCOVERY_SERVICE_HOST";
	public static final String DISCOVERY_SERVICE_PORT = "DISCOVERY_SERVICE_PORT";
	public static final String DICOVERY_RESULT = "DICOVERY_RESULT";
	//private NsdManager mNsdManager;
	private String mServiceName = "remi.RemoteControl";
	private LocalBroadcastManager broadcaster;

	@Override
	public void onCreate() {
		super.onCreate();
		Discovery.discover(new DiscoveryRequest(mServiceName), this);
		broadcaster = LocalBroadcastManager.getInstance(this);
	}


	public void setActivityEnabled(Context context,final Class<? extends Activity> activityClass,final boolean enable)
	{
		final PackageManager pm= getPackageManager();
		final int enableFlag=enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		pm.setComponentEnabledSetting(new ComponentName(context,activityClass),enableFlag,PackageManager.DONT_KILL_APP);
	}


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}



	@Override
	public void onServiceFound(ServiceDescription service) {
		Intent intent = new Intent(DICOVERY_RESULT);
	    if(service != null){
	        intent.putExtra(DISCOVERY_SERVICE_NAME, service.getServiceName());
	        intent.putExtra(DISCOVERY_SERVICE_PORT, service.getParameters().get("port"));
	        intent.putExtra(DISCOVERY_SERVICE_HOST, service.getInetAddress());
	        broadcaster.sendBroadcast(intent);
	    }
		
	}

	@Override
	public void onDestroy() {
		Discovery.stopDiscover(this);
		super.onDestroy();
	}

}
