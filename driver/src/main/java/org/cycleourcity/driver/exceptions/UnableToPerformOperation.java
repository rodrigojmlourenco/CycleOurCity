package org.cycleourcity.driver.exceptions;

public class UnableToPerformOperation extends Exception {

	private static final long serialVersionUID = 1L;

	private final String message;
	
	public UnableToPerformOperation(String cause){
		this.message = "Unable to perform operation because, "+cause;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
