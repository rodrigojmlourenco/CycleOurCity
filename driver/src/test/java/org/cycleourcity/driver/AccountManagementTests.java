package org.cycleourcity.driver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Random;

import org.cycleourcity.driver.database.TestDriver;
import org.cycleourcity.driver.database.impl.MariaDriver;
import org.cycleourcity.driver.exceptions.ExpiredTokenException;
import org.cycleourcity.driver.exceptions.InvalidIdentifierException;
import org.cycleourcity.driver.exceptions.NonMatchingPasswordsException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnableToRegisterUserException;
import org.cycleourcity.driver.exceptions.UnableToUnregisterUserException;
import org.cycleourcity.driver.exceptions.UnknownUserException;
import org.cycleourcity.driver.exceptions.UserRegistryException;
import org.cycleourcity.driver.impl.AccountManagementDriverImpl;
import org.cycleourcity.driver.utils.SecurityUtils;
import org.junit.Assert;
import org.junit.Test;

public class AccountManagementTests {

	private TestDriver testDriver = MariaDriver.getDriver();
	private AccountManagementDriver manager = AccountManagementDriverImpl.getManager(); 
	
	private void teardown(){
		try {
			testDriver.clearTable("users");
			testDriver.clearTable("users_emails");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUserCreationFailByUsername(){
		
		teardown();
		
		String correctUsername 		= "test_1";
		String password				= "passworD123#";
		String email1				= "test1@gmail.com";
		String email2				= "test2@gmail.com";
		String incorrectUsername1 	= "Test1"; 
		String shortUsername		= "test";
		String longUsername			= "thisisjusttoolongausername";
		String repeatedUsername		= correctUsername;
		
		try {
			manager.registerUser(correctUsername, email1, password, password);
			Assert.assertTrue("User "+correctUsername+"created successfully", true);
		} catch (UserRegistryException | NonMatchingPasswordsException | UnableToRegisterUserException | UnableToPerformOperation e) {
			fail("User should have been created, failed because "+e.getMessage());
		}
		
		try {
			manager.registerUser(incorrectUsername1, email2, password, password);
			fail("User '"+incorrectUsername1+"' should not have been created. Exiting...");
			return;
		} catch (UserRegistryException e) {
			assertTrue("Incorrect username '"+incorrectUsername1+"' successfully detected.", true);
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(shortUsername, email2, password, password);
			fail("User '"+shortUsername+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			assertTrue("Incorrect username '"+shortUsername+"' successfully detected.", true);
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(longUsername, email2, password, password);
			fail("User '"+longUsername+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			assertTrue("Incorrect username '"+longUsername+"' successfully detected.", true);
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		
		try {
			manager.registerUser(repeatedUsername, email2, password, password);
			fail("User '"+repeatedUsername+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			assertTrue("Incorrect username '"+repeatedUsername+"' successfully detected.", true);
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
	}
	
	@Test
	public void testUserCreationFailByEmail(){
		
		teardown();
		
		String username1 		= "test_1";
		String username2		= "test2";
		String password			= "passworD123#";
		String email1			= "test1@gmail.com";
		String emailFalse1		= "test2gmail.com";
		String emailFalse2		= "test3@something.xpto";
		String emailFalse3		= "@sapo.pt";
		
		try {
			manager.registerUser(username1, email1, password, password);
		} catch (UserRegistryException | NonMatchingPasswordsException | UnableToRegisterUserException | UnableToPerformOperation e) {
			fail("User should have been created, failed because "+e.getMessage());
		}
		
		try {
			manager.registerUser(username2, emailFalse1, password, password);
			fail("User '"+emailFalse1+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			assertTrue("Incorrect username '"+emailFalse1+"' successfully detected.", true);
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(username2, emailFalse2, password, password);
			fail("User '"+emailFalse2+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			assertTrue("Incorrect username '"+emailFalse2+"' successfully detected.", true);
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(username2, emailFalse3, password, password);
			fail("User '"+emailFalse3+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			assertTrue("Incorrect username '"+emailFalse3+"' successfully detected.", true);
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		teardown();
	}
	
	
	@Test
	public void testUserCreationFailByPassword(){
		
		teardown();
		
		String username1 		= "test_1";
		String username2		= "test2";
		String email1			= "test1@gmail.com";
		String email2			= "test2@gmail.com";
		String password			= "passwordD123#";
		
		String pMissingCaps		= "password123#";
		String pMissingMins		= "PASSWORD123#";
		String pMissingNumb		= "PassWOrd#";
		String pMissingSpecial	= "PassWOrd123";
		String pShort			= "Pa1#!";
		
		try {
			manager.registerUser(username1, email1, password, password);
		} catch (UserRegistryException | NonMatchingPasswordsException | UnableToRegisterUserException e) {
			fail("User should have been created.");
		} catch (UnableToPerformOperation e) {
			fail("User should have been created.");
		}
		
		try {
			manager.registerUser(username2, email2, password, password+"!");
			fail("User '"+username2+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (NonMatchingPasswordsException e) {
			;
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(username2, email2, pMissingCaps, pMissingCaps);
			fail("User '"+username2+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			;
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(username2, email2, pMissingMins, pMissingMins);
			fail("User '"+username2+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			;
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(username2, email2, pMissingNumb, pMissingNumb);
			fail("User '"+username2+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			;
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(username2, email2, pMissingSpecial, pMissingSpecial);
			fail("User '"+username2+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			;
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
		
		try {
			manager.registerUser(username2, email2, pShort, pShort);
			fail("User '"+username2+"' should not have been created. Exiting...");
			teardown();
			return;
		} catch (UserRegistryException e) {
			;
		} catch (NonMatchingPasswordsException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("Something went wrong: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("Something went wrong: "+e.getMessage());
		}
	}
	
	@Test
	public void testUserCorrectCreation(){
	
		teardown();
		
		int UID;
		String identifier 	= "test1";
		String password		= "passworD123#";
		String tokenClearText;
		
		try {
			
			tokenClearText = manager.registerUser(identifier, "test1@test.com", password, password);
			
			UID = manager.getUserID(identifier);
			
		} catch (UserRegistryException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (NonMatchingPasswordsException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (UnknownUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} 
		
	}
	
	@Test
	public void testUserCorrectCreationAndWaitingValidation(){
	
		teardown();
		
		int UID;
		String identifier 	= "test1";
		String password		= "passworD123#";
		String tokenClearText;
		
		try {
			
			tokenClearText = manager.registerUser(identifier, "test1@test.com", password, password);
			
			UID = manager.getUserID(identifier);
			
			assertTrue(manager.isPendingActivation(UID));
			
		} catch (UserRegistryException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (NonMatchingPasswordsException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (UnableToRegisterUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (UnknownUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} 
		
	}
	
	@Test
	public void testUserCorrectCreationAndValidation(){
	
		teardown();
		
		int UID;
		String identifier 	= "test1";
		String password		= "passworD123#";
		String tokenClearText;
		
		try {
			
			tokenClearText = manager.registerUser(identifier, "test1@test.com", password, password);
			
			UID = manager.getUserID(identifier);
			
			assertTrue(manager.isPendingActivation(UID));
			
		} catch (UserRegistryException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (NonMatchingPasswordsException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (UnableToRegisterUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (UnknownUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (UnableToPerformOperation e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		}
		
		try {
			manager.activateAccount(tokenClearText);
			assertFalse(manager.isPendingActivation(UID));
		} catch (ExpiredTokenException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		} catch (UnableToPerformOperation e) {
			fail("User should have been created, failed due to: "+e.getMessage());
		}
	}
	
	@Test
	public void testUserCorrectCreationAndDeletion(){
	
		teardown();
		
		int UID;
		String identifier 	= "test1";
		String password		= "passworD123#";
		String tokenClearText;
		
		try {
			
			tokenClearText = manager.registerUser(identifier, "test1@test.com", password, password);
			
			UID = manager.getUserID(identifier);
			
			assertTrue(manager.isPendingActivation(UID));
			
		} catch (UserRegistryException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (NonMatchingPasswordsException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (UnableToRegisterUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (UnknownUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (UnableToPerformOperation e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		}
		
		try {
			
			manager.unregisterUser(identifier, password);
			
		} catch (UnableToUnregisterUserException e) {
			fail("User should have been unregistered, failed due to: "+e.getMessage());
		} catch (NonMatchingPasswordsException e) {
			fail("User should have been unregistered, failed due to: "+e.getMessage());
		}
		
		try {
			
			System.out.println(manager.getUserID(identifier));
		} catch (UnknownUserException e) {
			;
		} catch (UnableToPerformOperation e) {
			fail();
		}
	}

	
	@Test
	public void testUserPasswordValidation(){
		
		String identifier 	= "test1";
		String password		= "passworD123#";
		
		teardown();
		
		try {
			manager.registerUser(identifier, "test1@test.com", password, password);
		} catch (UserRegistryException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (NonMatchingPasswordsException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (UnableToRegisterUserException e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		} catch (UnableToPerformOperation e) {
			fail("User should have been created, failed due to: "+e.getMessage());
			return;
		}
		
		try {
			assertTrue("Password should have been validated correctly", manager.isValidPassword(identifier, password));
		} catch (InvalidIdentifierException | UnableToPerformOperation e) {
			fail("Password should have been validated correctly, failed because "+e.getMessage());
		}
		
		try {
			assertFalse("Password should not have been validated correctly", manager.isValidPassword(identifier, password+'#'));
		} catch (InvalidIdentifierException | UnableToPerformOperation e) {
			fail("Password should not have been validated correctly, failed because "+e.getMessage());
		}
		
		try {
			assertFalse("Password should not have been validated correctly", manager.isValidPassword(identifier, password+password+password));
		} catch (InvalidIdentifierException | UnableToPerformOperation e) {
			fail("Password should not have been validated correctly, failed because "+e.getMessage());
		}
			
		int size;
		String newpass;
		Random r = new Random();
		for(int i=0; i<=10000; i++){
			do{
				size = r.nextInt(25);
			}while(size < 8);
			
			try {
				newpass = SecurityUtils.generateSecureActivationToken(size);
				assertFalse("Password should not have been validated correctly", manager.isValidPassword(identifier, newpass));
			} catch (UnsupportedEncodingException | InvalidIdentifierException | UnableToPerformOperation e) {
				fail("Password should not have been validated correctly, failed because "+e.getMessage());
			}
			
			
		}
		
	}

}
