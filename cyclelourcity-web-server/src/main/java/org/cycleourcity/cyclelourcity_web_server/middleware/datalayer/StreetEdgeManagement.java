package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer;

import java.util.HashMap;
import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.database.exception.StreetEdgeNotFoundException;
import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedStreetEdge;
import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedTripEdge;
import org.cycleourcity.cyclelourcity_web_server.datatype.Trip;
import org.cycleourcity.cyclelourcity_web_server.datatype.UserRating;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnableToPerformOperation;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnknowStreetEdgeException;
import org.opentripplanner.routing.graph.Graph;

public interface StreetEdgeManagement {

	
	//@StreetEdgeRating.php
	/**
	 * Fetches a list of all street edges classified in terms of elevation.
	 * 
	 * @return List of street edges
	 * 
	 * @see SimplifiedElevationEdge
	 */
	public List<SimplifiedStreetEdge> getStreetEdgesWithElevation();
	
	
	/**
	 * Fetches a list of all street edges classified in terms of safety.
	 * 
	 * @return List of street edges
	 * 
	 * @see SimplifiedSafetyEdge
	 */
	public List<SimplifiedStreetEdge> getStreetEdgesWithSafety();
	
	
	//@Usertrips.php
	/**
	 * Fetches all the street edges from a specific trip.
	 * <br>
	 * <b>SEE: </b>Userstrips.php
	 * 
	 * @param tripID The trip's UID
	 * 
	 * @return The trip's street edges as a Trip object
	 * @throws StreetEdgeNotFoundException 
	 * 
	 * @see Trip
	 */
	public Trip getTrip(int tripID) throws StreetEdgeNotFoundException;
	
	/**
	 * Fetches all the trips belonging to a specific user.
	 * <br>
	 * <b>SEE: </b>Userstrips.php 
	 * 
	 * TODO: deve ser verificado se o user tem sessão iniciada
	 * 
	 * @param userID The user's UID
	 * 
	 * @return List comprised of the user trips' UIDs
	 */
	public List<Integer> getUserTrips(int userID);
	
	/**
	 * TODO: comment
	 * @param userID
	 * @param tripName
	 * @param streetEdges
	 */
	public void saveTrip(long userID, String tripName, List<SimplifiedTripEdge> streetEdges) throws UnableToPerformOperation;
	
	/**
	 * Fetches all geometries from street edges, which are classified
	 * in terms of their elevation.
	 * 
	 * <br>
	 * <b>SEE: </b>RatedStreetEdges.php
	 * 
	 * @return List of geometries.
	 */
	public List<String> getAllDistinctGeometries();
	
	//@InsertUserFeedback.php
	/**
	 * Classifies a street edge belonging to a a user's given trip,
	 * according to four possible classification criteria.
	 * 
	 * @param tripID The trip's UID
	 * @param streetEdgeID The street edge's UID
	 * @param safety The safety classification ([1, 6])
	 * @param elevation The elevation classification ([1, 6])
	 * @param pavement The pavement classification ([1,4])
	 * @param rails The rails classification ([1,3])
	 * @param userID The user's UID
	 * @param last Specifies if this is the last street edge that belongs to the trip (being classified)
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws UnknowStreetEdgeException 
	 */
	public boolean classifyStreetEdge(long tripID, long streetEdgeID, int safety, int elevation, int pavement, int rails, long userID, boolean last) throws UnknowStreetEdgeException;
	
	//BACK-END
	public boolean isEmptyMap();
	
	public void populateStreetEdges(Graph graph);
	
	public HashMap<Long, List<UserRating>> getAllSafetyRatings();
	
	public HashMap<Long, List<UserRating>> getAllPavementRatings();
	
	public HashMap<Long, List<UserRating>> getAllRailsRatings();
	
	public HashMap<Long, List<UserRating>> getAllElevationRatings();
	
	public double[] getAllSafetyFactorsIDs();
	
	public double[] getAllElevationFactorsIDs();
	
	public double[] getAllPavementFactorsIDs();
	
	public double[] getAllRailsFactorsIDs();
	
	public boolean clearAndUpdateConsolidatedElevationRatings(HashMap<Integer, Integer> ratings);
	
	public boolean clearAndUpdateConsolidatedSafetyRatings(HashMap<Integer, Integer> ratings);
	
	public boolean clearAndUpdateConsolidatedPavementRatings(HashMap<Integer, Integer> ratings);
	
	public boolean clearAndUpdateConsolidatedRailsRatings(HashMap<Integer, Integer> ratings);
}
