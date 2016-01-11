package org.cycleourcity.driver.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.cycleourcity.driver.database.structures.CustomStreetEdge;
import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;
import org.cycleourcity.driver.database.structures.StreetEdgeWithRating;
import org.cycleourcity.driver.database.structures.UserRating;
import org.cycleourcity.driver.exceptions.StreetEdgeNotFoundException;
import org.cycleourcity.driver.exceptions.UnsupportedCriterionException;
import org.cycleourcity.driver.utils.CriteriaUtils.Criteria;

public interface StreetEdgesDriver {

	/**
	 * Creates a new street edge entry.
	 * 
	 * @param name The street edge's name.
	 * @param from The GeoLocation where the street edge starts.
	 * @param to The GeoLocation where the street edge finishes.
	 * @param geometry The street edge's geometry
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean insertStreetEdge(double id, String name, GeoLocation from, GeoLocation to, String geometry) throws SQLException;

	/**
	 * Deletes a specific street edge entry.
	 * 
	 * @param streetEdgeID The street edge's UID
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean deleteStreetEdge(double streetEdgeID) throws SQLException;

	/**
	 * Fetches a specific street edge starting location.
	 * 
	 * @param steetEdgeID The street edges UID.
	 * 
	 * @return The street edge starting location. 
	 * 
	 * @throws SQLException
	 * @throws StreetEdgeNotFoundException 
	 * 
	 * @see {@link GeoLocation}
	 */
	public GeoLocation getSteetEdgeFromLocation(double streetEdgeID) throws SQLException, StreetEdgeNotFoundException;

	/**
	 * Fetches a specific street edge finishing location.
	 * 
	 * @param steetEdgeID The street edges UID.
	 * 
	 * @return The street edges finishing location. 
	 * 
	 * @throws SQLException
	 * @throws StreetEdgeNotFoundException 
	 * 
	 * @see {@link GeoLocation}
	 */
	public GeoLocation getSteetEdgeToLocation(double streetEdgeID) throws SQLException, StreetEdgeNotFoundException;

	/**
	 * Fetches a specific street edge name.
	 * 
	 * @param steetEdgeID The street edges UID.
	 * 
	 * @return The street edge's name. 
	 * 
	 * @throws SQLException
	 * @throws StreetEdgeNotFoundException 
	 */
	public String getSteetEdgeName(double streetEdgeID) throws SQLException, StreetEdgeNotFoundException;

	/**
	 * Fetches a specific street edge geometry.
	 * 
	 * @param steetEdgeID The street edges UID.
	 * 
	 * @return The street edge's geometry as a string. 
	 * 
	 * @throws SQLException
	 * @throws StreetEdgeNotFoundException 
	 */
	public String getSteetEdgeGeometry(double streetEdgeID) throws SQLException, StreetEdgeNotFoundException;

	/**
	 * Fetches a street edge as an object.
	 * 
	 * @param streetEdgeID The street edge's UID.
	 * 
	 * @return The street edge.
	 * 
	 * @throws SQLException
	 * 
	 * @see {@link StreetEdge}
	 */
	public CustomStreetEdge getStreetEdge(double streetEdgeID) throws SQLException;  

	/**
	 * Classifies a street edge according to the specified criterion and
	 * its correspondent factor. 
	 * 
	 * @param criterion The classification criterion.
	 * @param streetEdgeID The street edge UID
	 * @param factorID The factor UID
	 * @param userID The user UID
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 * @throws UnsupportedCriterionException 
	 */
	public boolean classifyStreetEdge(Criteria criterion, double streetEdgeID, int factorID, long userID) throws SQLException, UnsupportedCriterionException;

	/**
	 * Fetches all the classification factors identifiers with regard
	 * to the classifications provided for the specified street edge.
	 * 
	 * @param criterion The classification criterion.
	 * @param streetEdgeID The street edge UID
	 * 
	 * @return List containing the factors identifiers, which are used to classify that street edge.
	 * @throws SQLException
	 * @throws UnsupportedCriterionException 
	 */
	public List<Integer> getStreetEdgeClassifications(Criteria criterion, double streetEdgeID) throws SQLException, UnsupportedCriterionException;

