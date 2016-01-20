package org.cycleourcity.server.resources.elements.trips;

import javax.xml.bind.annotation.XmlRootElement;

import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;

@XmlRootElement
public class TripRegistryRequest {

	private String tripName;
	private Long user;
	private SimplifiedStreetEdge[] streetEdges;
	
	public TripRegistryRequest(){}
	
	public TripRegistryRequest(Long user, String tripName, SimplifiedStreetEdge[] edges){
		this.tripName = tripName;
		this.user = user;
		this.streetEdges = edges;
	}

	public String getTripName() {
		return tripName;
	}

	public void setTripName(String tripName) {
		this.tripName = tripName;
	}

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public SimplifiedStreetEdge[] getStreetEdges() {
		return streetEdges;
	}

	public void setStreetEdges(SimplifiedStreetEdge[] streetEdges) {
		this.streetEdges = streetEdges;
	}
}
