package org.cycleourcity.otp.data;

import org.cycleourcity.driver.database.structures.StreetEdgeStatistics;

public class ComposedStreetEdgeStatistics {

	private final double streetEdgeId;
	private StreetEdgeStatistics safetyStats;
	private StreetEdgeStatistics elevationStats;
	
	public ComposedStreetEdgeStatistics(double id){
		this.streetEdgeId = id;
	}
	
	public double getStreetEdgeId(){ return this.streetEdgeId;}
	
	public void setSafetyStats(StreetEdgeStatistics stats){
		this.safetyStats = stats;
	}
	
	public StreetEdgeStatistics getSafetyStats(){ return this.safetyStats; }
	
	public StreetEdgeStatistics getElevationStats(){ return this.elevationStats; }
	
	public void setElevationStats(StreetEdgeStatistics stats){
		this.elevationStats = stats;
	}
	
}
