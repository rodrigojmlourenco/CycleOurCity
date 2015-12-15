package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer;

import org.cycleourcity.cyclelourcity_web_server.database.exception.UnknownUserIdentifierException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.ExpiredTokenException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.InvalidIdentifierException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.NonMatchingPasswordsException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.PasswordReuseException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnableToPerformOperation;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnableToRegisterUserException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnableToUnregisterUserException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnknownUserException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UserRegistryException;

public interface AccountManagementLayer {

	//@RegisterUser.php
	/**
	 * Creates a new user. This method must also be responsible for
	 * validating all the provided fields. If successful, the
	 * method will return the user's activation token, which
	 * must then be used to activate its account.
	 * 
	 * @param username The user's username.
	 * @param email The user's email address.
	 * @param pass1 The user's password.
	 * @param pass2 The user's password confirmation.
	 * 
	 * @return The activation token
	 * 
	 * @throws UserRegistryException If one of the fields is invalid.
	 * @throws NonMatchingPasswordsException If the passwords do not match.
	 * @throws UnableToRegisterUserException If the user creation was unsuccessful for some unforseeable event.
	 * @throws UnableToPerformOperation 
	 *  
	 */
	public String registerUser(String username, String email, String pass1, String pass2)
		throws UserRegistryException, NonMatchingPasswordsException, UnableToRegisterUserException, UnableToPerformOperation;
	
	
	/**
	 * Unregister a user, however, only if presents a correct password.
	 * 
	 * @param identifier The user's identifier, which may either be his username or email.
	 * @param password THe user's password.
	 * 
	 * @return True if the operation was successful. 
	 * @throws UnableToUnregisterUserException 
	 * @throws NonMatchingPasswordsException 
	 */
	public boolean unregisterUser(String identifier, String password) throws UnableToUnregisterUserException, NonMatchingPasswordsException;
	
	/**
	 * Fetches a given user's UID.
	 * 
	 * @param identifier The user's identification, which may either be his username or email.
	 * 
	 * @return The user's UID
	 * 
	 * @throws UnknownUserException There is no user registered with the provided identifier.
	 * @throws UnableToPerformOperation The operation was unsuccessful due to unforseeable reasons.
	 */
	public int getUserID(String identifier) throws UnknownUserException, UnableToPerformOperation;
	
	//@ActivateAccount.php
	/**
	 * Checks if a given user account is still pending activation.
	 * 
	 * @param userID The user's UID
	 * 
	 * @return True if the account has not yet been activated, false otherwise.
	 */
	public boolean isPendingActivation(int userID);
	
	//@ActivateAccount.php	
	/**
	 * Activated a specific user account given the provided activation token.
	 * 
	 * @param token The activation token (cleartext)
	 * @return True if the operation was successful, false otherwise.
	 * 
	 * @throws ExpiredTokenException
	 * @throws UnableToPerformOperation 
	 */
	public boolean activateAccount(String token) throws ExpiredTokenException, UnableToPerformOperation;
	
	
	//@ChangePasswordJSON.php
	public boolean changePassword(String username, String oldPass, String newPass, String confirmPass) 
			throws NonMatchingPasswordsException, PasswordReuseException, UnknownUserIdentifierException;

	//@ForgotPasswordJSON.php
	public String recoverPassword(String email) 
			throws UnknownUserIdentifierException, UnableToPerformOperation;
	
	//@ResetPassword.php
	public boolean resetPassword(String username, String token, String newPass, String confirmPass) 
			throws ExpiredTokenException, UnknownUserIdentifierException;
	
	//@Login.php
	public boolean login(String identifier, String password);
	
	//@Logout.php
	public boolean logout();
	
	
	public boolean isValidPassword(String identifier, String password) throws InvalidIdentifierException, UnableToPerformOperation;
}
