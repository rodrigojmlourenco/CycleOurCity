package org.cycleourcity.otp.planner;

import org.opentripplanner.api.model.TripPlan;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.standalone.Router;

public abstract class RoutePlanner implements Runnable{
	
	private final Router mRouter;
	private RoutingRequest mRequest;
	private TripPlan plan;
	
	public RoutePlanner(Router router, RoutingRequest request){
		this.mRouter = router;
		this.mRequest = request;
	}
	
	protected final void setRoutingRequest(RoutingRequest request){
		this.mRequest = request;
	}
	
	protected final Router getRouter(){
		return this.mRouter;
	}
	
	protected final RoutingRequest getRoutingRequest(){
		return this.mRequest;
	}
	
	public abstract TripPlan planRoute();
	
	public TripPlan getTripPlan(){
		return this.plan;
	}
	
	@Override
	public void run() {
		this.plan = planRoute();
	}
}
