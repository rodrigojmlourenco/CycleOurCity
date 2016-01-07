package org.cycleourcity.driver.exceptions;

public class UnknownUserException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String message;
	
	public UnknownUserException(String identifier){
		this.message = "The user '"+identifier+"' has not yet been registered.";
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
