package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.cycleourcity.cyclelourcity_web_server.database.MariaDriver;
import org.cycleourcity.cyclelourcity_web_server.database.UsersDriver;
import org.cycleourcity.cyclelourcity_web_server.database.exception.InvalidUsernameException;
import org.cycleourcity.cyclelourcity_web_server.database.exception.NoSuchTokenException;
import org.cycleourcity.cyclelourcity_web_server.database.exception.UnknownUserIdentifierException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.EmailAlreadyRegisteredException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.ExpiredTokenException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.InvalidEmailException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.InvalidIdentifierException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.InvalidPasswordException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.NonMatchingPasswordsException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.PasswordReuseException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnableToPerformOperation;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnableToRegisterUserException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnableToUnregisterUserException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnknownUserException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UserRegistryException;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UsernameAlreadyRegisteredException;
import org.cycleourcity.cyclelourcity_web_server.utils.FormFieldValidator;
import org.cycleourcity.cyclelourcity_web_server.utils.SecurityUtils;

public class AccountManager implements AccountManagementLayer{
	
	protected final int HASHING_ITERATIONS = 0;
	protected final int SALT_SIZE = 64;
	protected final int TOKEN_SIZE = 25;
	
	private final UsersDriver driver;
	private final static AccountManager MANAGER = new AccountManager();
	
	private AccountManager(){
		driver = MariaDriver.getDriver();
	}
	
	public static AccountManager getManager(){
		return MANAGER;
	}

