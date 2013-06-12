package app.ejust.model;

import java.io.Serializable;

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
		return c;
	}
}
