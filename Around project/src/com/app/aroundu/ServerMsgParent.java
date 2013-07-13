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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ServerMsgParent {

	String res = "";
	private String childId = "";
	private String KEYID = "AIzaSyCGiT9NZqikYmkhVWhnX8RGjFwGx7dkKbA";
	
	Activity context;
	
	public ServerMsgParent(Activity cnt) {
		this.context = cnt;
		failureToast = Toast.makeText(context, "Unable to send!", Toast.LENGTH_LONG);
		successToast = Toast.makeText(context, "sent!", Toast.LENGTH_LONG);

		mDialog = new ProgressDialog(context);
		mDialog.setMessage("sending ...");
		mDialog.setCancelable(false);
	}

	public String postMsg(final String msg) {
		try {
			HttpClient client = new DefaultHttpClient();
			String postURL = "http://androidc2dm.herokuapp.com/" + msg;
			HttpGet post = new HttpGet(postURL);
			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				res = EntityUtils.toString(resEntity);
				Log.i("RESPONSE : ", res);
				if (res.contains("Error")) {
					// Toast.makeText(cont, "Network Error on child device" , Toast.LENGTH_LONG).show();
				} else if (msg.contains("first_register")) {
					if (res.length() != 2) {
						res = res.split(":")[1];
						res = res.replace("\"", "");
						res = res.substring(0, res.length() - 1);
						childId = res;
					}
					gcmServer("parent="	+ AccountMenu.prefs.getString("id", ""));
				} else if (res.contains("id")) {
					if (res.length() != 2) {
						res = res.split(":")[1];
						res = res.replace("\"", "");
						res = res.substring(0, res.length() - 1);
						childId = res;
						gcmServer("getLocation");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public void gcmServer(String msg) {
		new ServerMessageAsyncTask().execute(new String[]{msg});
	}

	
	private static ProgressDialog mDialog;
	Toast failureToast;
	Toast successToast;

	class ServerMessageAsyncTask extends AsyncTask<String, Float, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			context.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
					mDialog.show();
//				}
//			});
		}

		@Override
		protected Boolean doInBackground(String... params) {
			final String msg = params[0];
			final Sender sender = new Sender(KEYID);
			final Message message = new Message.Builder()
							.delayWhileIdle(true)
							.addData("data", msg)
							.build();
			
			Log.d("child id ", childId);
			Result result;
			try {
				result = sender.send(message, childId, 1);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			Log.d("debug", "result from send: " + result);
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

//			context.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
					mDialog.dismiss();
//				}
//			});
			
			if (result)
				successToast.show();
			else
				failureToast.show();
		}
		
	}

}
