package com.app.aroundu;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
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
import android.preference.PreferenceManager;
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
	
	class PostMessageAsyncTask extends AsyncTask<String, Float, String>{
		String msg;
		
		@Override
		protected String doInBackground(String... params) {
			msg = params[0];
			HttpClient client = new DefaultHttpClient();
			String postURL = "http://androidc2dm.herokuapp.com/" + msg;
			HttpGet post = new HttpGet(postURL);
			HttpResponse responsePOST = null;

			try {
				responsePOST = client.execute(post);
				return EntityUtils.toString(responsePOST.getEntity());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		protected void onPostExecute(String res) {
			if (res != null) {
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
					String account = PreferenceManager.getDefaultSharedPreferences(context).getString("account", "");
					Log.d("Account",":: "+account);							
					new ServerMessageAsyncTask(false).execute("parent="	+ AccountMenu.prefs.getString("id", "")+"&account="+account);
				} else if (res.contains("id")) {
					if (res.length() != 2) {
						res = res.split(":")[1];
						res = res.replace("\"", "");
						res = res.substring(0, res.length() - 1);
						childId = res;
						//AccountMenu.childs.get(AccountMenu.childPosition).id = childId;
						new ServerMessageAsyncTask(false).execute("getLocation");
					}
				}
			}
		}
	};
	
	public void postMsg(final String msg) {
		new PostMessageAsyncTask().execute(msg);
	}

	public void gcmServer(String msg) {
		new ServerMessageAsyncTask(true).execute(msg);
	}

	
	private static ProgressDialog mDialog;
	Toast failureToast;
	Toast successToast;

	class ServerMessageAsyncTask extends AsyncTask<String, Float, Boolean> {
		private boolean showNoticications = false;
		
		public ServerMessageAsyncTask(boolean _showNoticications) {
			this.showNoticications = _showNoticications;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showNoticications) {
				mDialog.show();
			}
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
			if (showNoticications) {
				mDialog.dismiss();
				
				if (result)
					successToast.show();
				else
					failureToast.show();
			}
		}
		
	}

}
