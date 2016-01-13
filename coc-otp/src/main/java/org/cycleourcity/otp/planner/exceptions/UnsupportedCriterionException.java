package org.cycleourcity.otp.planner.exceptions;

import org.cycleourcity.driver.utils.CriteriaUtils.Criteria;

public class UnsupportedCriterionException extends Exception {

	private static final long serialVersionUID = 6782599992885001368L;

	private String message;
	
	public UnsupportedCriterionException(Criteria criterion){
		message = "Unsupported '"+criterion+" criterion.";
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
