package org.cycleourcity.cyclelourcity_web_server.database;

import java.sql.SQLException;
import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedTripEdge;

public interface TripsDriver {

	/**
	 * Creates a new trip entry.
	 * 
	 * @param userID The user's UID
	 * @param name The trips name.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * @throws SQLException 
	 */
	public boolean insertTrip(int userID, String name) throws SQLException;
	
	/**
	 * Creates a new trip's street edge.
	 * 
	 * @param tripID The trip's UID.
	 * @param streetEdgeID The street edge's UID.
	 * @param bicycle True if bicycle mode, false otherwise.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * @throws SQLException 
	 */
	public boolean insertTripStreetEdge(int tripID, int streetEdgeID, boolean bicycle) throws SQLException;
	
	
	/**
	 * Deletes a specific trip entry.
	 * 
	 * @param tripID The trip's UID.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * @throws SQLException 
	 */
	public boolean deleteTrip(int tripID) throws SQLException;
	
	/**
	 * Deletes all street edges associated with a specific trip.
	 * 
	 * @param tripID The trip's UID
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean deleteTripStreetEdges(int tripID) throws SQLException;
	
	/**
	 * Deletes a street edge associated with a specific trip.
	 * 
	 * @param tripID The trip's UID.
	 * @param streetEdgeID The street edge's UID.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * @throws SQLException 
	 */
	public boolean deleteTripStreetEdge(int tripID, int streetEdgeID) throws SQLException;

	/**
	 * Fetches a list of a specific user's trips.
	 * 
	 * @param userID The user's UID.
	 * 
	 * @return List containing the identifiers of the user's trips.
	 * @throws SQLException 
	 */
	public List<Integer> getUsersTrips(int userID) throws SQLException;
	
	/**
	 * Fetches a list of all the street edges that comprise a specific trip.
	 * 
	 * @param tripID The trips UID.
	 * 
	 * @return List containing the street edges identifiers that comprise a specific trip.
	 * @throws SQLException 
	 */
	public List<Integer> getTripStreetEdgesIDs(int tripID) throws SQLException;
	
	/**
	 * Fetches a list of all the street edges that comprise a specific trip.
	 * 
	 * @param tripID The trips UID.
	 * 
	 * @return List containing the street edges, as SimplifiedStreetEdges that comprise a specific trip.
	 * @throws SQLException
	 * 
	 * @see {@link SimplifiedTripEdge}
	 */
	public List<SimplifiedTripEdge> getTripStreetEdges(int tripID) throws SQLException;
	
	/**
	 * Checks if the specified trip contain a specific street edge.
	 * 
	 * @param tripID The trip's UID
	 * @param streetEdgeID The street edge's UID
	 * 
	 * @return True if the trip contains the street edge, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean tripContainStreetEdge(int tripID, int streetEdgeID) throws SQLException;
}
