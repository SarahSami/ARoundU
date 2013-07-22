package com.app.aroundu;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
	Toast failureToast;
	Toast successToast;

	public ServerMsgChild(Context cnt) {
		// send to all parents if alert msg only
		parentIds = PreferenceManager.getDefaultSharedPreferences(cnt.getApplicationContext()).getString("id", "");
		
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
					if (resEntity != null) {
						Log.i("RESPONSE", EntityUtils.toString(resEntity));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void sendHelpMeMessage(final String msg) {
		new ServerMessageAsyncTask(true).execute(msg);
	}
	
	public void gcmServer(final String msg) {
		new ServerMessageAsyncTask(false).execute(msg);
	}
	
	class ServerMessageAsyncTask extends AsyncTask<String, Float, Boolean> {
		boolean showDialog = false;
		public ServerMessageAsyncTask(boolean _showDialog) {
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
				Log.d("idd>>>>>",""+parentIds);  
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
