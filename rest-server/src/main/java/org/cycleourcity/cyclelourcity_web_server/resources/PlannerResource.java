package org.cycleourcity.cyclelourcity_web_server.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cycleourcity.cyclelourcity_web_server.middleware.CycleOurCityManager;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.planner.RoutePlanRequest;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.planner.RoutePlanResponse;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

/**
 * This is the end-point for requesting route planning recommendations
 * 
 * @author samm
 */
@Path("/route")
public class PlannerResource {

	private Gson gson = new Gson();
	private JsonParser parser = new JsonParser();
			
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public String test(){
		return "its alive!!!";
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RoutePlanResponse planRoute(@PathParam("request") RoutePlanRequest request){
		/*
		String error = null;
		try {
			RoutePlanner planner = manager.planRoute(request);
			planner.run();
			planner.getTripPlan();
		} catch (InvalidPreferenceSetException e) {
			e.printStackTrace();
			error = e.getMessage();
		}
		/*
		 */
		
		RoutePlanResponse response = new RoutePlanResponse();
		response.setError("Not implemented yet!");
		
		return response;
	}
}
