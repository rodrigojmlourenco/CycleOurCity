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
	
	public boolean deleteUser(int userID) throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement("DELETE FROM users WHERE Id=?");
		
		statement.setInt(1, userID);
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
	
	private boolean insertIntoUsersEmails(int userID, String token, boolean isRecovery)
		throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO users_emails (IdUser, RecoveryPassword, Token, ExpirationDate) "
						+ "VALUES (?,?,?,?)");
		
		statement.setInt(1, userID);
		if(isRecovery) statement.setInt(2, 1);
		else statement.setInt(2, 0);
		statement.setString(3, token);
		statement.setDate(4, new java.sql.Date(SecurityUtils.getExpirationDate(1).getTime()) );
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	public boolean insertValidationRequest(int userID, String token)
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
	
	public boolean insertPasswordRecoveryRequest(int userID, String token)
		throws SQLException{
		
		return insertIntoUsersEmails(userID, token, true);
	}
	
	@Override
	public boolean updatePassword(int userID, String passwordHash, String salt) throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"UPDATE users SET Password=?, Salt=? WHERE userID = ?");
		
		statement.setString(1, passwordHash);
		statement.setString(2, salt);
		statement.setInt(3, userID);
		
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
	public String getUserPasswordHash(int userID) throws SQLException, UnknownUserIdentifierException {
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT Password "
						+ "FROM users "
						+ "WHERE Id = ?");
		
		statement.setInt(1, userID);
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
	public boolean isPendingActivation(int userID) throws SQLException{

		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT IdUser FROM users_emails WHERE IdUser = ? AND RecoveryPassword = '0'");
		
		statement.setInt(1, userID);
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
	public String getUserSalt(int userID) throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement("SELECT salt FROM users WHERE Id = ?");
		
		statement.setInt(1, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(1);
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
		
		statement.setInt(1, userID);
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
	public boolean insertTrip(int userID, String name) throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO trips (IdUser, Name) "
						+ "VALUES (?,?)");
		
		statement.setInt(1, userID);
		statement.setString(2, name);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public boolean insertTripStreetEdge(int tripID, int streetEdgeID, boolean bicycle) throws SQLException {

		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO trips_streetedges (IdTrip, IdStreetEdge, BicycleMode) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(1, tripID);
		statement.setInt(2, streetEdgeID);
		if(bicycle) statement.setInt(3, 1);
		else if(bicycle) statement.setInt(3, 0);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public boolean deleteTrip(int tripID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips WHERE Id=?");
		
		statement.setInt(1, tripID);
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}
	
	@Override
	public boolean deleteTripStreetEdges(int tripID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips_streetedges WHERE IdTrip=?");
		
		statement.setInt(1, tripID);
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public boolean deleteTripStreetEdge(int tripID, int streetEdgeID) 
			throws SQLException {

		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM trips_streetedges "
						+ "WHERE IdTrip = ? AND IdStreetEdge = ?");
		
		statement.setInt(1, tripID);
		statement.setInt(2, streetEdgeID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public List<Integer> getUsersTrips(int userID) throws SQLException {
		
		List<Integer> trips = new ArrayList<Integer>();
		
		PreparedStatement statement =
				conn.prepareStatement(
						"SELECT Id FROM trips WHERE IdUser=?");
		
		statement.setInt(1, userID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		while(set.next())
			trips.add(set.getInt(1));
		
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
		
		statement.setInt(1, tripID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		while(set.next())
			streetEdges.add(set.getInt(1));
		
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
		
		statement.setInt(1, tripID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		
		boolean bicycle;
		String geometry;
		int streetEdgeID;
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
	public boolean tripContainStreetEdge(int tripID, int streetEdgeID) 
			throws SQLException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT * FROM trips_streetedges "
						+ "WHERE IdTrip = ? AND IdStreetEdge = ?");
		
		statement.setInt(1, tripID);
		statement.setInt(2, streetEdgeID);
		
		ResultSet set = statement.executeQuery();
		statement.close();
		
		return set.next();
	}
	
	/////////////////////////////////////////////////////////

	@Override
	public boolean insertStreetEdge(Long id, String name, GeoLocation from, GeoLocation to, String geometry)
			throws SQLException {
		
		PreparedStatement statement =
				conn.prepareStatement(
						"INSERT INTO streetedges (Id, Name, FromVertexLatitude, FromVertexLongitude, ToVertexLatitude, ToVertexLongitude, Geometry) "
						+ "VALUES (?,?,?,?,?,?,?)");
		
		statement.setLong(1, id);
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
	public boolean deleteStreetEdge(int streetEdgeID) throws SQLException {
		PreparedStatement statement =
				conn.prepareStatement(
						"DELETE FROM streetedges WHERE Id=?");
		
		statement.setInt(1, streetEdgeID);
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
	}

	@Override
	public GeoLocation getSteetEdgeFromLocation(int steetEdgeID) 
			throws SQLException, StreetEdgeNotFoundException {
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"SELECT FromVertexLatitude, FromVertexLongitude "
						+ "FROM streetedges "
						+ "WHERE Id = ?");
		
		statement.setInt(1, steetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return new GeoLocation(set.getFloat(1), set.getFloat(2));
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
		
		statement.setInt(1, steetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return new GeoLocation(set.getFloat(1), set.getFloat(2));
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
		
		statement.setInt(1, steetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(1);
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
		
		statement.setInt(1, steetEdgeID);
		ResultSet set = statement.executeQuery();
		statement.close();
		
		if(set.next())
			return set.getString(1);
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
		
		statement.setInt(1, streetEdge);
		statement.setInt(2, factorID);
		statement.setInt(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
		
	}
	
	private boolean classifySteetEdgeSafety(int streetEdge, int factorID, int userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_safety (IdStreetEdge, IdSafety, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(1, streetEdge);
		statement.setInt(2, factorID);
		statement.setInt(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
		
	}
	
	private boolean classifySteetEdgePavement(int streetEdge, int factorID, int userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_pavement (IdStreetEdge, IdPavement, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(1, streetEdge);
		statement.setInt(2, factorID);
		statement.setInt(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
		return count > 0;
		
	}
	
	private boolean classifySteetEdgeRails(int streetEdge, int factorID, int userID) 
			throws SQLException{
		
		PreparedStatement statement = 
				conn.prepareStatement(
						"INSERT INTO streetedges_rails (IdStreetEdge, IdRails, IdUser) "
						+ "VALUES (?,?,?)");
		
		statement.setInt(1, streetEdge);
		statement.setInt(2, factorID);
		statement.setInt(3, userID);
		
		int count = statement.executeUpdate();
		statement.close();
		
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
		
		statement.setInt(1, streetEdgeID);
		ResultSet set = statement.executeQuery();
		
		while(set.next())
			factors.add(set.getInt(1));
		
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
		List<Long> users = new ArrayList();
		Statement statement = conn.createStatement();
		ResultSet set = statement.executeQuery("SELECT Id FROM users");
		
		while(set.next())
			users.add(set.getLong(0));
		
		return users;
	}


	



	
}

