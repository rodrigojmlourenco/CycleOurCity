package org.cycleourcity.driver.database.structures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SimplifiedStreetEdge {

	private int factorID;
	private String geometry;
	private boolean bicycleMode;
	
	public SimplifiedStreetEdge(){}
	
	public SimplifiedStreetEdge(int factorID, String geometry){
		this.factorID = factorID;
		this.geometry = geometry;
	}
	
	public SimplifiedStreetEdge(int factorID, String geometry, boolean bicycle){
		this.factorID = factorID;
		this.geometry = geometry;
		this.bicycleMode = bicycle;
	}
	
	public int getFactorID(){ return this.factorID ;}
	
	public String getGeometry() { return this.geometry; }
	
	public void setFactorID(int factorID) {
		this.factorID = factorID;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public boolean isBicycleMode() {
		return bicycleMode;
	}

	public void setBicycleMode(boolean bicycleMode) {
		this.bicycleMode = bicycleMode;
	}

	public JsonObject getAsJson(){
		JsonObject object = new JsonObject();
		object.addProperty("IdFactor", factorID);
		object.addProperty("Geometry", geometry);
		
		return object;
	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(getAsJson());
	}
}
