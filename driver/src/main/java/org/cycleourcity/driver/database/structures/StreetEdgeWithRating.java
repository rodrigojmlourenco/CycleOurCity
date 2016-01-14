package org.cycleourcity.driver.database.structures;

public class StreetEdgeWithRating {
	
	private String streetEdgeId;
	private long lastRating;
	
	public StreetEdgeWithRating(String streetEdgeId, Long lastRating){
		this.streetEdgeId = streetEdgeId;
		this.lastRating = lastRating;
	}

	public String getStreetEdgeId() {
		return streetEdgeId;
	}
	
	public String getUserId(){
		return streetEdgeId;
	}

	public Long getLastRating() {
		return lastRating;
	}
	
	@Override
	public boolean equals(Object obj) {
		return streetEdgeId == ((StreetEdgeWithRating)obj).getStreetEdgeId();
	}
	
}
