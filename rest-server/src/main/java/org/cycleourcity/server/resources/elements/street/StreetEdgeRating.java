package org.cycleourcity.server.resources.elements.street;

public class StreetEdgeRating {

	private String streetEdgeId;
	private int 	elevationRate 	= -1,
					safetyRate 		= -1,
					pavementRate 	= -1,
					railsRate		= -1;
	
	public StreetEdgeRating(){}
	
	public StreetEdgeRating(String streetEdgeId, 
			int elevation, int safety, int pavement, int rails){
		
		this.streetEdgeId = streetEdgeId;
		
		this.safetyRate 	=	safety;
		this.pavementRate	= pavement;
		this.railsRate		= rails;
		this.elevationRate	= elevation;
	}

	public String getStreetEdgeId() {
		return streetEdgeId;
	}

	public void setStreetEdgeId(String streetEdgeId) {
		this.streetEdgeId = streetEdgeId;
	}

	public int getElevationRate() {
		return elevationRate;
	}

	public void setElevationRate(int elevationRate) {
		this.elevationRate = elevationRate;
	}

	public int getSafetyRate() {
		return safetyRate;
	}

	public void setSafetyRate(int safetyRate) {
		this.safetyRate = safetyRate;
	}

	public int getPavementRate() {
		return pavementRate;
	}

	public void setPavementRate(int pavementRate) {
		this.pavementRate = pavementRate;
	}

	public int getRailsRate() {
		return railsRate;
	}

	public void setRailsRate(int railsRate) {
		this.railsRate = railsRate;
	}
}
