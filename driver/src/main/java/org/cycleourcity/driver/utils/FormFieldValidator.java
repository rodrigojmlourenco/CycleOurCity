package org.cycleourcity.driver.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;

public class FormFieldValidator {

	private final static EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();
	private final static Pattern USERNAME_VALIDATOR  	= Pattern.compile("^[a-z0-9_]{5,10}$");
	private final static Pattern PASSWORD_VALIDATOR  	= Pattern.compile("(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
	
	public static boolean isValidEmail(String email){
		return EMAIL_VALIDATOR.isValid(email);
	}
	
	public static boolean isValidUsername(String username){
		Matcher m = USERNAME_VALIDATOR.matcher(username);
		return m.matches();
	}
	
	public static boolean isValidPassword(String pass1){
		Matcher m1 = PASSWORD_VALIDATOR.matcher(pass1);
		return m1.matches();
	}
	
}
