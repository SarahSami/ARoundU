package com.app;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ServerMsgChild {

	private String KEYID = "AIzaSyCGiT9NZqikYmkhVWhnX8RGjFwGx7dkKbA";
	private String parentIds = "";

	public ServerMsgChild(Context cnt) {
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

	public void gcmServer(final String msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// send to all parents if alert msg only
					parentIds = ChildActivity.prefs.getString("id", "");
					
					Log.d("idd>>>>>",""+parentIds);
					String[] ids = parentIds.split("/");
					
					Sender sender = new Sender(KEYID);
					Message message = new Message.Builder().collapseKey("1")
							.timeToLive(3).delayWhileIdle(true)
							.addData("data", msg).build();
					Log.d("in server child","gcm send");
					for (int i = 0; i < ids.length; i++) {
						Log.d("id", ids[i]);
						Result result = sender.send(message, ids[i], 1);
						Log.d("debug", "result from send: " + result);
					}
					//String tmp = "APA91bFbTd2TbUuXS5CMLLQKTzDlX3ndcWLaTkkq1jHp3qQbLpD5c_TJ9oyLVzj4Qp-PIjyvxUGYER-EGMvhnT3yUe3jAUO9-W07-Poa7Z4DaQMTYi0evM_qOMgDKMtzYsZJP5FpyroR";

					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
