package com.app.aroundu;



import com.google.android.gcm.GCMRegistrar;

import static com.app.aroundu.CommonUtilities.SENDER_ID;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.aroundu.WizardActivity;

public class ChildActivity extends Activity {
   
	
    public final static String ACTION = "action!";
    public static SharedPreferences prefs;
    public static String preferences = "pref";
    public static Context context;
    public static ServerMsgChild server;
	private Button st;
	private Button guide;
	private Button help;
	private Intent intent;
	private boolean started ;
	private Receive rcv;
	public static String account;
	private Intent batteryIntent ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
        	 showDialog();
        }
         prefs = WizardActivity.prefs;
         context = this;        
         RouteService.cntx= context;
         super.onCreate(savedInstanceState);
         setContentView(R.layout.main_child);
         st = (Button)findViewById(R.id.button1);
         guide = (Button)findViewById(R.id.guide);
         help = (Button) findViewById(R.id.contact);
         
         GCMRegistrar.checkDevice(this);
  		 GCMRegistrar.checkManifest(this);
  		 String regId = GCMRegistrar.getRegistrationId(this);
 		 if (regId.equals("")) {
  			GCMRegistrar.register(this, SENDER_ID);
  		 } else{
  			Log.d("done ","get reg id succesfully "+regId);
  		 }
 		
         batteryIntent = new Intent(this, BatteryService.class);
         intent = new Intent(this, RouteService.class);
         IntentFilter intentFilter = new IntentFilter();
         intentFilter.addAction(ACTION);
         rcv = new Receive();	          
         registerReceiver(rcv, intentFilter);
         account = prefs.getString("account","");
         if(!RouteService.work){
	         started = false;
	         server = new ServerMsgChild(context);
	        
         }else{
        	 	started = true;
        	    st.setText("Stop");
		        st.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_blue_stop, 0);
         }
         guide.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				guide();
			}
        	 
         });
         
         help.setOnClickListener(new Button.OnClickListener(){

 			public void onClick(View v) {
 				server.gcmServer("needs for help.please contact him:"+account);
 			}
         	 
          });
         st.setOnClickListener(new Button.OnClickListener(){
        	   public void onClick(View arg0) {
        		   if(!started){
        			   String rt = ChildActivity.prefs.getString("route","");
        			   if(rt.compareTo("") == 0){
        		    		Toast.makeText(context, "No route specified", Toast.LENGTH_LONG).show();
        			   }else{
        				    started = true;
        		    		start();
        			        st.setText("Stop");
        			        st.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_blue_stop, 0);
        			   }
        		   }
        		   else{
        			   started = false;
        			   stop();
        			   st.setText(" Start");
   			           st.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.player_start, 0);
        		   }
        	   }});
     
         
    }
    
    private void stop(){
    	if(!RouteService.arrived)
    		server.gcmServer("application's has been deactivated:"+account);
    	RouteService.work = false;
    	stopService(batteryIntent);
    	stopService(intent);
    	
    }
    
    @Override
    public void onStop(){
    	try{
    	unregisterReceiver(rcv);
    	}catch(IllegalArgumentException e){
    		
    	}
    	super.onStop();
    }
   
    private  void start(){
        startService(intent);
        startService(batteryIntent);
    }
    
    
    private void guide(){
      if(RouteService.points.size() == 0)
    		Toast.makeText(context, "No route specified yet", Toast.LENGTH_LONG).show();
      else {
    	  Intent i = new Intent(this, GuideView.class);
          startActivity(i);
      }
    }
    
    
    private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			Log.d("debug","errorrr");
		}
	}

    private void showDialog() {
    	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		//alertbox.setIcon(R.drawable.icon);
		alertbox.setTitle("Network error");
		alertbox.setMessage("This application requires a working data connection");
		alertbox.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				System.exit(0);
			}
		});

		alertbox.show();
		
	}
    
    
	public class Receive extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent in) {
			String msg = in.getStringExtra("stop");
			if(msg != null){
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 st.setText("Start");
					     st.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.player_start, 0);

				    }
				});
				
		        stopService(intent);
		        stopService(batteryIntent);
	      }
	}
}
	

    	
    }
    
    


