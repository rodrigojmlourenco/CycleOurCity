package org.cycleourcity.cyclelourcity_web_server.middleware;

import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedStreetEdge;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.AccountManagementLayer;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.AccountManager;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.StreetEdgeManagement;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.StreetEdgeManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CycleOurCityManager {

	private final StreetEdgeManagement 		STREET_MANAGER;
	private final AccountManagementLayer 	ACCOUNT_MANAGER;
	
	
	private CycleOurCityManager(){
		this.ACCOUNT_MANAGER= AccountManager.getManager();
		this.STREET_MANAGER	= StreetEdgeManager.getManager();
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
	 */
	
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
