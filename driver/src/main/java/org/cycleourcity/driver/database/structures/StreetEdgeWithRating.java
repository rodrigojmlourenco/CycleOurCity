package org.cycleourcity.graph_updater_standalone;

public class StreetEdgeWithRating {
	private Long streetEdgeId;
	private Long lastRating;
	
	public StreetEdgeWithRating(Long streetEdgeId, Long lastRating){
		this.streetEdgeId = streetEdgeId;
		this.lastRating = lastRating;
	}

	public Long getStreetEdgeId() {
		return streetEdgeId;
	}
	
	//é a vida :(
	public Long getUserId(){
		return streetEdgeId;
	}

	public Long getLastRating() {
		return lastRating;
	}
	
}
