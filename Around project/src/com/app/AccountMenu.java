package com.app;

import static com.app.CommonUtilities.SENDER_ID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AccountMenu extends Activity{
	public static Child child;
	public static ServerMsgParent server;
	public static LinkedList<Child> onlineChilds;
	public static LinkedList<Child> childs = new LinkedList<Child>();
	public static SharedPreferences prefs;
	public static Hashtable<String, String> map = new Hashtable<String,String>();
	private  ListView listView;
	private String []users;
	private Adapter adapterCH;
	private LazyAdapter adapter;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
        if (!hasInternetConnection()) {
        	 showDialog();
        }else{
        	setContentView(R.layout.account);
        	prefs = WizardActivity.prefs;
        	onlineChilds = new LinkedList<Child>();
        	server = new ServerMsgParent(this);
        	GCMRegistrar.checkDevice(this);
     		GCMRegistrar.checkManifest(this);
     		String regId = GCMRegistrar.getRegistrationId(this);
    		if (regId.equals("")) {
     			GCMRegistrar.register(this, SENDER_ID);
     		} else{
     			Log.d("done ","get reg id succesfully");
     		}    
           
            
	        listView = (ListView) findViewById(R.id.listview);
	        listView.setOnItemClickListener(new OnItemClickListener() {
	        	@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) {
			
					Iterator itr = map.keySet().iterator();
					while(itr.hasNext()){
						String k = (String)itr.next();
						
						if((users[position]).compareTo(map.get(k) )== 0){
							Log.d("user pos",""+position +" k "+k);
							child = childs.get(position);	
						}
					}
					start();
				}
	        });
	       
	        try {
				loadData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
    }
    
    /**
     * returns true if the device has internet connection
     * */
    private boolean hasInternetConnection(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }
    
    private void showDialog() {
    	AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		//alertbox.setIcon(R.drawable.pch);
		alertbox.setTitle("Network Error");
		alertbox.setMessage("This application requires a working data connection");
		alertbox.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				System.exit(0);
			}
		});

		alertbox.show();
		
	}

	private void loadData() throws IOException{
		Gson gson = new Gson();
		users = new String[0];
		String [] jsons = null;
		childs.clear();
		if(prefs.contains("child")){
			
			String chs = prefs.getString("child", "");
			if(chs.charAt(0) == '/')
				chs = chs.substring(1);
			
			if(chs.contains("/")){
				jsons = chs.split("/");
				users = new String[jsons.length];
			}
			else if(chs.compareTo("") != 0){
				users = new String[1];
				jsons = new String[1];
				jsons[0] = chs;
			}
			
			for(int i=0;i<users.length;i++){
			    Child c = gson.fromJson(jsons[i], Child.class);
			    childs.add(c);
			    users[i] = c.name;
	        	map.put(c.mail, c.name);
			}
	    
		}
    	//FileInputStream fip = openFileInput("users");
        //InputStreamReader isr = new InputStreamReader ( fip ) ;
        //BufferedReader buffreader = new BufferedReader ( isr ) ;
        //String readString = buffreader.readLine();
        
        //if(readString != null){
	        //String [] contacts = readString.split("/");
	        //users = new String[1];
	        //String [] parts; 
	       // for(int i=0;i<contacts.length;i++){
	        //	parts = contacts[i].split(",");
	        //	Log.d("contact",contacts[i]);
	        	//users[0] = c.name;
	        	//map.put(c.mail, c.name);	        	
	       // }
       // }
        adapter = new LazyAdapter(this, users);
        adapterCH = new Adapter(this, users);
        listView.setAdapter(adapter);
        
	}
	
	private void start(){
		
		Intent myIntent = new Intent(this, ChildInfoActivity.class);
		startActivity(myIntent);
	}
	
	public void addChild(View v){
		Intent myIntent = new Intent(this, AddActivity.class);
		startActivity(myIntent);
	}
	
	public void removeChild(View v){
		if(users == null)
			return;
		
		if(listView.getAdapter() == adapterCH)
			listView.setAdapter(adapter);
		else
			listView.setAdapter(adapterCH);

		if(adapterCH.checks.size() > 0){
	    	try {
				saveToFile();
				loadData();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	adapterCH.checks.clear();
	    }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// load data again to get any new updates
		try {
			if(hasInternetConnection())
				loadData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void saveToFile() throws IOException{
		//FileOutputStream fos = openFileOutput("users", 0);
    	//OutputStreamWriter isw = new OutputStreamWriter ( fos ) ;
       // BufferedWriter buffw = new BufferedWriter( isw ) ;
		prefs.edit().remove("child").commit();
		//LinkedList<Child> tmplist = childs;
		String tmp = "";
		Gson gson = new Gson();
		//childs.clear();
		for(int i=0;i<users.length;i++){ 
    		Iterator itr = map.keySet().iterator();
    		if(!adapterCH.checks.contains(i)){
				while(itr.hasNext()){
					String k = (String)itr.next();
					//String[] params;
					if((users[i]).compareTo(map.get(k)) == 0){
						String json = gson.toJson(childs.get(i));
						tmp = tmp+"/"+json;
//							 params = (users[i]).split(" ");
//							 if(params.length > 1)
//								 buffw.write(k+","+params[0]+","+params[1]);
//							 else
//								 buffw.write(k+","+params[0]+","+" ");
//						     buffw.write("/");
						}
					}
			}
    	}
		Log.d("saved after remove is ",tmp);
		if(tmp.compareTo("") != 0)
			prefs.edit().putString("child",tmp).commit();
		//buffw.close();
	}
}