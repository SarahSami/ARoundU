package com.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class BatteryService extends Service {

	  BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
		    @Override
		    public void onReceive(Context arg0, Intent intent) {
		      int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		      String lv = String.valueOf(level);
		      Log.d("battery>>",lv);
		      if(RouteService.work && !RouteService.arrived){//TODO change condition
		    	  if(level == 20 || level == 10 || level == 5)
		    			  ChildActivity.server.gcmServer("battery's is low."+lv+":"+ChildActivity.account);
		    	  
		    	  }
		    }
		  };
		  
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    this.unregisterReceiver(mBatInfoReceiver);
	}
	
	public void onCreate() {
	    super.onCreate();
	    this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

}
