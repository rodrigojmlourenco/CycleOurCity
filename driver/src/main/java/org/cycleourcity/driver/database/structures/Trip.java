package org.cycleourcity.driver.database.structures;

import java.util.List;

public class Trip {

	private final GeoLocation to;
	private final GeoLocation from;
	private final List<SimplifiedTripEdge> streetEdges;
	
	
	public Trip(GeoLocation from, GeoLocation to, List<SimplifiedTripEdge> streetEdges){
		this.to = to;
		this.from = from;
		this.streetEdges = streetEdges;
	}
	
	public GeoLocation getFromLocation(){ return this.from; }
	
	public GeoLocation getToLocation(){ return this.to; }
	
	public List<SimplifiedTripEdge> getTripStreetEdges(){ return this.streetEdges; }
	
}
