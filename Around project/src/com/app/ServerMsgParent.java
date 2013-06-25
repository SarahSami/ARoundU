package com.app;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import android.content.Context;
import android.util.Log;





public class ServerMsgParent {
	  
    String res="";	  
	private String childId="";
	private  String KEYID="AIzaSyCGiT9NZqikYmkhVWhnX8RGjFwGx7dkKbA";
	
	public ServerMsgParent(Context cnt ){
		
	}
	
	   
  
	   public String postMsg(final String msg){
		   
		   new Thread(new Runnable(){

				@Override
				public void run() {
		   try {
		       HttpClient client = new DefaultHttpClient();  
		       String postURL = "http://androidc2dm.herokuapp.com/"+msg;
		       HttpGet post = new HttpGet(postURL); 
		       HttpResponse responsePOST = client.execute(post);  
		       HttpEntity resEntity = responsePOST.getEntity(); 
		       if (resEntity != null) {    
		    	    res = EntityUtils.toString(resEntity);
		               Log.i("RESPONSE : ",res);
		               if(res.contains("Error")){
		            	   //Toast.makeText(cont, "Network Error on child device" , Toast.LENGTH_LONG).show();
		               }else if(msg.contains("first_register")){
		            	   if(res.length() != 2){
			       				res = res.split(":")[1];
			       				res = res.replace("\"", "");
			       				res = res.substring(0, res.length()-1);
			       				childId = res;
			       			}
		            	   gcmServer("parent="+AccountMenu.prefs.getString("id",""));
		               }
		               else if(res.contains("id")){
			       			if(res.length() != 2){
			       				res = res.split(":")[1];
			       				res = res.replace("\"", "");
			       				res = res.substring(0, res.length()-1);
			       				childId = res;
			       				gcmServer("getLocation");
			       			}
		               }
		           }
		   } catch (Exception e) {
		       e.printStackTrace();
		   }
				}
		   }).start();
		   return res;
	   }
	   

	   
	   public void gcmServer(String msg){
		   try {

				Sender sender = new Sender(KEYID);
				Message message = new Message.Builder()
						.collapseKey("1")
						.timeToLive(3)
						.delayWhileIdle(true)
						.addData("data",msg)
						.build();
				Log.d("child id ", childId);
				 Result result = sender.send(message,childId,1);
				Log.d("debug","result from send: "+result);
					
			} catch (Exception e) {
				e.printStackTrace();
			}

	   }

}
