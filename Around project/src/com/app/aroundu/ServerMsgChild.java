package com.app.aroundu;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.app.aroundu.ServerMsgParent.ServerMessageAsyncTask;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ServerMsgChild {

	private String KEYID = "AIzaSyCGiT9NZqikYmkhVWhnX8RGjFwGx7dkKbA";
	private String parentIds = "";
	
	public ProgressDialog mDialog;
	private String reply = "";
	Toast failureToast;
	Toast successToast;
	
	Context cnt;
	public ServerMsgChild(Context cnt) {
		// send to all parents if alert msg only
//		parentIds = PreferenceManager.getDefaultSharedPreferences(cnt.getApplicationContext()).getString("id", "");
		
		this.cnt = cnt;
		
		failureToast = Toast.makeText(cnt, "Unable to send!", Toast.LENGTH_LONG);
		successToast = Toast.makeText(cnt, "sent!", Toast.LENGTH_LONG);
		
		mDialog = new ProgressDialog(cnt);
		mDialog.setMessage("sending ...");
		mDialog.setCancelable(false);
	}

	public void postMsg(final String msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClient client = new DefaultHttpClient();
					String postURL = "http://androidc2dm.herokuapp.com/" + msg;
					HttpGet post = new HttpGet(postURL);
					HttpResponse responsePOST = client.execute(post);
					HttpEntity resEntity = responsePOST.getEntity();
					String res = EntityUtils.toString(resEntity);
					if (res.contains("id")) {
						if (res.length() != 2) {
							res = res.split(":")[1];
							res = res.replace("\"", "");
							res = res.substring(0, res.length() - 1);
							gcmOneParent(res);
						}
					}
					
					if (resEntity != null) {
						Log.i("RESPONSE", EntityUtils.toString(resEntity));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			
		}).start();
	}
	
	private void gcmOneParent(String id) {
		Sender sender = new Sender(KEYID);
		Message message = new Message.Builder().delayWhileIdle(true)
				.addData("data", reply).build();
		Log.d("in server child", "gcm send one parent");
		Result result = null ;
		try {
			result = sender.send(message, id, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("debug", "result from send: " + result);
		
		
	}
	
	public void sendHelpMeMessage(final String msg) {
		new ServerMessageAsyncTask(true, cnt).execute(msg);
	}
	
	public void gcmServer(final String msg) {
		new ServerMessageAsyncTask(false, cnt).execute(msg);
	}
	
	class ServerMessageAsyncTask extends AsyncTask<String, Float, Boolean> {
		boolean showDialog = false;
		public ServerMessageAsyncTask(boolean _showDialog, Context cnt) {
			// retrieve parentsIds again as it may has been updated
			parentIds = PreferenceManager.getDefaultSharedPreferences(cnt.getApplicationContext()).getString("id", "");
			this.showDialog = _showDialog;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showDialog) {
				mDialog.show();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			final String msg = params[0];
			try {
				if(msg.contains("/")){
					String [] parms = msg.split("/");
					reply = parms[0];
					String account = parms[1];
					postMsg("/get_id?account="+account);
					
				}else{
					Log.d("idd>>>>>",""+parentIds); 
					if(parentIds.length() >= 1 && parentIds.charAt(0) == '/')
						parentIds = parentIds.substring(1);
					
					String[] ids = parentIds.split("/");
					
					Sender sender = new Sender(KEYID);
					Message message = new Message.Builder()
							.delayWhileIdle(true)
							.addData("data", msg).build();
					Log.d("in server child","gcm send");
					for (int i = 0; i < ids.length; i++) {//TODO send to parent who sent request connection
						Log.d("id", ids[i]);
						if(!ids[i].equals("")){
							Result result = sender.send(message, ids[i], 1);
							Log.d("debug", "result from send: " + result);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if (showDialog) {
				mDialog.dismiss();
				if (result)
					successToast.show();
				else
					failureToast.show();
			}
		}
		
	}
}
