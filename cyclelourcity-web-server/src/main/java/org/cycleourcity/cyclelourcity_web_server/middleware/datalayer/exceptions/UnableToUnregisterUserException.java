package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions;

public class UnableToUnregisterUserException extends Exception {

	private static final long serialVersionUID = -7301494796857374156L;
	
	private final String message;
	
	public UnableToUnregisterUserException(String user, String cause){
		this.message = "Unable to unregister user "+user+" because, "+cause;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}

}
