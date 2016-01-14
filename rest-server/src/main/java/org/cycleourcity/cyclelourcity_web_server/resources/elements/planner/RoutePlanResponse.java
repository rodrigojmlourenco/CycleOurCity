package org.cycleourcity.cyclelourcity_web_server.resources.elements.planner;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoutePlanResponse {
	
	private String error;
	
	public RoutePlanResponse(){}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
}
