package org.cycleourcity.cyclelourcity_web_server.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.cycleourcity.cyclelourcity_web_server.resources.elements.Response;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.trips.DetailedTripResponse;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.trips.TripRegistryRequest;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.trips.UserTripsResponse;

@Path("/trip")
public class TripResouce {

	/**
	 * <b>UserTrips.php</b>
	 * 
	 * Fetches all the trips associated with a specific user.
	 * 
	 * @param username The user's username
	 * @return UserTripsResponse
	 */
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public UserTripsResponse getUserTrips(@QueryParam("username") String username){
		return null;
	}
	
	/**
	 * <b>UserTrips.php</b>
	 * 
	 * Fetches the details of a certain a certain trip, belonging to
	 * a certain user.
	 * 
	 * @param username The user's username.
	 * @param tripId The request trip
	 * 
	 * @return DetailedTripResponse 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DetailedTripResponse getTrip(@QueryParam("username")String username, @QueryParam("tripId")int tripId){
		
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
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveTrip(TripRegistryRequest r){
		return new Response(500, "Method not implemented yet!");
	}
}
