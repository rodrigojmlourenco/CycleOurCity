package org.cycleourcity.otp.graph.exceptions;

public class UnableToSerializeGraph extends Exception {

	private static final long serialVersionUID = 3301907706442646869L;

	private String message;
	
	public UnableToSerializeGraph(String message){
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
