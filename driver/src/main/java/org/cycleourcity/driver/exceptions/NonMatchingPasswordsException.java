package org.cycleourcity.driver.exceptions;

public class NonMatchingPasswordsException extends Exception {

	private static final long serialVersionUID = 8789642605817630061L;
	
	@Override
	public String getMessage() {
		return "The provided passwords do not match";
	}

}
