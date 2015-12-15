package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions;

public class InvalidEmailException extends UserRegistryException {

	private static final long serialVersionUID = 4884361029755682415L;

	private final String message;
	
	public InvalidEmailException(String email){
		this.message = "The email '"+email+"' is not a valid one.";
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
