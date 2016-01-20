package org.cycleourcity.server.resources.elements.trips;

import java.util.List;

import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;
import org.cycleourcity.driver.database.structures.SimplifiedTripEdge;

public class DetailedTripResponse {

	private GeoLocation from;
	private GeoLocation to;
	private SimplifiedTripEdge[] streetEdges;
	
	public DetailedTripResponse(){}
	
	public DetailedTripResponse(SimplifiedTripEdge[] streetEdges, GeoLocation from, GeoLocation to){
		this.from 	= from;
		this.to		= to;
		
		this.streetEdges = streetEdges;
	}
	
	public DetailedTripResponse(List<SimplifiedTripEdge> streetEdges, GeoLocation from, GeoLocation to){
		this.from 	= from;
		this.to		= to;
		
		this.streetEdges = new SimplifiedTripEdge[streetEdges.size()];
		streetEdges.toArray(this.streetEdges);
	}

	public GeoLocation getFrom() {
		return from;
	}

	public void setFrom(GeoLocation from) {
		this.from = from;
	}

	public GeoLocation getTo() {
		return to;
	}

	public void setTo(GeoLocation to) {
		this.to = to;
	}

	public SimplifiedTripEdge[] getStreetEdges() {
		return streetEdges;
	}

	public void setStreetEdges(SimplifiedTripEdge[] streetEdges) {
		this.streetEdges = streetEdges;
	}
}
