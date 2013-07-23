package com.app.aroundu;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;

import com.app.aroundu.ChildInfoActivity;

public class Child implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public String name;
	public String mail;
	public String id;
	public String number;
	public boolean activationFlag = true;
	public boolean request = true;
	// current location information
	public double lng;
	public double lat;
	
	
	// routes defined for this child
	public static class Route implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Vector<Pair<String, String>> steps;
		public String name;
		public int greenZone;
		
		public Route(Vector<Pair<String, String>> steps, String name) {
			this.steps = steps;
			this.name = name;
		}
	}
	
	
	public Vector<Route> routes = new Vector<Route>();

	
	/**
	 * This function should return child specified by its id
	 * 
	 * @param	childId	the id of the child
	 * @return	the child specified by this id
	 * */
	public static Child findChildById(int childId){
		// TODO: needs to be implemented
		Child c = new Child();
		return c;
	}
	
	public  void getId() {
		Log.d("in get id","in id");
		if(!mail.contains("@gmail"))
			mail+="@gmail.com";
		AccountMenu.server.postMsg("/get_id?account="+mail);
		
	}

	/**
	 * this function saves this child on a file
	 * */
	public void save(){
		// TODO: implement it
	}
	
	public static void saveChildIcon(Context context, String name, Bitmap icon) throws IOException{
		FileOutputStream out = context.openFileOutput(name + "_icon", Context.MODE_PRIVATE);
		
		icon.compress(Bitmap.CompressFormat.JPEG, 100, out);
		
		out.flush();
		out.close();
	}
	
	public static Bitmap loadChildIconBitmap(Context context, String name){
		try{
			FileInputStream iconStream = context.openFileInput(name + "_icon");
			return BitmapFactory.decodeStream(iconStream);
		}catch (FileNotFoundException e) {
			Drawable iconDrawable = context.getResources().getDrawable(R.drawable.contact);
			return ((BitmapDrawable)iconDrawable).getBitmap();
		}
	}
}
