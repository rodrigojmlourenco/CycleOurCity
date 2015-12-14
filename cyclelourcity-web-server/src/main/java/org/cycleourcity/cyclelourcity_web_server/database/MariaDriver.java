package org.cycleourcity.cyclelourcity_web_server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.database.exception.NoSuchTokenException;
import org.cycleourcity.cyclelourcity_web_server.database.exception.StreetEdgeNotFoundException;
import org.cycleourcity.cyclelourcity_web_server.database.exception.UnknownUserIdentifierException;
import org.cycleourcity.cyclelourcity_web_server.datatype.CriteriaFactor;
import org.cycleourcity.cyclelourcity_web_server.datatype.GeoLocation;
import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedStreetEdge;
import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedTripEdge;
import org.cycleourcity.cyclelourcity_web_server.utils.CriteriaUtils;
import org.cycleourcity.cyclelourcity_web_server.utils.CriteriaUtils.Criteria;
import org.cycleourcity.cyclelourcity_web_server.utils.SecurityUtils;
import org.cycleourcity.cyclelourcity_web_server.utils.exceptions.UnsupportedCriterionException;
import org.opentripplanner.routing.edgetype.StreetEdge;

//TODO: commits devem passar a ser uma função explicita. Esta passa assim a ser invocadas por classes de topo.

public class MariaDriver implements UsersDriver, TripsDriver, StreetEdgesDriver{


	private final static MariaDriver DRIVER = new MariaDriver();

	private Connection conn;

	//TODO: isto não deve ir parar ao repositório!!!
	private final String database = "UsersClassifications";
	private final String username = "root";
	private final String password = "admin613SHH";

	private MariaDriver(){

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+database, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static MariaDriver getDriver(){ return DRIVER; }


	/*
	 ************************************************************************
	 * A - Users Tables														*
	 ************************************************************************	
	 * These functions were designed to allow the creation, managenent and	*
	 * deletion of new and existing users.									*
	 ************************************************************************/

	//Setters
	public boolean insertUser(String username, String email, String passwordHash, String salt)
		throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO users (Username, Email, Password, Salt) "+
						"VALUES (?,?,?,?)");
		
