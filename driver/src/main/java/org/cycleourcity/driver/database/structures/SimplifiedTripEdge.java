package org.cycleourcity.driver.database.structures;

public class SimplifiedTripEdge {
	
	private final String streetEdgeID;
	private final String geometry;
	private boolean bicycleMode;
	
	public SimplifiedTripEdge(String streetEdgeID, String geometry, boolean bicycle){
		this.streetEdgeID = streetEdgeID;
		this.geometry = geometry;
		this.bicycleMode = bicycle;
	}

	public String getStreetEdgeID(){ return this.streetEdgeID; }
	
	public String getGeometry(){ return this.geometry; }
	
	public boolean isBicycleMode(){ return this.bicycleMode; }
}
