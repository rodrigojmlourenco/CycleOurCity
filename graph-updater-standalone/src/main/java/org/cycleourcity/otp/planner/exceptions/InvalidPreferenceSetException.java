package org.cycleourcity.otp.planner.exceptions;

public class InvalidPreferenceSetException extends Exception {

	private static final long serialVersionUID = 3499378186735780028L;
	
	private final String message = "The sum of the user's preferences must be equal to 1";
	
	@Override
	public String getMessage() {
		return message;
	}
}
