package org.cycleourcity.cyclelourcity_web_server.resources.elements.trips;

import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;

public class DetailedTripResponse {

	private GeoLocation from;
	private GeoLocation to;
	private SimplifiedStreetEdge[] streetEdges;
	
	public DetailedTripResponse(){}
	
	public DetailedTripResponse(SimplifiedStreetEdge[] streetEdges, GeoLocation from, GeoLocation to){
		this.from 	= from;
		this.to		= to;
		
		this.streetEdges = streetEdges;
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

	public SimplifiedStreetEdge[] getStreetEdges() {
		return streetEdges;
	}

	public void setStreetEdges(SimplifiedStreetEdge[] streetEdges) {
		this.streetEdges = streetEdges;
	}
}
