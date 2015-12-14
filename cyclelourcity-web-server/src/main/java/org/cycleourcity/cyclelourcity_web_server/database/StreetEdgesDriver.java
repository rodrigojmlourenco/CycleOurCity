package org.cycleourcity.cyclelourcity_web_server.database;

import java.sql.SQLException;
import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.database.exception.StreetEdgeNotFoundException;
import org.cycleourcity.cyclelourcity_web_server.datatype.GeoLocation;
import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedStreetEdge;
import org.cycleourcity.cyclelourcity_web_server.utils.CriteriaUtils.Criteria;
import org.cycleourcity.cyclelourcity_web_server.utils.exceptions.UnsupportedCriterionException;
import org.opentripplanner.routing.edgetype.StreetEdge;

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
	public boolean insertStreetEdge(String name, GeoLocation from, GeoLocation to, String geometry) throws SQLException;
	
	/**
	 * Deletes a specific street edge entry.
	 * 
	 * @param streetEdgeID The street edge's UID
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean deleteStreetEdge(int streetEdgeID) throws SQLException;
	
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
	public GeoLocation getSteetEdgeFromLocation(int steetEdgeID) throws SQLException, StreetEdgeNotFoundException;
	
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
	public GeoLocation getSteetEdgeToLocation(int steetEdgeID) throws SQLException, StreetEdgeNotFoundException;
	
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
	public String getSteetEdgeName(int steetEdgeID) throws SQLException, StreetEdgeNotFoundException;
	
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
	public String getSteetEdgeGeometry(int steetEdgeID) throws SQLException, StreetEdgeNotFoundException;
	
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
	public StreetEdge getStreetEdge(int streetEdgeID) throws SQLException;  

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
	public boolean classifyStreetEdge(Criteria criterion, int streetEdgeID, int factorID, int userID) throws SQLException, UnsupportedCriterionException;
	
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
	public List<Integer> getStreetEdgeClassifications(Criteria criterion, int streetEdgeID) throws SQLException, UnsupportedCriterionException;
	
	/**
	 * Fetches all classification provided by a specific user,
	 * for a specific classification criterion.
	 * 
	 * @param criterion The classification criterion.
	 * @param userID The user's UID
	 * 
	 * @return List of all street edges classified by the user.
	 * @throws SQLException
	 */
	public List<Integer> getUserClassifiedStreetEdges(Criteria criterion, int userID) throws SQLException;
	
	
	/**
	 * Fetches all the street edges that are classified with regard to their elevation.
	 * @return List of edges with elevation classifications
	 * @throws SQLException
	 * @see {@link SimplifiedElevationEdge}
	 */
	public List<SimplifiedStreetEdge> getAllStreetEdgesWithElevation() throws SQLException;
	
	/**
	 * Fetches all the street edges that are classified with regard to their safety.
	 * @return List of edges with safety classifications
	 * @throws SQLException
	 * @see {@link SimplifiedElevationEdge}
	 */
	public List<SimplifiedStreetEdge> getAllStreetEdgesWithSafety() throws SQLException;
	
	
	/**
	 * Fetches all geometries from street edges, which are classified
	 * in terms of their elevation.
	 * 
	 * @return List of geometries.
	 * @throws SQLException 
	 */
	public List<String> getAllDistinctGeometries() throws SQLException;
}
