package org.cycleourcity.driver.database.structures;

public class CustomStreetEdge {

	private double Id;
	private String name;
	private GeoLocation from, to;
	
	public CustomStreetEdge(double id, String name, GeoLocation from, GeoLocation to){
		this.Id = id;
		this.name = name;
		this.from = from;
		this.to= to;
	}
	
	public double getId(){ return this.Id; }
	
	public String getName(){ return this.name; }
	
	public GeoLocation getFrom() { return this.from; }
	
	public GeoLocation getTo() { return this.to; }
}
