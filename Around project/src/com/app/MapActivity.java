package com.app;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public  class MapActivity extends Activity  implements LocationListener{
	
	public static WebView webview;
	private double lattitude = 31.214857;
	private double longitude = 29.939804;
	private String final_json = "";
	public static LinkedList <String> routes = new LinkedList<String>();
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        Button button = (Button) findViewById(R.id.route);
	    button.setOnClickListener(new Button.OnClickListener(){
	    	public void onClick(View v) {
	    		String name = ((TextView) findViewById(R.id.title)).getText().toString();
	    		if(name.compareTo("") == 0)
	    			showToast();
	    		else
	    			sendRoute(name);
	    	
	    	}
	    });
        getLocation();
        setupWebView();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
    }


    private void showToast(){
    	Toast.makeText(this, "Add title for the route", Toast.LENGTH_LONG).show();
    }
	private void getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		// get the location from network provider or from GPS
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}else{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}

		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(location != null){
			lattitude = location.getLatitude();
			longitude = location.getLongitude(); 
		}
	}


	public  void setupWebView(){
    	  final String centerURL = "javascript:init(" +lattitude + "," +longitude+ ")";
    	  webview = (WebView) findViewById(R.id.map_view);
    	  webview.getSettings().setJavaScriptEnabled(true);
    	  WebSettings webSettings = webview.getSettings();
    	  webSettings.setJavaScriptEnabled(true);
    	  
    	  webview.setWebViewClient(new WebViewClient(){
    		  
    	    @Override
    	    public void onPageFinished(WebView view, String url){
    	    	webview.loadUrl(centerURL);
   
    	    }
    	  });
    	  webview.addJavascriptInterface(new JavaScriptInterface(this), "Android");
    	  webview.loadUrl("file:///android_asset/map.html");
    	  

    	}
      


	public void onLocationChanged(Location arg0) {
        lattitude = arg0.getLatitude();
		longitude = arg0.getLongitude();		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	

	private void sendRoute(String title){
		  JSONArray routeArray = null;
			try {
				 JSONObject ob = new JSONObject(final_json);
			     routeArray = ob.getJSONArray("routes");
			     String sent = "";
			     boolean putStart = false;
			     for(int i=0;i<routeArray.length();i++){
				    	JSONObject route = routeArray.getJSONObject(i);
				    	JSONArray legs = route.getJSONArray("legs");
				    	JSONObject leg = legs.getJSONObject(0);
				    	JSONArray steps = leg.getJSONArray("steps");
				    	
				    	for(int j=0;j<steps.length();j++){
				    		JSONObject step = steps.getJSONObject(j);
				    		JSONObject start = step.getJSONObject("end_location");

				    		if(!putStart){
				    			JSONObject end = step.getJSONObject("start_location");
				    			String ed = start.toString();
				    			String k = ed.charAt(2)+""+ed.charAt(3);
				    			String g = ed.charAt(ed.indexOf(",")+2)+""+ed.charAt(ed.indexOf(",")+3);
				    			sent = end.getString(g)+","+end.getString(k);
				    			putStart = true;
				    		}
				    		
				    		String st = start.toString();
				    		
				    		String lng = st.charAt(2)+""+st.charAt(3);
				    		String lat = st.charAt(st.indexOf(",")+2)+""+st.charAt(st.indexOf(",")+3);
				    		
				    		
				    		sent = sent+"/"+ start.getString(lat)+","+start.getString(lng);
				    		
				    		
				    		
//				    		if(j % 15 == 0 || j == steps.length()-1 && sent.compareTo("")!=0){		    			
////				    	        HomeActivity.server.gcmServer(strt+sent);
//				    	        Log.d("in senddiiiin","sent to server"+sent);
//				    			sent = "";
//				    			strt = "";
//				    		}

				    	}
				    	
				    }
			     	finish();
			    	Intent i = new Intent(this, ChildInfoActivity.class);
			    	i.putExtra("title",title);
			    	i.putExtra("route",sent);
				    startActivity(i);

			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	
	 public class JavaScriptInterface {	
	        Context mContext;
	 
	        JavaScriptInterface(Context c) {
	            mContext = c;
	        }
	  
	 public void getRoute(String json) {
		 final_json = json;  
	 	}
	        
	}       
    
}