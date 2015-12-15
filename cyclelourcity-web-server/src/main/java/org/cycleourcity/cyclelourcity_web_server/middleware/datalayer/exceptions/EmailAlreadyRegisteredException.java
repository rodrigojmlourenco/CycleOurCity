package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions;

public class EmailAlreadyRegisteredException extends UserRegistryException {

	private static final long serialVersionUID = 8056827152115455078L;

	private final String message;
	
	public EmailAlreadyRegisteredException(String email){
		this.message = "The email "+email+" is already registered.";
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
