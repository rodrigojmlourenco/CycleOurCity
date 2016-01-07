package org.cycleourcity.driver.database.structures;

public class SimplifiedTripEdge {
	
	private final double streetEdgeID;
	private final String geometry;
	private boolean bicycleMode;
	
	public SimplifiedTripEdge(double streetEdgeID, String geometry, boolean bicycle){
		this.streetEdgeID = streetEdgeID;
		this.geometry = geometry;
		this.bicycleMode = bicycle;
	}

	public double getStreetEdgeID(){ return this.streetEdgeID; }
	
	public String getGeometry(){ return this.geometry; }
	
	public boolean isBicycleMode(){ return this.bicycleMode; }
}
