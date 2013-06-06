package app.child;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GuideView extends Activity{
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.map);
	        setupWebView();
	        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        
	    }
	 
	 
	 public void setupWebView(){
		  
			//  final String centerURL = "javascript:init("+31.256028+","+29.992676+"," 
 		//		  +31.248177 + "," +30.003319+","+31.280520+","+30.015280+","+31.217940+","+29.928303+")";
		 
		 
	    	      RouteService.gotLost();
	    		  final WebView webview;
	    		  String []src = RouteService.points.get(0).split(",");
	    		  String [] dst = RouteService.points.get(RouteService.points.size()-1).split(",");
	    		  
	    		  final String centerURL = "javascript:init("+RouteService.minLat+","+RouteService.minLng+"," 
	    				  +RouteService.lat + "," +RouteService.lng+","+src[0]+","+src[1]+","+dst[0]+","+dst[1]+")";
	        	  webview = (WebView) findViewById(R.id.webview);
	        	  WebSettings webSettings = webview.getSettings();
	        	  webSettings.setJavaScriptEnabled(true);
	        	  webview.setWebViewClient(new WebViewClient(){
	        		  
	        	    @Override
	        	    public void onPageFinished(WebView view, String url){
	        	    	webview.loadUrl(centerURL);
	       
	        	    }
	        	  });
	        	  webview.loadUrl("file:///android_asset/map.html");
	    		
	 }
	 
	 

}