		statement.setString(0, username);
		statement.setString(1, email);
		statement.setString(2, password);
		statement.setString(3, salt);

		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}
	
	public boolean deleteUser(int userID) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement("DELETE FROM users WHERE Id=?");
		
		statement.setInt(0, userID);
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
		
	}
	
	public boolean deleteUser(String identifier) throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM users "
						+ "WHERE Username=? OR Email=?");
		
		statement.setString(0, identifier);
		statement.setString(1, identifier);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}
	
	private boolean insertIntoUsersEmails(int userID, String token, boolean isRecovery)
		throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO users_emails (IdUser, RecoveryPassword, Token, ExpirationDate) "
						+ "VALUES (?,?,?,?)");
		
		statement.setInt(0, userID);
		if(isRecovery) statement.setInt(1, 1);
		else statement.setInt(1, 0);
		statement.setString(2, token);
		statement.setDate(3, (java.sql.Date) SecurityUtils.getExpirationDate(1));
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}
	
	public boolean insertValidationRequest(int userID, String token)
		throws SQLException{
		
		return insertIntoUsersEmails(userID, token, false);
	}
	
	public boolean deleteRequest(int activationID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement("DELETE FROM users_emails WHERE Id=?");
		
		statement.setInt(0, activationID);
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}
	
	public boolean insertPasswordRecoveryRequest(int userID, String token)
		throws SQLException{
		
		return insertIntoUsersEmails(userID, token, true);
	}
	
	@Override
	public boolean updatePassword(int userID, String passwordHash, String salt) throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"UPDATE users SET Password=?, Salt=? WHERE userID = ?");
		
		statement.setString(0, passwordHash);
		statement.setString(1, salt);
		statement.setInt(2, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}
	
	//Getters
	public int getUserIDfromUsername(String username) throws SQLException{
		
		PreparedStatement statement =
				conn.prepareStatement("SELECT Id FROM users WHERE username = ?");
		
		statement.setString(0, username);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next()){
			return set.getInt("Id");
		}else 
			return -1;
	}
	
	public int getUserIDfromEmail(String email) throws SQLException{
		
		PreparedStatement statement =
				conn.prepareStatement("SELECT Id FROM users WHERE email = ?");
		
		statement.setString(0, email);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next()){
			return set.getInt("Id");
		}else 
			return -1;
	}
	
	public List<Integer> getUserActivationRequests(int userID) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Id "
						+ "FROM users_emails "
						+ "WHERE UserId=? AND PasswordRecovery = '0'");
		
		statement.setInt(0, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		List<Integer> requests = new ArrayList<>();
		while(set.next())
			requests.add(set.getInt(0));
		
		return requests;
	}
	

	public List<Integer> getUserRecoveryRequests(int userID) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Id "
						+ "FROM users_emails "
						+ "WHERE UserId=? AND PasswordRecovery = '1'");
		
		statement.setInt(0, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		List<Integer> requests = new ArrayList<>();
		while(set.next())
			requests.add(set.getInt(0));
		
		return requests;
	}
	
	public List<Integer> getExpiredRequests() throws SQLException{
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT Id "
						+ "FROM users_emails "
						+ "WHERE ExpirationDate < ?");
		
		statement.setDate(0, new java.sql.Date((new Date()).getTime()));
		ResultSet set = statement.executeQuery();
		statement.close();
		
		List<Integer> requests = new ArrayList<>();
		while(set.next())
			requests.add(set.getInt(0));
		
		return requests;
	}
	
	public long getActivationExpirationDate(int activationID) throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT ExpirationDate "
						+ "FROM users_emails "
						+ "WHERE Id = ?");
		
		statement.setInt(0, activationID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next()){
			return set.getDate("ExpirationDate").getTime();
		}else
			return -1;
	}
	
	@Override
	public int getTokenActivationID(String token) throws SQLException, NoSuchTokenException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Id "
						+ "FROM users_emails "
						+ "WHERE Token = ? AND RecoveryPassword = '0'");
		
		statement.setString(0, token);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getInt(0);
		else
			throw new NoSuchTokenException();
		
	}
	
	@Override
	public String getUserPasswordHash(int userID) throws SQLException, UnknownUserIdentifierException {
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Password "
						+ "FROM users "
						+ "WHERE Id = ?");
		
		statement.setInt(0, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(0);
		else
			throw new UnknownUserIdentifierException();
					
	}
	
	//Checkers
	public boolean isUsernameRegistered(String username) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement("SELECT * FROM users WHERE Username=?");

		statement.setString(0, username);
		ResultSet set = statement.executeQuery();
		statement.close();

		return set.next();
	}

	
	public boolean isEmailRegistered(String email) throws SQLException{
		PreparedStatement statement = 
				conn.prepareStatement("SELECT * FROM users WHERE Email=?");

		statement.setString(0, username);
		ResultSet set = statement.executeQuery();
		statement.close();

		return set.next();
	}
	
	@Override
	public boolean isPendingActivation(int userID) throws SQLException{

		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT IdUser FROM users_emails WHERE IdUser = ? AND RecoveryPassword = '0'");
		
		statement.setInt(0, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		return set.next();
	}

	@Override
	public boolean isTokenExpired(String token) throws SQLException, NoSuchTokenException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT ExpirationDate FROM users_email "
						+ "WHERE Token=? AND RecoveryPassword='0'");
			
		statement.setString(0, token);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next()){
			long expires = set.getDate(0).getTime();
			return (expires - (new Date()).getTime()) <= 0;
		}else
			throw new NoSuchTokenException();
		
	}
	
	
	@Override
	public String getUserSalt(int userID) throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement("SELECT salt FROM users WHERE Id = ?");
		
		statement.setInt(0, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(0);
		else
			return null;
	}

	@Override
	public boolean hasMatchingPassword(int userID, String password) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT Id "
						+ "FROM users "
						+ "WHERE Id = ? AND Password = ?");
		
		statement.setInt(0, userID);
		statement.setString(1, password);
		
		ResultSet set = statement.executeQuery();
		
		if(set.next()) return true;
		else return false;
	}

	/*
	 ************************************************************************
	 * B - Criteria Factors													*
	 ************************************************************************	
	 * These table is immutable, and must only be used for search queries	*
	 ************************************************************************/
	/**
	 * Returns a list specified criterion's factors.
	 * 
	 * @param criterion The intended criterion.
	 * 
	 * @return List of CriteriaFactors
	 * @see CriteriaFactor
	 * 
	 * @throws SQLException
	 */
	public List<CriteriaFactor> getCriterionFactors(Criteria criterion) 
			throws SQLException{

		String table;

		switch (criterion) {
		case safety:
			table = "safety";
			break;
		case elevation:
			table = "elevation";
			break;
		case pavement:
			table = "pavement";
			break;
		case rails:
			table = "rails";
			break;
		default:
			return null;
		}

		Statement stmt = conn.createStatement();
		ResultSet r = stmt.executeQuery("SELECT * FROM "+table);
		stmt.close();

		List<CriteriaFactor> factors = new ArrayList<>();

		while(r.next())
			factors.add(new CriteriaFactor(
					r.getInt("Id"),
					r.getString("Name"),
					r.getFloat("Factor")));

		return factors;
	}

	/*
	 ************************************************************************
	 * D - Trips															*
	 ************************************************************************/
	@Override
	public boolean insertTrip(int userID, String name) throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO trips (IdUser, Name) "
						+ "VALUES (?,?)");
		
		statement.setInt(0, userID);
		statement.setString(1, name);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}

	@Override
	public boolean insertTripStreetEdge(int tripID, int streetEdgeID, boolean bicycle) throws SQLException {

		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO trips_streetedges (IdTrip, IdStreetEdge, BicycleMode) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(0, tripID);
		statement.setInt(1, streetEdgeID);
		if(bicycle) statement.setInt(2, 1);
		else if(bicycle) statement.setInt(2, 0);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}

	@Override
	public boolean deleteTrip(int tripID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips WHERE Id=?");
		
		statement.setInt(0, tripID);
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}
	
	@Override
	public boolean deleteTripStreetEdges(int tripID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips_streetedges WHERE IdTrip=?");
		
		statement.setInt(0, tripID);
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}

	@Override
	public boolean deleteTripStreetEdge(int tripID, int streetEdgeID) 
			throws SQLException {

		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips_streetedges "
						+ "WHERE IdTrip = ? AND IdStreetEdge = ?");
		
		statement.setInt(0, tripID);
		statement.setInt(1, streetEdgeID);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}

	@Override
	public List<Integer> getUsersTrips(int userID) throws SQLException {
		
		List<Integer> trips = new ArrayList<Integer>();
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT Id FROM trips WHERE IdUser=?");
		
		statement.setInt(0, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		while(set.next())
			trips.add(set.getInt(0));
		
		return trips;
	}

	@Override
	public List<Integer> getTripStreetEdgesIDs(int tripID) throws SQLException {
		
		List<Integer> streetEdges = new ArrayList<>();
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT IdStreetEdge "
						+ "FROM trips_streetedges "
						+ "WHERE IdTrip = ?");
		
		statement.setInt(0, tripID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		while(set.next())
			streetEdges.add(set.getInt(0));
		
		return streetEdges;
	}
	
	@Override
	public List<SimplifiedTripEdge> getTripStreetEdges(int tripID)
			throws SQLException {
		
		List<SimplifiedTripEdge> streetEdges = new ArrayList<>();
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT t1.IdStreetEdge, t2.Geometry, t1.BicycleMode "
						+ "FROM trips_streetedges AS t1 INNER JOIN streetedges AS t2 "
						+ "ON t1.IdStreetEdge = t2.Id "
						+ "WHERE t1.IdTrip = ?");
		
		statement.setInt(0, tripID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		
		boolean bicycle;
		String geometry;
		int streetEdgeID;
		while(set.next()){
			
			streetEdgeID = set.getInt(0);
			geometry = set.getString(1);
			bicycle = set.getInt(2) == 1;  
			
			streetEdges.add(
					new SimplifiedTripEdge(streetEdgeID, geometry, bicycle));
		}
		
		return streetEdges;
	}
	
	@Override
	public boolean tripContainStreetEdge(int tripID, int streetEdgeID) 
			throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT * FROM trips_streetedges "
						+ "WHERE IdTrip = ? AND IdStreetEdge = ?");
		
		statement.setInt(0, tripID);
		statement.setInt(1, streetEdgeID);
		
		ResultSet set = statement.executeQuery();
		statement.close();
		
		return set.next();
	}
	
	/////////////////////////////////////////////////////////

	@Override
	public boolean insertStreetEdge(String name, GeoLocation from, GeoLocation to, String geometry)
			throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO streetedges (Name, FromVertexLatitude, FromVertexLongitude, ToVertexLatitude, ToVertexLongitude, Geometry) "
						+ "VALUES (?,?,?,?,?,?)");
		
		statement.setString(0, name);
		statement.setDouble(1, from.getLatitude());
		statement.setDouble(2, from.getLongitude());
		statement.setDouble(3, to.getLatitude());
		statement.setDouble(4, to.getLongitude());
		statement.setString(5, geometry);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
	}

	@Override
	public boolean deleteStreetEdge(int streetEdgeID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM streetedges WHERE Id=?");
		
		statement.setInt(0, streetEdgeID);
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return false;
	}

	@Override
	public GeoLocation getSteetEdgeFromLocation(int steetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT FromVertexLatitude, FromVertexLongitude "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setInt(0, steetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return new GeoLocation(set.getFloat(0), set.getFloat(1));
		else
			throw new StreetEdgeNotFoundException();
	}

	@Override
	public GeoLocation getSteetEdgeToLocation(int steetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT ToVertexLatitude, ToVertexLongitude "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setInt(0, steetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return new GeoLocation(set.getFloat(0), set.getFloat(1));
		else
			throw new StreetEdgeNotFoundException();
	}

	@Override
	public String getSteetEdgeName(int steetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Name "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setInt(0, steetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(0);
		else
			throw new StreetEdgeNotFoundException();
	}

	@Override
	public String getSteetEdgeGeometry(int steetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Geometry "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setInt(0, steetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(0);
		else
			throw new StreetEdgeNotFoundException();
	}

	@Override
	public StreetEdge getStreetEdge(int streetEdgeID) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	
	private boolean classifySteetEdgeElevation(int streetEdge, int factorID, int userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_elevation (IdStreetEdge, IdElevation, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(0, streetEdge);
		statement.setInt(1, factorID);
		statement.setInt(2, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
		
	}
	
	private boolean classifySteetEdgeSafety(int streetEdge, int factorID, int userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_safety (IdStreetEdge, IdSafety, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(0, streetEdge);
		statement.setInt(1, factorID);
		statement.setInt(2, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
		
	}
	
	private boolean classifySteetEdgePavement(int streetEdge, int factorID, int userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_pavement (IdStreetEdge, IdPavement, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(0, streetEdge);
		statement.setInt(1, factorID);
		statement.setInt(2, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
		
	}
	
	private boolean classifySteetEdgeRails(int streetEdge, int factorID, int userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_rails (IdStreetEdge, IdRails, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(0, streetEdge);
		statement.setInt(1, factorID);
		statement.setInt(2, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		conn.commit();
		
		return count > 0;
		
	}
	
	@Override
	public boolean classifyStreetEdge(Criteria criterion, int streetEdgeID, int factorID, int userID)
			throws SQLException, UnsupportedCriterionException {
		
		switch (criterion) {
		case safety:
			return classifySteetEdgeSafety(streetEdgeID, factorID, userID);
		case elevation:
			return classifySteetEdgeElevation(streetEdgeID, factorID, userID);
		case pavement:
			return classifySteetEdgePavement(streetEdgeID, factorID, userID);
		case rails:
			return classifySteetEdgeRails(streetEdgeID, factorID, userID);
		default:
			throw new UnsupportedCriterionException();
		}
	}

	@Override
	public List<Integer> getStreetEdgeClassifications(Criteria criterion, int streetEdgeID) throws SQLException, UnsupportedCriterionException {
		
		List<Integer> factors = new ArrayList<>();
		String table = CriteriaUtils.getCriterionClassificationTable(criterion);
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT * FROM "+table+" WHERE IdStreetEdge = ?");
		
		statement.setInt(0, streetEdgeID);
		ResultSet set = statement.executeQuery();
		
		while(set.next())
			factors.add(set.getInt(0));
		
		return null;
	}

	@Override
	public List<Integer> getUserClassifiedStreetEdges(Criteria criterion, int userID) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SimplifiedStreetEdge> getAllStreetEdgesWithElevation() throws SQLException {
		
		List<SimplifiedStreetEdge> edges = new ArrayList<>();
		Statement statement = conn.createStatement();
		ResultSet set = statement.executeQuery(
				"SELECT t1.Geometry, t2.IdElevation"
				+ "FROM streetedges AS t1 INNER JOIN streetedge_consolidatedelevation AS t2"
				+ "ON t1.Id=t2.Id");
		
		int elevationID;
		String geometry;

		while(set.next()){
			geometry = set.getString(0);
			elevationID = set.getInt(1);
			edges.add(new SimplifiedStreetEdge(elevationID, geometry));
		}
		
		return edges;
	}

	@Override
	public List<SimplifiedStreetEdge> getAllStreetEdgesWithSafety() throws SQLException {
		
		List<SimplifiedStreetEdge> edges = new ArrayList<>();
		Statement statement = conn.createStatement();
		ResultSet set = statement.executeQuery(
				"SELECT t1.Geometry, t2.IdElevation"
				+ "FROM streetedges AS t1 INNER JOIN streetedge_consolidatedsafety AS t2"
				+ "ON t1.Id=t2.Id");
		
		int elevationID;
		String geometry;

		while(set.next()){
			geometry = set.getString(0);
			elevationID = set.getInt(1);
			edges.add(new SimplifiedStreetEdge(elevationID, geometry));
		}
		
		return edges;
	}

	@Override
	public List<String> getAllDistinctGeometries() throws SQLException {
		List<String> geometries = new ArrayList<>();
		
		Statement statement = conn.createStatement();
		ResultSet set = statement.executeQuery(
				"SELECT DISTINCT t1.Geometry "
						+ "FROM streetedges AS t1 INNER JOIN streetedge_elevation AS t2 "
						+ "ON t1.Id = t2.IdStreetEdge");
		
		while(set.next())
			geometries.add(set.getString(0));
		
		
		return geometries;
	}

	



	
}

