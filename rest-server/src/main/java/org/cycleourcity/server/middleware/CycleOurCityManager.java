/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.cycleourcity.server.middleware;

import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.cycleourcity.driver.AccountManagementDriver;
import org.cycleourcity.driver.StreetEdgeManagementDriver;
import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.SimplifiedTrip;
import org.cycleourcity.driver.database.structures.SimplifiedTripEdge;
import org.cycleourcity.driver.database.structures.Trip;
import org.cycleourcity.driver.exceptions.ExpiredTokenException;
import org.cycleourcity.driver.exceptions.NonMatchingPasswordsException;
import org.cycleourcity.driver.exceptions.StreetEdgeNotFoundException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnableToRegisterUserException;
import org.cycleourcity.driver.exceptions.UnknowStreetEdgeException;
import org.cycleourcity.driver.exceptions.UnknownUserException;
import org.cycleourcity.driver.exceptions.UserRegistryException;
import org.cycleourcity.driver.impl.AccountManagementDriverImpl;
import org.cycleourcity.driver.impl.StreetEdgeManagementDriverImpl;
import org.cycleourcity.otp.OTPGraphManager;
import org.cycleourcity.otp.planner.RoutePlanner;
import org.cycleourcity.otp.planner.exceptions.InvalidPreferenceSetException;
import org.cycleourcity.otp.planner.preferences.UserPreferences;
import org.cycleourcity.server.resources.elements.planner.RoutePlanRequest;
import org.cycleourcity.server.resources.elements.street.GeometryRating;
import org.opentripplanner.api.model.Itinerary;
import org.opentripplanner.api.model.Leg;
import org.opentripplanner.api.model.TripPlan;
import org.opentripplanner.api.model.WalkStep;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CycleOurCityManager {

	private static Logger LOG = LoggerFactory.getLogger(CycleOurCityManager.class);
	
	private OTPGraphManager				otpManager;
	private AccountManagementDriver		accountManager;
	private StreetEdgeManagementDriver	streetEdgeManager;
	
	
	private static CycleOurCityManager MANAGER = new CycleOurCityManager();
	
	private final String BASE_DIR = System.getenv("HOME")+"/otp";
	
	private CycleOurCityManager(){
		
		try{
			this.otpManager	= new OTPGraphManager(true, BASE_DIR);
		}catch(Exception e){
			LOG.error(e.getMessage()+" - Creating a new graph from scratch");
			try{
				this.otpManager	= new OTPGraphManager(BASE_DIR);
			}catch(NullPointerException e2){
				LOG.error("No osm files found at '"+BASE_DIR+"'");
				throw new RuntimeException(e2);
			}
		}
		
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
	public List<GeometryRating> getSafetyRatedGeometries(){
		
		throw new UnsupportedOperationException();
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
	public int getUserId(String user) throws UnknownUserException, UnableToPerformOperation{
		return accountManager.getUserID(user);
	}
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
	
	
	public RoutePlanner planRoute(RoutePlanRequest r) throws InvalidPreferenceSetException{
		
		GeoLocation from= new GeoLocation(r.getFromLat(), r.getFromLon());
		GeoLocation to	= new GeoLocation(r.getToLat(), r.getToLon());
		UserPreferences prefs = new UserPreferences(r.getSafetyPref(), r.getElevationPref(), r.getTimePref());
		
		return otpManager.planRoute(from, to, prefs);
	}
	
	public void saveTrip(String user, TripPlan plan) 
			throws UnknownUserException, UnableToPerformOperation{
		
		int id = accountManager.getUserID(user);
		
		for(Itinerary itinerary : plan.itinerary){
		
		String name;
			
			
			name = itinerary.legs.get(0).from.name + " -> " + itinerary.legs.get(0).to.name; 
			
			StreetEdge tmp;
			List<SimplifiedTripEdge> streetEdges = new ArrayList<>();
			for(Leg l : itinerary.legs){
				
				List<WalkStep> steps = l.walkSteps;
				
				for(WalkStep s : steps){
					for(Edge e : s.edges)
						
						if(e instanceof StreetEdge){
							tmp = (StreetEdge)e;
							streetEdges.add(new SimplifiedTripEdge(tmp.getUID(), "", true));
						}
				}
			}
			
			try {
				streetEdgeManager.saveTrip(id, name, streetEdges);
			} catch (UnableToPerformOperation e) {
				e.printStackTrace();
			}
		}
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
	
	/*
	 ********************************************************
	 * E - Trips											*
	 ********************************************************
	 ********************************************************
	 */
	public List<SimplifiedTrip> getUserTrips(String user) 
			throws UnknownUserException, UnableToPerformOperation{
		
		List<SimplifiedTrip> trips = new ArrayList<>();
		
		int userId = accountManager.getUserID(user);
		
		SimplifiedTrip aux;
		for(int id : streetEdgeManager.getUserTrips(userId)){
			aux = streetEdgeManager.getTripDetails(id);
			if(aux!=null) trips.add(aux);
		}
		
		return trips;
	}
	
	public Trip getTrip(int trip){
		try {
			return streetEdgeManager.getTrip(trip);
		} catch (StreetEdgeNotFoundException e) {
			return null;
		}
	}
	
	
	public static void main(String[] args){
		
		CycleOurCityManager man = CycleOurCityManager.getInstance();

		List<GeometryRating> geometries = man.getSafetyRatedGeometries();
		
		/*
		RoutePlanRequest req = new RoutePlanRequest(
								38.7495721,-9.142133, //From
								38.7423355,-9.1399701, //To
								0.2f,0.2f,0.6f);
		
		TripPlan plan;
		try {
			RoutePlanner planner = man.planRoute(req);
			planner.run();
			plan = planner.getTripPlan();

			//Step 2 - Save the trip and its street edges
			try {
				man.saveTrip("bonobo2", plan);
			} catch (UnknownUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnableToPerformOperation e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidPreferenceSetException e) {
			e.printStackTrace();
		}
		*/
	}
}
