package org.cycleourcity.driver;

import java.util.HashMap;
import java.util.List;

import org.cycleourcity.driver.database.structures.CustomStreetEdge;
import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;
import org.cycleourcity.driver.database.structures.SimplifiedTripEdge;
import org.cycleourcity.driver.database.structures.StreetEdgeWithRating;
import org.cycleourcity.driver.database.structures.Trip;
import org.cycleourcity.driver.database.structures.UserRating;
import org.cycleourcity.driver.exceptions.StreetEdgeNotFoundException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnknowStreetEdgeException;

public interface StreetEdgeManagementDriver {


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


	/**
	 * Fetches the list of street edges and corresponding safety ratings
	 * made by the specified user.
	 * <br>
	 * <b>Note: </b> No longer discards streets with less than two classifications.
	 * 
	 * @param userId The user unique identifier
	 * 
	 * @return List of StreetEdgeWithRating
	 */
	public List<StreetEdgeWithRating> getAllSafetyRatings(long userID);

	/**
	 * Fetches the list of street edges and corresponding pavement ratings
	 * made by the specified user.
	 * <br>
	 * <b>Note: </b> No longer discards streets with less than two classifications.
	 * 
	 * @param userId The user unique identifier
	 * 
	 * @return List of StreetEdgeWithRating
	 */
	public List<StreetEdgeWithRating> getAllPavementRatings(long userID);

	/**
	 * Fetches the list of street edges and corresponding rails ratings
	 * made by the specified user.
	 * <br>
	 * <b>Note: </b> No longer discards streets with less than two classifications.
	 * 
	 * @param userId The user unique identifier
	 * 
	 * @return List of StreetEdgeWithRating
	 */
	public List<StreetEdgeWithRating> getAllRailsRatings(long userID);

	/**
	 * Fetches the list of street edges and corresponding elevation ratings
	 * made by the specified user.
	 * <br>
	 * <b>Note: </b> No longer discards streets with less than two classifications.
	 * 
	 * @param userId The user unique identifier
	 * 
	 * @return List of StreetEdgeWithRating
	 */
	public List<StreetEdgeWithRating> getAllElevationRatings(long userID);

	public double[] getAllSafetyFactors();

	public double[] getAllElevationFactors();

	public double[] getAllPavementFactors();

	public double[] getAllRailsFactors();

	public HashMap<Double, List<UserRating>> getAllSafetyRatings();

	public HashMap<Double, List<UserRating>> getAllPavementRatings();

	public HashMap<Double, List<UserRating>> getAllRailsRatings();

	public HashMap<Double, List<UserRating>> getAllElevationRatings();

	//TODO: daqui para baixo nao está nada implementado
	//BACK-END
	public boolean isEmptyMap();

	//public void populateStreetEdges(Graph graph);

	public void populateStreetEdges(List<CustomStreetEdge> streetEdges);

	public boolean updateConsolidatedElevationRatings(HashMap<Double, Integer> ratings);

	public boolean updateConsolidatedSafetyRatings(HashMap<Double, Integer> ratings);

	public boolean updateConsolidatedPavementRatings(HashMap<Double, Integer> ratings);

	public boolean updateConsolidatedRailsRatings(HashMap<Double, Integer> ratings);
}
