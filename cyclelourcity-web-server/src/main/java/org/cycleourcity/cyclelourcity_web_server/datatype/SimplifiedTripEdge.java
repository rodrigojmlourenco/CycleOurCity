package org.cycleourcity.cyclelourcity_web_server.datatype;

public class SimplifiedTripEdge {
	
	private final int streetEdgeID;
	private final String geometry;
	private boolean bicycleMode;
	
	public SimplifiedTripEdge(int streetEdgeID, String geometry, boolean bicycle){
		this.streetEdgeID = streetEdgeID;
		this.geometry = geometry;
		this.bicycleMode = bicycle;
	}

	public int getStreetEdgeID(){ return this.streetEdgeID; }
	
	public String getGeometry(){ return this.geometry; }
	
	public boolean isBicycleMode(){ return this.bicycleMode; }
}
