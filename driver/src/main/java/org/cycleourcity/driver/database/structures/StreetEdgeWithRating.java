package org.cycleourcity.driver.database.structures;

public class StreetEdgeWithRating {
	private double streetEdgeId;
	private long lastRating;
	
	public StreetEdgeWithRating(double streetEdgeId, Long lastRating){
		this.streetEdgeId = streetEdgeId;
		this.lastRating = lastRating;
	}

	public double getStreetEdgeId() {
		return streetEdgeId;
	}
	
	public double getUserId(){
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