	/**
	 * Checks if the street edges tables, which characterizes the a city's map,
	 * is empty or not.
	 * 
	 * @return True if it is empty, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean isEmptyMap() throws SQLException;

	/**
	 * Fetches all geometries from street edges, which are classified
	 * in terms of their elevation.
	 * 
	 * @return List of geometries.
	 * @throws SQLException 
	 */
	public List<String> getAllDistinctGeometries() throws SQLException;


	/**
	 * Fetches the constant factors that define a street's safety.
	 * 
	 * @return The criterion factor
	 * @throws SQLException
	 */
	public double[] getAllSafetyFactors() throws SQLException;

	/**
	 * Fetches the constant factors that define a street's elevation.
	 * 
	 * @return The criterion factor
	 * @throws SQLException
	 */
	public double[] getAllElevationFactors() throws SQLException;

	/**
	 * Fetches the constant factors that define a street's pavement.
	 * 
	 * @return The criterion factor
	 * @throws SQLException
	 */
	public double[] getAllPavementFactors() throws SQLException;

	/**
	 * Fetches the constant factors that define a street's rails.
	 * 
	 * @return The criterion factor
	 * @throws SQLException
	 */
	public double[] getAllRailsFactors() throws SQLException;

	/**
	 * Fetches a map, which is indexed by street edge id, and that contains
	 * a list of all user safety ratings. Each UserRating in the list pertains to
	 * a particular user.
	 * 
	 * @return Map of list of user ratings per street edge.
	 * 
	 * @throws SQLException
	 */
	public HashMap<Double, List<UserRating>> getAllSafetyRatings() throws SQLException;

	/**
	 * Fetches a map, which is indexed by street edge id, and that contains
	 * a list of all user pavement ratings. Each UserRating in the list pertains to
	 * a particular user.
	 * 
	 * @return Map of list of user ratings per street edge.
	 * 
	 * @throws SQLException
	 */
	public HashMap<Double, List<UserRating>> getAllPavementRatings() throws SQLException;

	/**
	 * Fetches a map, which is indexed by street edge id, and that contains
	 * a list of all user elevation ratings. Each UserRating in the list pertains to
	 * a particular user.
	 * 
	 * @return Map of list of user ratings per street edge.
	 * 
	 * @throws SQLException
	 */
	public HashMap<Double, List<UserRating>> getAllElevationRatings() throws SQLException;

	/**
	 * Fetches a map, which is indexed by street edge id, and that contains
	 * a list of all user rails ratings. Each UserRating in the list pertains to
	 * a particular user.
	 * 
	 * @return Map of list of user ratings per street edge.
	 * 
	 * @throws SQLException
	 */
	public HashMap<Double, List<UserRating>> getAllRailsRatings() throws SQLException;

	//TODO: daqui para baixo extá tudo por terminar/implementar
	
	public List<StreetEdgeWithRating> getAllSafetyRatingsByUser(long userId) throws SQLException;
	
	public List<StreetEdgeWithRating> getAllSafetyElevationByUser(long userId) throws SQLException;
	
	public List<StreetEdgeWithRating> getAllSafetyPavementByUser(long userId) throws SQLException;
	
	public List<StreetEdgeWithRating> getAllSafetyRailsByUser(long userId) throws SQLException;

	public void updateConsolidatedSafetyRating(double streetEdgeId, int rating) throws SQLException;
	
	public void updateConsolidatedElevationRating(double streetEdgeId, int rating) throws SQLException;
	
	public void updateConsolidatedPavementRating(double streetEdgeId, int rating) throws SQLException;
	
	public void updateConsolidatedRailsRating(double streetEdgeId, int rating) throws SQLException;

	
	public List<SimplifiedStreetEdge> getAllStreetEdgesWithElevation() throws SQLException;

	public List<Integer> getUserClassifiedStreetEdges(Criteria criterion, long userID) throws SQLException;

	public List<SimplifiedStreetEdge> getAllStreetEdgesWithSafety() throws SQLException;

}
