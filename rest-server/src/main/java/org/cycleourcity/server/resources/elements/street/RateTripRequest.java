package org.cycleourcity.server.resources.elements.street;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RateTripRequest {

	private int tripId;
	
	private StreetEdgeRating[] ratings;
	
	public RateTripRequest(){}
	
	public RateTripRequest(int userId, int tripId, StreetEdgeRating[] ratings){
		this.tripId = tripId;
		
		this.ratings = ratings;
	}
	
	public int getTripId() {
		return tripId;
	}
	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public StreetEdgeRating[] getRatings() {
		return ratings;
	}

	public void setRatings(StreetEdgeRating[] ratings) {
		this.ratings = ratings;
	}
}
