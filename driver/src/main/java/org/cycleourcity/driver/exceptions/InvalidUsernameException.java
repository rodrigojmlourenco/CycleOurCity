package org.cycleourcity.driver.exceptions;

public class InvalidUsernameException extends UserRegistryException {

	private static final long serialVersionUID = -3909509076069689433L;

	private final String message;
	
	public InvalidUsernameException(String username){
		this.message = "The username '"+username+"' is not valid. "
				+ "A valid username must contain only letters and/or the underscore sign, "
				+ "and must be at least of length 5 up to a maximum length of 10 characters.";
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
