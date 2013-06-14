package app.ui;


import app.aroundu.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public  class LocationView extends Activity  {
	
	public static WebView webview;
	private double lattitude = 40.147006;
	private double longitude = 50.970703;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent= getIntent(); 
        lattitude = Double.parseDouble(intent.getStringExtra("lat")); 
        longitude = Double.parseDouble(intent.getStringExtra("lng"));
        setupWebView();
    }



	public  void setupWebView(){
    	  final String centerURL = "javascript:init(" +lattitude + "," +longitude+ ")";
    	  webview = (WebView) findViewById(R.id.locview);
    	  webview.getSettings().setJavaScriptEnabled(true);
    	  WebSettings webSettings = webview.getSettings();
    	  webSettings.setJavaScriptEnabled(true);
    	  
    	  webview.setWebViewClient(new WebViewClient(){
    		  
    	    @Override
    	    public void onPageFinished(WebView view, String url){
    	    	webview.loadUrl(centerURL);
   
    	    }
    	  });
    	  webview.loadUrl("file:///android_asset/location.html");
    	  

    	}
     
}