package org.cycleourcity.driver.database.structures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SimplifiedStreetEdge {

	private final int factorID;
	private final String geometry;
	
	public SimplifiedStreetEdge(int factorID, String geometry){
		this.factorID = factorID;
		this.geometry = geometry;
	}
	
	public int getFactorID(){ return this.factorID ;}
	
	public String getGeometry() { return this.geometry; }
	
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
