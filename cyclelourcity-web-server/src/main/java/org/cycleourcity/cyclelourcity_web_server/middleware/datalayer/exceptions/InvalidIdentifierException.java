package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions;

public class InvalidIdentifierException extends Exception {

	private static final long serialVersionUID = 2993257370671409462L;
	private final String message;
	
	public InvalidIdentifierException(String identifier){
		this.message = identifier + " does not correspond to a valid username nor email address.";
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
