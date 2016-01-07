package org.cycleourcity.driver.database.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cycleourcity.driver.database.StreetEdgesDriver;
import org.cycleourcity.driver.database.TestDriver;
import org.cycleourcity.driver.database.TripsDriver;
import org.cycleourcity.driver.database.UsersDriver;
import org.cycleourcity.driver.database.structures.CriteriaFactor;
import org.cycleourcity.driver.database.structures.CustomStreetEdge;
import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;
import org.cycleourcity.driver.database.structures.SimplifiedTripEdge;
import org.cycleourcity.driver.exceptions.NoSuchTokenException;
import org.cycleourcity.driver.exceptions.StreetEdgeNotFoundException;
import org.cycleourcity.driver.exceptions.UnknownUserIdentifierException;
import org.cycleourcity.driver.exceptions.UnsupportedCriterionException;
import org.cycleourcity.driver.utils.CriteriaUtils;
import org.cycleourcity.driver.utils.SecurityUtils;
import org.cycleourcity.driver.utils.CriteriaUtils.Criteria;


public class MariaDriver implements UsersDriver, TripsDriver, StreetEdgesDriver, TestDriver{


	private final static MariaDriver DRIVER = new MariaDriver();

	private Connection conn;

	//TODO: isto não deve ir parar ao repositório!!!
	private final String DATABASE = "UsersClassifications";
	private final String USERNAME = "root";
	private final String PASSWORD = "admin613SSH";

	private MariaDriver(){

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+DATABASE+"?characterEncoding=utf8", USERNAME, PASSWORD);
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
		
		statement.setString(1, username);
		statement.setString(2, email);
		statement.setString(3, passwordHash);
		statement.setString(4, salt);

		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	public boolean deleteUser(long userID) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement("DELETE FROM users WHERE Id=?");
		
