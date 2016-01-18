package org.cycleourcity.cyclelourcity_web_server.middleware;

import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.cycleourcity.cyclelourcity_web_server.resources.elements.planner.RoutePlanRequest;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.street.GeometryRating;
import org.cycleourcity.driver.AccountManagementDriver;
import org.cycleourcity.driver.StreetEdgeManagementDriver;
import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.SimplifiedTripEdge;
import org.cycleourcity.driver.exceptions.ExpiredTokenException;
import org.cycleourcity.driver.exceptions.NonMatchingPasswordsException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnableToRegisterUserException;
import org.cycleourcity.driver.exceptions.UnknowStreetEdgeException;
import org.cycleourcity.driver.exceptions.UserRegistryException;
import org.cycleourcity.driver.impl.AccountManagementDriverImpl;
import org.cycleourcity.driver.impl.StreetEdgeManagementDriverImpl;
import org.cycleourcity.otp.OTPGraphManager;
import org.cycleourcity.otp.planner.RoutePlanner;
import org.cycleourcity.otp.planner.exceptions.InvalidPreferenceSetException;
import org.cycleourcity.otp.planner.preferences.UserPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CycleOurCityManager {

	private final OTPGraphManager				otpManager;
	private final AccountManagementDriver		accountManager;
	private final StreetEdgeManagementDriver	streetEdgeManager;
	
	
	private static CycleOurCityManager MANAGER = new CycleOurCityManager();
	
	private CycleOurCityManager(){
		this.otpManager			= new OTPGraphManager(true, "/var/otp");
		this.accountManager		= AccountManagementDriverImpl.getManager();
		this.streetEdgeManager	= StreetEdgeManagementDriverImpl.getManager();
	}
	
	public static CycleOurCityManager getInstance(){
		return MANAGER;
	}
	
	/*
	 ********************************************************
	 * A - Public Street Edge Management					*
	 ********************************************************
	 * These functions may be invoked by an anonymous user	*
	 ********************************************************
	 */
	
	//@StreetEdgesRatings.php a) Safety
	public List<GeometryRating> getSafetyRatedGeometries() 
			throws OperationNotSupportedException{
		
		throw new OperationNotSupportedException();
	}

	//@StreetEdgesRatings.php a) Elevation
	public List<GeometryRating> getElevationRatedGeometries() 
			throws OperationNotSupportedException{
		
		throw new OperationNotSupportedException();
	}
	
	//@RatedStreetEdges.php
	public List<String> getRatedStreetEdgesGeometries(){
		return streetEdgeManager.getAllDistinctGeometries();
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
	public boolean classifyStreetEdge(int userId, int tripId,
			String streetEdgeId,
			int safety, int elevation, int pavement, int rails,
			boolean last) throws UnknowStreetEdgeException{
		
		
			return streetEdgeManager.classifyStreetEdge(
					tripId,
					streetEdgeId,
					safety,
					elevation, pavement, rails,
					userId, last);
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

			/*
			 * TODO: descomentar
			 * 
			tripEdge = new SimplifiedTripEdge(
					aux.get("streetEdgeId").getAsLong(),
					aux.get("geometry").getAsString(),
					aux.get("isBicycle").getAsBoolean());
			
			edges.add(tripEdge);
			*/
		}
			
		
		try {
			streetEdgeManager.saveTrip(userID, name, edges);
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
	
	public RoutePlanner planRoute(RoutePlanRequest r) throws InvalidPreferenceSetException{
		GeoLocation from= new GeoLocation(r.getFromLat(), r.getFromLon());
		GeoLocation to	= new GeoLocation(r.getToLat(), r.getToLon());
		UserPreferences prefs = new UserPreferences(r.getSafetyPref(), r.getElevationPref(), r.getTimePref());
		return otpManager.planRoute(from, to, prefs);
	}
	
	/*
	 ********************************************************
	 * C - Account Manager Functions						*
	 ********************************************************
	 * These functions enable the creation, magement and 	*
	 * deletion of new users.
	 ********************************************************
	 */
	
	public String registerUser(String username, String email, String password, String confirmPassword) 
			throws 	UserRegistryException,
					NonMatchingPasswordsException,
					UnableToRegisterUserException,
					UnableToPerformOperation{
		
		return accountManager.registerUser(username, email, password, confirmPassword);
		
	}
	
	public boolean activateUser(String token) 
			throws ExpiredTokenException, UnableToPerformOperation{
		
		return accountManager.activateAccount(token);
	}
	
	/*
	 ********************************************************
	 * D - Authentication Function							*
	 ********************************************************
	 * These functions enable the authentication of users	*
	 ********************************************************
	 */
	
	public static void main(String[] args){
		
		CycleOurCityManager man = CycleOurCityManager.getInstance();
		
	}
}
