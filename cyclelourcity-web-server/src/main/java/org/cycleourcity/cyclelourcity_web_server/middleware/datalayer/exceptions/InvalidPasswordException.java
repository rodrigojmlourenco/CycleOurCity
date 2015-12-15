package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions;

public class InvalidPasswordException extends UserRegistryException {

	private static final long serialVersionUID = -6489716055868108261L;

	private final String message;
	
	public InvalidPasswordException(String pass){
		this.message = "The password '"+pass+"' is not valid. "
				+ "A password should be at least 8 characters long, and must contain "
				+ "number, letters both caps and non-caps, and special characters";
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
