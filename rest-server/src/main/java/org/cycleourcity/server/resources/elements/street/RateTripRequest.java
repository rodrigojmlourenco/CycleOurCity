package org.cycleourcity.server.resources.elements.street;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RateTripRequest {

	private int tripId;
	private int userId;
	
	private StreetEdgeRating[] ratings;
	
	public RateTripRequest(){}
	
	public RateTripRequest(int userId, int tripId, StreetEdgeRating[] ratings){
		this.userId = userId;
		this.tripId = tripId;
		
		this.ratings = ratings;
	}
	
	public int getTripId() {
		return tripId;
	}
	public void setTripId(int tripId) {
		this.tripId = tripId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public StreetEdgeRating[] getRatings() {
		return ratings;
	}

	public void setRatings(StreetEdgeRating[] ratings) {
		this.ratings = ratings;
	}
}
