package com.app;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import android.util.Log;
import android.util.Pair;
import com.app.ChildInfoActivity;

public class Child implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// child name
	public String name;
	public String mail;
	public String id;
	public String number;
	// current location information
	public double lng;
	public double lat;
	
	
	// routes defined for this child
	public static class Route implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Vector<Pair<Double, Double>> steps;
		public String name;
		public int greenZone;
//		public double startLat;
//		public double endLat;
//		public double startLng;
//		public double endLng;
		
		public Route(Vector<Pair<Double, Double>> steps, String name) {
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
		c.name = "Boody";
		c.mail = "sarahsami198";
		c.lat = 31.214306;
		c.lng = 29.945587;
		getId(c);
		// build redundant routes
		Vector<Pair<Double, Double>> route1 = new Vector<Pair<Double,Double>>();
		route1.add(new Pair<Double, Double>(31.214306, 29.945587));
		route1.add(new Pair<Double, Double>(31.217288, 29.94637));
		
		Vector<Pair<Double, Double>> route2 = new Vector<Pair<Double,Double>>();
		route2.add(new Pair<Double, Double>(31.214306, 29.945587));
		route2.add(new Pair<Double, Double>(31.213013, 29.943312));
		
		c.routes.add(new Route(route1, "Way to Club"));
		c.routes.add(new Route(route2, "Way to Home"));
		return c;
	}
	
	private static void getId(Child c) {
		String mail = c.mail;
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
}