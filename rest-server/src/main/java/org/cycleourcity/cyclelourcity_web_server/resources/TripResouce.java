package org.cycleourcity.cyclelourcity_web_server.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.cycleourcity.cyclelourcity_web_server.middleware.CycleOurCityManager;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.Response;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.trips.DetailedTripResponse;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.trips.TripRegistryRequest;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.trips.UserTripsResponse;
import org.cycleourcity.cyclelourcity_web_server.security.Secured;
import org.cycleourcity.driver.database.structures.SimplifiedTrip;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnknownUserException;
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
	public UserTripsResponse getUserTrips(@QueryParam("user") String username){
		
		String error;
		SimplifiedTrip[] payload;
		List<SimplifiedTrip> tripList;
		
		//TODO: verificar a validade do token de autenticacao
		
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
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public DetailedTripResponse getTrip(){
		
		return null;
	}
	
	/**
	 * <b>SaveTrip.php</b>
	 * Register a new trip, which is characterized by its name and a set of street edges,
	 * and that belongs to the specified user.
	 * 
	 * @param r A TripRegistryRequest that contains all the information required.
	 * @return Reponse
	 */
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveTrip(TripRegistryRequest r){
		return new Response(500, "Method not implemented yet!");
	}
}
