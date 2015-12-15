package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions;

public class UsernameAlreadyRegisteredException extends UserRegistryException{

	private static final long serialVersionUID = 6974056787375027861L;

	private final String message;
	
	public UsernameAlreadyRegisteredException(String username){
		this.message = "The username '"+username+"' is already registered.";
	}
}
