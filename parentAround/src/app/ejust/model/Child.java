package app.ejust.model;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import android.util.Pair;

public class Child implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// child name
	public String name;
	
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
		c.lat = 31.214306;
		c.lng = 29.945587;
		
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
	
	/**
	 * this function saves this child on a file
	 * */
	public void save(){
		// TODO: implement it
	}
}
