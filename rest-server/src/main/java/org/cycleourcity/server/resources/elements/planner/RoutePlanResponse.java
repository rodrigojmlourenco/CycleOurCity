package org.cycleourcity.server.resources.elements.planner;

import javax.xml.bind.annotation.XmlRootElement;
import org.opentripplanner.api.model.TripPlan;

@XmlRootElement
public class RoutePlanResponse {
	
	private String error;
	private TripPlan plan;
	
	public RoutePlanResponse(){}
	
	public RoutePlanResponse(TripPlan plan, String error) {
		this.plan = plan;
		this.error = error;
	}

	public TripPlan getPlan() {
		return plan;
	}

	public void setPlan(TripPlan plan) {
		this.plan = plan;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}

}
