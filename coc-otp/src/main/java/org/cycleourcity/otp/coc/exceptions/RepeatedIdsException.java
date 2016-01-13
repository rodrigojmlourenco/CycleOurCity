package org.cycleourcity.otp.coc.exceptions;

public class RepeatedIdsException extends Exception {

	private static final long serialVersionUID = 7307672629621230903L;
	private String message;
	
	public RepeatedIdsException(int count){
		message = "Found "+count+" repeated Ids";
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
