package org.cycleourcity.cyclelourcity_web_server.middleware;

import java.util.ArrayList;
import java.util.List;

import org.cycleourcity.driver.AccountManagementDriver;
import org.cycleourcity.driver.StreetEdgeManagementDriver;
import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;
import org.cycleourcity.driver.database.structures.SimplifiedTripEdge;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnknowStreetEdgeException;
import org.cycleourcity.driver.impl.AccountManagementDriverImpl;
import org.cycleourcity.driver.impl.StreetEdgeManagementDriverImpl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CycleOurCityManager {

	private final StreetEdgeManagementDriver 		STREET_MANAGER;
	private final AccountManagementDriver	 	ACCOUNT_MANAGER;
	
	
	private CycleOurCityManager(){
		this.ACCOUNT_MANAGER= AccountManagementDriverImpl.getManager();
		this.STREET_MANAGER	= StreetEdgeManagementDriverImpl.getManager();
		
	}
	
	/*
	 ********************************************************
	 * A - Public Street Edge Management					*
	 ********************************************************
	 * These functions may be invoked by an anonymous user	*
	 ********************************************************
	 */
	
	//@StreetEdgesRatings.php
	public JsonObject getStreetEdgesRating(){
		
		JsonObject streetEdgesRatings = new JsonObject();
		
		List<SimplifiedStreetEdge> elevationEdges 	= STREET_MANAGER.getStreetEdgesWithElevation();
		List<SimplifiedStreetEdge> safetyEdges		= STREET_MANAGER.getStreetEdgesWithSafety();
		
		JsonArray elevationEdgesAsJson = new JsonArray();
		JsonArray safetyEdgesAsJson = new JsonArray();
		
		for(SimplifiedStreetEdge e : elevationEdges)
			elevationEdgesAsJson.add(e.toString());
		
		for(SimplifiedStreetEdge e : safetyEdges)
			safetyEdgesAsJson.add(e.toString());
		
		streetEdgesRatings.add("safetyRatings", safetyEdgesAsJson);
		streetEdgesRatings.add("elevationRatings", elevationEdgesAsJson);
		
		return streetEdgesRatings;
	}
	
	//@RatedStreetEdges.php
	public JsonObject getRatedStreetEdges(){
		JsonObject ratedStreetEdges = new JsonObject();
		
		JsonArray geometriesAsJson = new JsonArray();
		List<String> geometries = STREET_MANAGER.getAllDistinctGeometries();
		
		JsonObject tmp;
		for(String s : geometries){
			tmp = new JsonObject();
			tmp.addProperty("Geometry", s);
			geometriesAsJson.add(tmp);
		}
		
		ratedStreetEdges.add("ratedStreetEdges", geometriesAsJson);
		
		return ratedStreetEdges;
	}
	
	
	/*
	 ********************************************************
	 * B - Secure Street Edge Management					*
	 ********************************************************
	 * These functions may only be invoked by an			* 
	 * authenticated user									*
	 ********************************************************
	 * a) Street Edge Classification						*
	 * b) Trip Management									*
	 ********************************************************
	 */
	
	//@InsertUserFeedback.php
	public JsonObject classifyStreetEdge(JsonObject request, boolean last){
		
		JsonObject response = new JsonObject();
		
		long userId 	= request.get("userId").getAsLong();
		long tripId 	= request.get("tripId").getAsLong();
		long streetId 	= request.get("streetEdgeId").getAsLong();
		
		int elevation 	= request.get("elevation").getAsInt();
		int safety 		= request.get("safety").getAsInt();
		int rails 		= request.get("rails").getAsInt();
		int pavement 	= request.get("pavement").getAsInt();
		
		try {
			
			STREET_MANAGER.classifyStreetEdge(
					tripId,
					streetId,
					safety,
					elevation, pavement, rails,
					userId, last);
			
			response.addProperty("success", true);
			
			
		} catch (UnknowStreetEdgeException e) {
			e.printStackTrace();
			
			response.addProperty("success", false);
			response.addProperty("error", e.getMessage());
		}
		
		return response;
	}
	
	//@SaveTrip.php
	public JsonObject saveTrip(JsonObject request){
		
		JsonObject response = new JsonObject();
		long userID = request.get("user").getAsInt();
		String name = request.get("tripName").getAsString();
		JsonArray streetEdges = request.getAsJsonArray("tripStreetEdges");
		
		List<SimplifiedTripEdge> edges = new ArrayList<SimplifiedTripEdge>();
		
		JsonObject aux;
		SimplifiedTripEdge tripEdge;
		
		for(JsonElement e : streetEdges){
			aux = (JsonObject) e;
			
			tripEdge = new SimplifiedTripEdge(
					aux.get("streetEdgeId").getAsLong(),
					aux.get("geometry").getAsString(),
					aux.get("isBicycle").getAsBoolean());
			
			edges.add(tripEdge);
		}
			
		
		try {
			STREET_MANAGER.saveTrip(userID, name, edges);
		} catch (UnableToPerformOperation e1) {
			e1.printStackTrace();
			response.addProperty("success", false);
			response.addProperty("error", e1.getMessage());
		}
		
		return null;
	}
	
	//@NEW
	public JsonObject planTrip(JsonObject request){
		
		return null;
	}
	
	/*
	 ********************************************************
	 * C - Account Manager Functions						*
	 ********************************************************
	 * These functions enable the creation, magement and 	*
	 * deletion of new users.
	 ********************************************************
	 */
	
	
	/*
	 ********************************************************
	 * D - Authentication Function							*
	 ********************************************************
	 * These functions enable the authentication of users	*
	 ********************************************************
	 */
}
