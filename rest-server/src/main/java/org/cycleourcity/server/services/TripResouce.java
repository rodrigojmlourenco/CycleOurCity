package org.cycleourcity.server.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.cycleourcity.driver.database.structures.SimplifiedTrip;
import org.cycleourcity.driver.database.structures.Trip;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnknownUserException;
import org.cycleourcity.server.middleware.CycleOurCityManager;
import org.cycleourcity.server.resources.elements.trips.DetailedTripResponse;
import org.cycleourcity.server.resources.elements.trips.UserTripsResponse;
import org.cycleourcity.server.security.Secured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/trip")
public class TripResouce {

	private static Logger LOG = LoggerFactory.getLogger(TripResouce.class);
	
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();
	
	/**
	 * <b>UserTrips.php</b>
	 * 
	 * Fetches all the trips associated with a specific user.
	 * 
	 * @return UserTripsResponse
	 */
	@GET
	@Secured
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public UserTripsResponse getUserTrips(@Context SecurityContext context){
		
		String error;
		SimplifiedTrip[] payload;
		List<SimplifiedTrip> tripList;
		
		String username = context.getUserPrincipal().getName();
		
		try {
			
			tripList = manager.getUserTrips(username);
			payload = new SimplifiedTrip[tripList.size()];
			
			tripList.toArray(payload);
			
			return new UserTripsResponse(payload);
			
		} catch (UnknownUserException | UnableToPerformOperation e) {
			error = e.getMessage();
		}
		
		UserTripsResponse r = new UserTripsResponse();
		r.setError(error);
		return r;
	}
	
	/**
	 * <b>UserTrips.php</b>
	 * 
	 * Fetches the details of a certain a certain trip, belonging to
	 * a certain user.
	 * 
	 * @return DetailedTripResponse 
	 */
	@Path("/list/{trip}")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public DetailedTripResponse getTrip(@PathParam("trip") int trip){
		
		Trip details = manager.getTrip(trip);
		
		return new DetailedTripResponse(
					details.getTripStreetEdges(),
					details.getFromLocation(), details.getToLocation());
	}
}
