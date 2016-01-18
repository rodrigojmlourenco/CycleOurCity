package org.cycleourcity.driver.exceptions;

public class UsernameAlreadyRegisteredException extends UserRegistryException{

	private static final long serialVersionUID = 6974056787375027861L;

	private final String message;
	
	public UsernameAlreadyRegisteredException(String username){
		this.message = "The username '"+username+"' is already registered.";
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
