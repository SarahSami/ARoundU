package com.app;


import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class RouteService extends IntentService implements LocationListener{
	
	   public static boolean work = false;
	   public static String route = "";
	   public static LinkedList <String> points   = new LinkedList<String>();;
	   public static  LinkedList <String> dangerousRoutes = new LinkedList<String>() ;
	   public static int greenZone = 20;
	   private static int difference = 0;
	   public static double minLat = 0;
	   public static double minLng = 0;
	   public static boolean arrived = false;
	   public  static double lat  = 0;
	   public  static double lng  = 0; 
	   public static Context cntx;
	   private boolean done = false;
	   private LocationManager locationManager;
	   public RouteService() {
		super("intent service");
		locationManager = (LocationManager) cntx.getSystemService(cntx.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		findMyLocation();
	   }


    public void findMyLocation(){
	    	 
		     Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		     if(location != null){
			     lat = location.getLatitude();
			     lng = location.getLongitude();    
		     }
		     else
		    	 ChildActivity.server.gcmServer("location is unknown:"+ChildActivity.account);
    	
	    
    }
		
	private static  float distFrom(double lat1, double lat2, double lng1, double lng2) {
		    double earthRadius = 3958.75;
		    double dLat = Math.toRadians(lat2-lat1);
		    double dLng = Math.toRadians(lng2-lng1);
		    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
		               Math.sin(dLng/2) * Math.sin(dLng/2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    double dist = earthRadius * c;

		    int meterConversion = 1609;

		    return new Float(dist * meterConversion).floatValue();
		  }
	

	
 

	public static boolean gotLost(){
		Float minA;
		String [] tmp = points.get(0).split(",");
		minA = distFrom(Float.parseFloat(tmp[0]),lat,Float.parseFloat(tmp[1]),lng);	
		int min = 0;
		for(int i=1;i<points.size();i++){
			tmp = points.get(i).split(",");
			if(distFrom(Float.parseFloat(tmp[0]),lat,Float.parseFloat(tmp[1]),lng) < minA){
				minA = distFrom(Float.parseFloat(tmp[0]),lat,Float.parseFloat(tmp[1]),lng);
				min = i;
			}
			
		}
		String [] ms = points.get(min).split(",");
		minLat = Double.parseDouble(ms[0]);
		minLng = Double.parseDouble(ms[1]);
		if(minA > greenZone){
			difference = (int) (minA - greenZone);
			return true;
		}
	    if(minA <= 50){
			arrived = true;
			ChildActivity.server.gcmServer("arrived at destination:"+ChildActivity.account);
		}
		return false;
	}
	
//	public static String getFromLocation(double lat, double lon) {
//        String urlStr = "http://maps.google.com/maps/geo?q=" + lat + "," + lon + "&output=json&sensor=false";
//                String response = "";
//                List<Address> results = new ArrayList<Address>();
//                HttpClient client = new DefaultHttpClient();
//                try {
//                        HttpResponse hr = client.execute(new HttpGet(urlStr));
//                        HttpEntity entity = hr.getEntity();
//                        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
//                        String buff = null;
//                        while ((buff = br.readLine()) != null)
//                                response += buff;
//                } catch (IOException e) {
//                        e.printStackTrace();
//                }
//
//                JSONArray responseArray = null;
//                try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        responseArray = jsonObject.getJSONArray("Placemark");
//                } catch (JSONException e) {
//                        return "";
//                }
//                
//                        Address addy = new Address(Locale.getDefault());
//
//                        try {
//                                JSONObject jsl = responseArray.getJSONObject(0);
//                                String addressLine = jsl.getString("address");
//                                if(addressLine.contains(","))
//                                        addressLine = addressLine.split(",")[0];
//                                addy.setAddressLine(0, addressLine);
//                                
//                                jsl = jsl.getJSONObject("AddressDetails").getJSONObject("Country");
//                                addy.setCountryName(jsl.getString("CountryName"));
//
//                                jsl = jsl.getJSONObject("AdministrativeArea");
//                                if(jsl != null)
//                                	addy.setAdminArea(jsl.getString("AdministrativeAreaName"));
//
//                                jsl = jsl.getJSONObject("SubAdministrativeArea");
//                                if(jsl != null)
//                                	addy.setSubAdminArea(jsl.getString("SubAdministrativeAreaName"));
//
//                                jsl = jsl.getJSONObject("Locality");
//                                if(jsl != null)
//                                	addy.setLocality(jsl.getString("LocalityName"));
//
//
//
//                        } catch (JSONException e) {
//                                e.printStackTrace();
//                        }
//
//                        results.add(addy);
//                return results.get(0).getAddressLine(0);
//        }
	
//	private void checkDanger(){
//				String ad = getFromLocation(lat, lng);
//				String sent = "";
//				int i =0;
//				while(i<dangerousRoutes.size()){
//					if(ad.contains(dangerousRoutes.get(i))){
//						sent = dangerousRoutes.get(i).replace(" ", "+");
//						ChildActivity.server.postMsg("set_msg?msg=dangerous+"+sent );
//						Toast.makeText(RouteService.this, "dangerous route"+sent, Toast.LENGTH_LONG).show();
//					}
//					i++;
//				}
//		}
		
	

	public void onLocationChanged(Location location) {
		   lat = location.getLatitude();
	       lng = location.getLongitude();
		
	}

	public void onProviderDisabled(String provider) {
		
	}

	public void onProviderEnabled(String provider) {
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	  

	public void notifyLost(){
		String msg =  "You are out of green zone.";
		String svcName = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager)cntx.getSystemService(svcName);        
        int icon = R.drawable.pch;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon,msg, when);
        notification.contentView = new RemoteViews(cntx.getPackageName(), R.layout.notification);
		notification.flags |= Notification.FLAG_AUTO_CANCEL; 
		notification.defaults = Notification.DEFAULT_SOUND;
		Intent intnt = new Intent(cntx, GCMIntentService.class);
        PendingIntent launchIntent = PendingIntent.getActivity(cntx.getApplicationContext(), 0, intnt, 0);
        notification.contentIntent = launchIntent;
        notification.contentView.setTextViewText(R.id.status_text,msg);
        int notificationRef = 1;
        notificationManager.notify(notificationRef, notification);
	}
	
	

	@Override
	protected void onHandleIntent(Intent intent) {
		
		if(!done){

			route = ChildActivity.prefs.getString("route","");
			String [] tmp = route.split("/");
			for(int j=0;j<tmp.length;j++){
				if(!tmp[j].contains("/") && !tmp[j].equals("")){
					points.add(tmp[j]);
				}
			}
		
		done = true;
		}
       work = true;
       if(points.size() == 0)
    	   Toast.makeText(RouteService.this, "No route specified", Toast.LENGTH_LONG).show();
       else{
    	   
    	   new Timer().scheduleAtFixedRate(new TimerTask() {
    	        public void run() {
    	    		if (work && !arrived){
    	    			findMyLocation();
    	    			Log.d("workiiiiiinnnnggg","innnnnnn"+lat+","+lng);
    	    			if(gotLost()){
    	    				ChildActivity.server.gcmServer("is out of green zone by "+difference+" meters:"+ChildActivity.account);
    	    				notifyLost();
    	    				
    	    			}
    	    		}else if(arrived){
    	    			Log.d("arrived","doneeee>>>");
    	    			 work = false;
    	    			 Intent intn = new Intent();
    	    		     intn.setAction(ChildActivity.ACTION);  
    	    		     intn.putExtra("stop", "stop");
    	    			 sendBroadcast(intn);
    	    			 cancel();
    	    		}
    	        }}, 60,60*1000*5 );
    	   
		
    }
	}




	


}
