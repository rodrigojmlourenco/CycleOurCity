package org.cycleourcity.cyclelourcity_web_server.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cycleourcity.cyclelourcity_web_server.middleware.CycleOurCityManager;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.planner.RoutePlanRequest;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.planner.RoutePlanResponse;
import org.cycleourcity.otp.planner.RoutePlanner;
import org.cycleourcity.otp.planner.exceptions.InvalidPreferenceSetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opentripplanner.api.model.TripPlan;
/**
 * This is the end-point for requesting route planning recommendations
 * 
 * @author Rodrigo Lourenço
 */
@Path("/route")
public class PlannerResource {

	
	private static Logger LOG = LoggerFactory.getLogger(PlannerResource.class);
	
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();

	/*
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public RoutePlanRequest test(){
		return new RoutePlanRequest(
				38.7495721,-9.142133, //From
				38.7423355,-9.1399701, //To
				0.33f,0.33f,0.33f);
	}
	*/

	//TODO: este método pode ser autenticado ou nao... como proceder?
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RoutePlanResponse planRoute(RoutePlanRequest request){

		String error = null;
		TripPlan plan;
		
		try {

			//Step 1 - Plan the route
			RoutePlanner planner = manager.planRoute(request);
			planner.run();
			plan = planner.getTripPlan();

			//Step 2 - Save the trip and its street edges
			manager.saveTrip(plan);
			
			return new RoutePlanResponse(planner.getTripPlan(), "success");

		} catch (InvalidPreferenceSetException e) {
			LOG.error(e.getMessage());
			error = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RoutePlanResponse(null, error);
	}
}