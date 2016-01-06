package org.cycleourcity.cyclelourcity_web_server.database;

import java.sql.SQLException;
import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.database.exception.NoSuchTokenException;
import org.cycleourcity.cyclelourcity_web_server.database.exception.UnknownUserIdentifierException;

public interface UsersDriver {
	
	/**
	 * Inserts a new user in the users table.
	 * 
	 * @param username The user's username.
	 * @param email The user's email address.
	 * @param passwordHash The user's passwordHash
	 * @param salt The salt.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 *  
	 * @throws SQLException
	 */
	public boolean insertUser(String username, String email, String passwordHash, String salt) throws SQLException;
	
	/**
	 * Removes a user from the users table.
	 * 
	 * @param userID The user's UID.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean deleteUser(long userID) throws SQLException;
	
	/**
	 * Removes a user from the users table.
	 * 
	 * @param identifier Either the user's username or email.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean deleteUser(String identifier) throws SQLException;
	
	/**
	 * Creates a new validation request entry.
	 * <br>
	 * <b>Note: </b>Every time a new user is registered, an activation request
	 * must also be created.
	 * 
	 * @param userID The user's UID.
	 * @param token The activation token.
	 * 
	 * @return True if the operation was succesful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean insertValidationRequest(long userID, String token) throws SQLException;

	/**
	 * Deletes an user's activation or recovery request entry.
	 * 
	 * @param activationID The activation UID.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean deleteRequest(int activationID) throws SQLException;
	
	/**
	 * Creates a new password recovery request entry.
	 * 
	 * @param userID The user's UID.
	 * @param token The activation token.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean insertPasswordRecoveryRequest(long userID, String token) throws SQLException;
	
	/**
	 * Fetches a user's UID.
	 * 
	 * @param username The user's unique username
	 * @return The user's UID if he exists, -1 otherwise.
	 * 
	 * @throws SQLException
	 */
	public int getUserIDfromUsername(String username) throws SQLException;
	
	/**
	 * Fetches a user's UID.
	 * 
	 * @param email The users email address.
	 * 
	 * @return The user's UID if he exists, -1 otherwise.
	 * @throws SQLException
	 */
	public int getUserIDfromEmail(String email) throws SQLException;
	
	/**
	 * Fetches a list of a user's pending activation requests.
	 * 
	 * @param userID The user's UID.
	 * 
	 * @return List containing the identifiers of all pending activation requests.
	 * 
	 * @throws SQLException
	 */
	public List<Integer> getUserActivationRequests(long userID) throws SQLException;
	
	/**
	 * Fetches a list of a user's pending recovery requests.
	 * 
	 * @param userID The user's UID.
	 * 
	 * @return List containing the identifiers of all pending recovery requests.
	 * 
	 * @throws SQLException
	 */
	public List<Integer> getUserRecoveryRequests(long userID) throws SQLException;
	
	/**
	 * Fetches a list of all expired requests, either activation or
	 * password recovery.
	 * 
	 * @return List containing the identifiers of all expired requests.
	 * 
	 * @throws SQLException
	 */
	public List<Integer> getExpiredRequests() throws SQLException;
	
	/**
	 * Returns the expiration date of a specific activation request entry.
	 * 
	 * @param activationID The request UID
	 * 
	 * @return The expiration date in millis.
	 * 
	 * @throws SQLException
	 */
	public long getActivationExpirationDate(int activationID) throws SQLException; 
	
	/**
	 * Check is a given email address is already registered.
	 * 
	 * @param email The user's email address.
	 * 
	 * @return True if the email is registered, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean isEmailRegistered(String email) throws SQLException;
	
	/**
	 * Check is a given username is already registered.
	 * 
	 * @param email The user's username.
	 * 
	 * @return True if the username is registered, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean isUsernameRegistered(String username) throws SQLException;
	
	/**
	 * Fetches the salt used to secure a user's password.
	 * 
	 * @param userID The user's UID
	 * @return The salt.
	 * 
	 * @throws SQLException
	 */
	public String getUserSalt(long userID) throws SQLException;
	
	/**
	 * Fetches the password hash of the specified user.
	 * 
	 * @param userID The user's UID
	 * 
	 * @return The password hash.
	 *  
	 * @throws SQLException
	 * @throws UnknownUserIdentifierException 
	 */
	public String getUserPasswordHash(long userID) throws SQLException, UnknownUserIdentifierException;
	
	
	/**
	 * Checks if the provided passwords matches the one stored, upon the
	 * user's creation.
	 * 
	 * @param userID The user's UID
	 * @param password The password used for confimation.
	 * @return
	 * @throws SQLException 
	 */
	public boolean hasMatchingPassword(long userID, String password) throws SQLException;
	
	/**
	 * Checks if a given user account has not yet been activated.
	 * 
	 * @param userID The user's UID
	 * 
	 * @return True if the activation is pending, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean isPendingActivation(long userID) throws SQLException;
	
	/**
	 * Checks if a given token has expired.
	 * 
	 * @param token The activation token (not cleartext)
	 * 
	 * @return True if the token has expired, false otherwise.
	 * @throws SQLException
	 * @throws NoSuchTokenException 
	 */
	public boolean isTokenExpired(String token) throws SQLException, NoSuchTokenException;
	
	/**
	 * Fetches the activation UID associated with the provided token
	 * 
	 * @param token The activation token (not cleartext)
	 * 
	 * @return The activation UID
	 * 
	 * @throws SQLException
	 * @throws NoSuchTokenException
	 */
	public int getTokenActivationID(String token) throws SQLException, NoSuchTokenException;
	
	
	/**
	 * Updates the password and salt of a given user.
	 * 
	 * @param userID The user's UID
	 * @param passwordHash The new password hash
	 * @param salt The new salt.
	 * 
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws SQLException
	 */
	public boolean updatePassword(long userID, String passwordHash, String salt) throws SQLException;
	
	
	public List<Long> getUsersIDs() throws SQLException;
}