	private boolean isEmailRegistered(String email) {
		try {
			return driver.isEmailRegistered(email);
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}

	private boolean isUsernameRegistered(String username) {
		
		try {
			return driver.isUsernameRegistered(username);
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	private void validateFields(String username, String email, String pass1, String pass2) 
			throws InvalidEmailException, InvalidUsernameException, InvalidPasswordException, UsernameAlreadyRegisteredException, EmailAlreadyRegisteredException, NonMatchingPasswordsException{
		
		if(!FormFieldValidator.isValidEmail(email))
			throw new InvalidEmailException(email);

		if(!FormFieldValidator.isValidUsername(username))
			throw new InvalidUsernameException(username);

		if(!FormFieldValidator.isValidPassword(pass1)) 
			throw new InvalidPasswordException(pass1);
		
		if(!FormFieldValidator.isValidPassword(pass2))
			throw new InvalidPasswordException(pass2);

		if(isUsernameRegistered(username))
			throw new UsernameAlreadyRegisteredException(username);

		if(isEmailRegistered(email))
			throw new EmailAlreadyRegisteredException(email);

		if(!pass1.equals(pass2))
			throw new NonMatchingPasswordsException();
	}
	
	

	@Override
	public String registerUser(String username, String email, String pass1, String pass2) 
			throws UserRegistryException, NonMatchingPasswordsException, 
			UnableToRegisterUserException, UnableToPerformOperation {
		
		// 1 - Validate the security of the provided fields.
		validateFields(username, email, pass1, pass2);
		
		// 2 - Secure the password
		String salt;
		byte[] hashedPassword;
		try {
			salt = new String(SecurityUtils.generateSalt(SALT_SIZE), "UTF-8");
			hashedPassword=  SecurityUtils.getSecureHash(HASHING_ITERATIONS, pass1, salt.getBytes());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new UnableToRegisterUserException();
		}
		
		// 3 - Create new user entry
		boolean success = false;
		try {
			success = driver.insertUser(username, email, new String(hashedPassword, "UTF-8"), salt);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnableToRegisterUserException();
		} catch (UnsupportedEncodingException e) {
			throw new UnableToPerformOperation(e.getMessage());
		}finally {
			if(!success) throw new UnableToRegisterUserException();
		}
		
		// 4 - Setup the activation token
		int userID = -1;
		String token = "";
		success = false;
		try {
			userID = driver.getUserIDfromUsername(username);
			token = SecurityUtils.generateSecureActivationToken(25);
			byte[] hashedToken = SecurityUtils.hashSHA1(token);
			
			success = driver.insertValidationRequest(userID, new String(hashedToken, "UTF-8"));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			success = false;
		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		} catch (UnsupportedEncodingException e) {
			throw new UnableToPerformOperation(e.getMessage());
		} 
		
		try{
			if(!success){
				driver.deleteUser(userID);
				throw new UnableToRegisterUserException();
			}
			
		}catch(SQLException e){
			throw new UnableToRegisterUserException();
		}
		
		return token;
	}

	@Override
	public boolean unregisterUser(String identifier, String password) 
			throws UnableToUnregisterUserException, NonMatchingPasswordsException {

		try{
			
			int userID = getUserID(identifier);
			
			if(isValidPassword(identifier, password))
				return driver.deleteUser(userID);
			else
				throw new NonMatchingPasswordsException();
					
		}catch(SQLException e){
			e.printStackTrace();
			throw new UnableToUnregisterUserException(identifier, e.getMessage());
		} catch (InvalidIdentifierException e) {
			throw new UnableToUnregisterUserException(identifier, e.getMessage());
		} catch (UnableToPerformOperation e) {
			throw new UnableToUnregisterUserException(identifier, e.getMessage());
		} catch (UnknownUserException e) {
			throw new UnableToUnregisterUserException(identifier, e.getMessage());
		}
	}

	@Override
	public boolean isPendingActivation(int userID) {
		
		try {
			return driver.isPendingActivation(userID);
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}

	@Override
	public boolean activateAccount(String token) throws ExpiredTokenException, UnableToPerformOperation {
		
		try {
			byte[] hashedToken = SecurityUtils.hashSHA1(token);
			String tokenHashAsString = new String(hashedToken, "UTF-8");
			
			if(driver.isTokenExpired(tokenHashAsString))
				throw new ExpiredTokenException();
			
			int activationID = driver.getTokenActivationID(tokenHashAsString);
			
			return driver.deleteRequest(activationID);
			
			
		} catch (NoSuchAlgorithmException e) {
			throw new UnableToPerformOperation(e.getMessage());
		} catch (SQLException e) {
			throw new UnableToPerformOperation(e.getMessage());
		} catch (NoSuchTokenException e) {
			e.printStackTrace();
			throw new ExpiredTokenException();
		} catch (UnsupportedEncodingException e) {
			throw new UnableToPerformOperation(e.getMessage());
			
		} 
	}

	@Override
	public boolean changePassword(String username, String oldPass, String newPass, String confirmPass)
			throws NonMatchingPasswordsException, PasswordReuseException, UnknownUserIdentifierException {
		
		if(oldPass.equals(newPass))
			throw new PasswordReuseException();
		
		
		int userID;
		String original, salt;
		byte[] newSalt;
		try {

			
			userID 	= driver.getUserIDfromUsername(username);
			if(userID == -1) throw new UnknownUserIdentifierException();
			
			original= driver.getUserPasswordHash(userID);
			salt	= driver.getUserSalt(userID);
			
			oldPass = new String(SecurityUtils.getSecureHash(HASHING_ITERATIONS, oldPass, salt.getBytes("UTF-8")), "UTF-8");
			
			if(!original.equals(oldPass) || !newPass.equals(confirmPass))
				throw new NonMatchingPasswordsException();
			
			
			newSalt = SecurityUtils.generateSalt(SALT_SIZE);
			newPass = new String(SecurityUtils.getSecureHash(HASHING_ITERATIONS, newPass, newSalt), "UTF-8");
			
			return driver.updatePassword(userID, newPass, new String(newSalt, "UTF-8"));
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String recoverPassword(String email) throws UnknownUserIdentifierException, UnableToPerformOperation {
		
		int userID;
		String token;
		try {
			
			userID = driver.getUserIDfromEmail(email);
			if(userID == -1) throw new UnknownUserIdentifierException();
			
			token = SecurityUtils.generateSecureActivationToken(TOKEN_SIZE);
			String tokenHash = new String(SecurityUtils.hashSHA1(token), "UTF-8");
			
			driver.insertPasswordRecoveryRequest(userID, tokenHash);
			return token;
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} catch (NoSuchAlgorithmException e) {
			throw new UnableToPerformOperation(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new UnableToPerformOperation(e.getMessage());
		}
		
		return null;
	}

	@Override
	public boolean resetPassword(String username, String token, String newPass, String confirmPass)
			throws ExpiredTokenException, UnknownUserIdentifierException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean login(String identifier, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUserID(String identifier) throws UnknownUserException, UnableToPerformOperation {
		
		int UID;
		try{
			if(FormFieldValidator.isValidEmail(identifier))
				UID =  driver.getUserIDfromEmail(identifier);
			else if(FormFieldValidator.isValidUsername(identifier))
				UID = driver.getUserIDfromUsername(identifier);
			else
				throw new UnableToPerformOperation("Invalid identifier.");
			
			if(UID <= -1)
				throw new UnknownUserException(identifier);
			else
				return UID;
			
		}catch(SQLException e){
			throw new UnableToPerformOperation(e.getMessage());
		}
	}

	@Override
	public boolean isValidPassword(String identifier, String password) throws InvalidIdentifierException, UnableToPerformOperation {
		
		int userID;
		String salt;
		
		try{
		if(FormFieldValidator.isValidEmail(identifier))
			userID = driver.getUserIDfromEmail(identifier);
		else if(FormFieldValidator.isValidUsername(identifier))
			userID = driver.getUserIDfromUsername(identifier);
		else 
			throw new InvalidIdentifierException(identifier);
		
		salt = driver.getUserSalt(userID);
		
		byte[] hashedPass = SecurityUtils.getSecureHash(HASHING_ITERATIONS, password, salt.getBytes("UTF-8"));
		
		return driver.hasMatchingPassword(userID, new String(hashedPass, "UTF-8"));
		
		}catch(SQLException e){
			throw new UnableToPerformOperation(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new UnableToPerformOperation(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new UnableToPerformOperation(e.getMessage());
		}
	}

}
