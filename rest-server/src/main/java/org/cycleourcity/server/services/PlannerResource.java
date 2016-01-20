/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.cycleourcity.server.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnknownUserException;
import org.cycleourcity.otp.planner.RoutePlanner;
import org.cycleourcity.otp.planner.exceptions.InvalidPreferenceSetException;
import org.cycleourcity.server.middleware.CycleOurCityManager;
import org.cycleourcity.server.resources.elements.planner.RoutePlanRequest;
import org.cycleourcity.server.resources.elements.planner.RoutePlanResponse;
import org.cycleourcity.server.security.Secured;
import org.opentripplanner.api.model.TripPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This is the end-point for requesting route planning recommendations
 * 
 * @author Rodrigo Lourenço
 */
@Path("/route")
public class PlannerResource {

	
	private static Logger LOG = LoggerFactory.getLogger(PlannerResource.class);
	
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();

	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public RoutePlanRequest test(){
		return new RoutePlanRequest(
				38.7495721,-9.142133, //From
				38.7423355,-9.1399701, //To
				0.33f,0.33f,0.33f);
	}
	

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

			return new RoutePlanResponse(planner.getTripPlan(), "success");

		} catch (InvalidPreferenceSetException e) {
			LOG.error(e.getMessage());
			error = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RoutePlanResponse(null, error);
	}
	
	@Path("/{user}")
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response planRouteAndSave(
			@PathParam("user") String user,
			RoutePlanRequest request,
			@Context SecurityContext context){
		
		TripPlan plan;
		
		if(!user.equals(context.getUserPrincipal().getName()))
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity("The provided user name does not math the token's subject")
					.build();
		
		try {

			//Step 1 - Plan the route
			RoutePlanner planner = manager.planRoute(request);
			planner.run();
			plan = planner.getTripPlan();

			//Step 2 - Save the trip and its street edges
			try {
				manager.saveTrip(context.getUserPrincipal().getName() ,plan);
			} catch (UnknownUserException e) {
				LOG.error(e.getMessage());
				return Response.status(Response.Status.UNAUTHORIZED)
						.entity(e.getMessage())
						.build();
			} catch (UnableToPerformOperation e) {
				LOG.error(e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(e.getMessage())
						.build();
			}
			
			return Response.ok()
					.entity(new RoutePlanResponse(planner.getTripPlan(), "success"))
					.build();

		} catch (InvalidPreferenceSetException e) {
			LOG.error(e.getMessage());
			return Response.status(Response.Status.METHOD_NOT_ALLOWED)
					.entity(e.getMessage())
					.build();
		} 
	}
}