		statement.setLong(1, userID);
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
		
	}
	
	public boolean deleteUser(String identifier) throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM users "
						+ "WHERE Username=? OR Email=?");
		
		statement.setString(1, identifier);
		statement.setString(2, identifier);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	private boolean insertIntoUsersEmails(long userID, String token, boolean isRecovery)
		throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO users_emails (IdUser, RecoveryPassword, Token, ExpirationDate) "
						+ "VALUES (?,?,?,?)");
		
		statement.setLong(1, userID);
		if(isRecovery) statement.setInt(2, 1);
		else statement.setInt(2, 0);
		statement.setString(3, token);
		statement.setDate(4, new java.sql.Date(SecurityUtils.getExpirationDate(1).getTime()) );
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	public boolean insertValidationRequest(long userID, String token)
		throws SQLException{
		
		return insertIntoUsersEmails(userID, token, false);
	}
	
	public boolean deleteRequest(int activationID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement("DELETE FROM users_emails WHERE Id=?");
		
		statement.setInt(1, activationID);
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	public boolean insertPasswordRecoveryRequest(long userID, String token)
		throws SQLException{
		
		return insertIntoUsersEmails(userID, token, true);
	}
	
	public boolean updatePassword(long userID, String passwordHash, String salt) throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"UPDATE users SET Password=?, Salt=? WHERE userID = ?");
		
		statement.setString(1, passwordHash);
		statement.setString(2, salt);
		statement.setLong(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	//Getters
	public int getUserIDfromUsername(String username) throws SQLException{
		
		PreparedStatement statement =
				conn.prepareStatement("SELECT Id FROM users WHERE username = ?");
		
		statement.setString(1, username);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next()){
			return set.getInt(1);
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
	
	public List<Integer> getUserActivationRequests(long userID) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Id "
						+ "FROM users_emails "
						+ "WHERE UserId=? AND PasswordRecovery = '0'");
		
		statement.setLong(0, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		List<Integer> requests = new ArrayList<Integer>();
		while(set.next())
			requests.add(set.getInt(0));
		
		return requests;
	}
	

	public List<Integer> getUserRecoveryRequests(long userID) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Id "
						+ "FROM users_emails "
						+ "WHERE UserId=? AND PasswordRecovery = '1'");
		
		statement.setLong(0, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		List<Integer> requests = new ArrayList<Integer>();
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
		
		statement.setDate(1, new java.sql.Date((new Date()).getTime()));
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
		
		statement.setInt(1, activationID);
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
		
		statement.setString(1, token);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getInt(1);
		else
			throw new NoSuchTokenException();
		
	}
	
	@Override
	public String getUserPasswordHash(long userID) throws SQLException, UnknownUserIdentifierException {
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Password "
						+ "FROM users "
						+ "WHERE Id = ?");
		
		statement.setLong(1, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(1);
		else
			throw new UnknownUserIdentifierException();
					
	}
	
	//Checkers
	public boolean isUsernameRegistered(String username) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement("SELECT * FROM users WHERE Username=?");

		statement.setString(1, username);
		ResultSet set = statement.executeQuery();
		statement.close();

		return set.next();
	}

	
	public boolean isEmailRegistered(String email) throws SQLException{
		PreparedStatement statement = 
				conn.prepareStatement("SELECT * FROM users WHERE Email=?");

		statement.setString(1, email);
		ResultSet set = statement.executeQuery();
		statement.close();

		return set.next();
	}
	
	@Override
	public boolean isPendingActivation(long userID) throws SQLException{

		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT IdUser FROM users_emails WHERE IdUser = ? AND RecoveryPassword = '0'");
		
		statement.setLong(1, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		return set.next();
	}

	@Override
	public boolean isTokenExpired(String token) throws SQLException, NoSuchTokenException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT ExpirationDate FROM users_emails "
						+ "WHERE Token=? AND RecoveryPassword='0'");
			
		statement.setString(1, token);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next()){
			long expires = set.getDate(1).getTime();
			return (expires - (new Date()).getTime()) <= 0;
		}else
			throw new NoSuchTokenException();
		
	}
	
	
	@Override
	public String getUserSalt(long userID) throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement("SELECT salt FROM users WHERE Id = ?");
		
		statement.setLong(1, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(1);
		else
			return null;
	}


	@Override
	public boolean hasMatchingPassword(long userID, String password) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT Id "
						+ "FROM users "
						+ "WHERE Id = ? AND Password = ?");
		
		statement.setLong(1, userID);
		statement.setString(2, password);
		
		ResultSet set = statement.executeQuery();
		
		if(set.next()) return true;
		else 
			return false;
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
	public boolean insertTrip(long userID, String name) throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO trips (IdUser, Name) "
						+ "VALUES (?,?)");
		
		statement.setLong(1, userID);
		statement.setString(2, name);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public boolean insertTripStreetEdge(long tripID, double streetEdgeID, boolean bicycle) throws SQLException {

		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO trips_streetedges (IdTrip, IdStreetEdge, BicycleMode) "
						+ "VALUES (?,?,?)");
		
		statement.setLong(1, tripID);
		statement.setDouble(2, streetEdgeID);
		if(bicycle) statement.setInt(3, 1);
		else if(bicycle) statement.setInt(3, 0);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public boolean deleteTrip(long tripID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips WHERE Id=?");
		
		statement.setLong(1, tripID);
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	@Override
	public boolean deleteTripStreetEdges(long tripID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips_streetedges WHERE IdTrip=?");
		
		statement.setLong(1, tripID);
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public boolean deleteTripStreetEdge(long tripID, double streetEdgeID) 
			throws SQLException {

		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips_streetedges "
						+ "WHERE IdTrip = ? AND IdStreetEdge = ?");
		
		statement.setLong(1, tripID);
		statement.setDouble(2, streetEdgeID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public List<Integer> getUsersTrips(long userID) throws SQLException {
		
		List<Integer> trips = new ArrayList<Integer>();
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT Id FROM trips WHERE IdUser=?");
		
		statement.setLong(1, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		while(set.next())
			trips.add(set.getInt(1));
		
		return trips;
	}

	@Override
	public List<Integer> getTripStreetEdgesIDs(long tripID) throws SQLException {
		
		List<Integer> streetEdges = new ArrayList<>();
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT IdStreetEdge "
						+ "FROM trips_streetedges "
						+ "WHERE IdTrip = ?");
		
		statement.setLong(1, tripID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		while(set.next())
			streetEdges.add(set.getInt(1));
		
		return streetEdges;
	}
	
	@Override
	public List<SimplifiedTripEdge> getTripStreetEdges(long tripID)
			throws SQLException {
		
		List<SimplifiedTripEdge> streetEdges = new ArrayList<>();
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT t1.IdStreetEdge, t2.Geometry, t1.BicycleMode "
						+ "FROM trips_streetedges AS t1 INNER JOIN streetedges AS t2 "
						+ "ON t1.IdStreetEdge = t2.Id "
						+ "WHERE t1.IdTrip = ?");
		
		statement.setLong(1, tripID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		boolean bicycle;
		String geometry;
		double streetEdgeID;
		while(set.next()){
			
			streetEdgeID = set.getInt(1);
			geometry = set.getString(2);
			bicycle = set.getInt(3) == 1;  
			
			streetEdges.add(
					new SimplifiedTripEdge(streetEdgeID, geometry, bicycle));
		}
		
		return streetEdges;
	}
	
	@Override
	public boolean tripContainStreetEdge(long tripID, double streetEdgeID) 
			throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT * FROM trips_streetedges "
						+ "WHERE IdTrip = ? AND IdStreetEdge = ?");
		
		statement.setLong(1, tripID);
		statement.setDouble(2, streetEdgeID);
		
		ResultSet set = statement.executeQuery();
		statement.close();
		
		return set.next();
	}
	
	/////////////////////////////////////////////////////////

	@Override
	public boolean insertStreetEdge(double id, String name, GeoLocation from, GeoLocation to, String geometry)
			throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO streetedges (Id, Name, FromVertexLatitude, FromVertexLongitude, ToVertexLatitude, ToVertexLongitude, Geometry) "
						+ "VALUES (?,?,?,?,?,?,?)");
		
		statement.setDouble(1, id);
		statement.setString(2, name);
		statement.setDouble(3, from.getLatitude());
		statement.setDouble(4, from.getLongitude());
		statement.setDouble(5, to.getLatitude());
		statement.setDouble(6, to.getLongitude());
		statement.setString(7, geometry);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public boolean deleteStreetEdge(double streetEdgeID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM streetedges WHERE Id=?");
		
		statement.setDouble(1, streetEdgeID);
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public GeoLocation getSteetEdgeFromLocation(double streetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT FromVertexLatitude, FromVertexLongitude "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setDouble(1, streetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return new GeoLocation(set.getFloat(1), set.getFloat(2));
		else
			throw new StreetEdgeNotFoundException();
	}

	@Override
	public GeoLocation getSteetEdgeToLocation(double streetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT ToVertexLatitude, ToVertexLongitude "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setDouble(1, streetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return new GeoLocation(set.getFloat(1), set.getFloat(2));
		else
			throw new StreetEdgeNotFoundException();
	}

	@Override
	public String getSteetEdgeName(double streetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Name "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setDouble(1, streetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(1);
		else
			throw new StreetEdgeNotFoundException();
	}

	@Override
	public String getSteetEdgeGeometry(double streetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Geometry "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setDouble(1, streetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(1);
		else
			throw new StreetEdgeNotFoundException();
	}

	@Override
	public CustomStreetEdge getStreetEdge(double streetEdgeID) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	
	private boolean classifySteetEdgeElevation(double streetEdgeID, int factorID, long userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_elevation (IdStreetEdge, IdElevation, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setDouble(1, streetEdgeID);
		statement.setInt(2, factorID);
		statement.setLong(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
		
	}
	
	private boolean classifySteetEdgeSafety(double streetEdgeID, int factorID, long userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_safety (IdStreetEdge, IdSafety, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setDouble(1, streetEdgeID);
		statement.setInt(2, factorID);
		statement.setLong(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
		
	}
	
	private boolean classifySteetEdgePavement(double streetEdgeID, int factorID, long userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_pavement (IdStreetEdge, IdPavement, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setDouble(1, streetEdgeID);
		statement.setInt(2, factorID);
		statement.setLong(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	private boolean classifySteetEdgeRails(double streetEdgeID, int factorID, long userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_rails (IdStreetEdge, IdRails, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setDouble(1, streetEdgeID);
		statement.setInt(2, factorID);
		statement.setLong(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	@Override
	public boolean classifyStreetEdge(Criteria criterion, double streetEdgeID, int factorID, long userID)
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
	public List<Integer> getStreetEdgeClassifications(Criteria criterion, double streetEdgeID) throws SQLException, UnsupportedCriterionException {
		
		List<Integer> factors = new ArrayList<>();
		String table = CriteriaUtils.getCriterionClassificationTable(criterion);
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT * FROM "+table+" WHERE IdStreetEdge = ?");
		
		statement.setDouble(1, streetEdgeID);
		ResultSet set = statement.executeQuery();
		
		while(set.next())
			factors.add(set.getInt(1));
		
		return null;
	}

	@Override
	public List<Integer> getUserClassifiedStreetEdges(Criteria criterion, long userID) throws SQLException {
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
			geometry = set.getString(1);
			elevationID = set.getInt(2);
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
			geometry = set.getString(1);
			elevationID = set.getInt(2);
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
			geometries.add(set.getString(1));
		
		
		return geometries;
	}

	@Override
	public void clearTable(String tablename) throws SQLException {
		Statement statement = conn.createStatement();
		statement.executeQuery("Delete from "+tablename);
		statement.executeQuery("ALTER TABLE "+tablename+" AUTO_INCREMENT = 1");
		statement.close();
		
	}

	@Override
	public boolean isEmptyMap() throws SQLException {
		Statement statement = conn.createStatement();
		ResultSet set = statement.executeQuery("SELECT Id From streetedges");
		
		return !set.next();
	}

	@Override
	public List<Long> getUsersIDs() throws SQLException {
		List<Long> users = new ArrayList<Long>();
		Statement statement = conn.createStatement();
		ResultSet set = statement.executeQuery("SELECT Id FROM users");
		
		while(set.next())
			users.add(set.getLong(0));
		
		return users;
	}	
}

