package app.ejust;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import app.ejust.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public  class MapActivity extends Activity  implements LocationListener{
	
	public static WebView webview;
	private double lattitude = 30.017177;
	private double longitude = 31.412648;
	public static LinkedList <String> routes = new LinkedList<String>();
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        getLocation();
        setupWebView();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
    }


    private void getLocation() {
    	 LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	 locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	     Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	     if(location != null){
	    	 lattitude = location.getLatitude();
	    	 longitude = location.getLongitude(); 
	     }
    	
	}


	public  void setupWebView(){
    	  final String centerURL = "javascript:init(" +lattitude + "," +longitude+ ")";
    	  webview = (WebView) findViewById(R.id.webview);
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
	

	
	 public class JavaScriptInterface {
	        Context mContext;
	 
	        JavaScriptInterface(Context c) {
	            mContext = c;
	        }
	  
 public void getRoute(String json) {
	    JSONArray routeArray = null;
	try {
		 JSONObject ob = new JSONObject(json);
	     routeArray = ob.getJSONArray("routes");
	     String sent = "";
	     for(int i=0;i<routeArray.length();i++){
		    	JSONObject route = routeArray.getJSONObject(i);
		    	JSONArray legs = route.getJSONArray("legs");
		    	JSONObject leg = legs.getJSONObject(0);
		    	JSONArray steps = leg.getJSONArray("steps");
		    	for(int j=0;j<steps.length();j++){
		    		JSONObject step = steps.getJSONObject(j);
		    		JSONObject start = step.getJSONObject("start_location");
		    		String st = start.toString();
		    		String l = st.charAt(2)+""+st.charAt(3);
		    		String g = st.charAt(st.indexOf(",")+2)+""+st.charAt(st.indexOf(",")+3);
		    		sent+=start.getString(g)+","+start.getString(l)+"/";
		    		if(j % 15 == 0 || j == steps.length()-1 && sent.compareTo("")!=0){		    			
		    	        HomeActivity.server.gcmServer(sent);
		    			sent = "";
		    		}

		    	}
		    	
		    }
	     

	} catch (JSONException e) {
		e.printStackTrace();
	}
}
	        
	 }       
    
}