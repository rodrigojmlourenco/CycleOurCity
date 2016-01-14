package org.cycleourcity.cyclelourcity_web_server.resources;

import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cycleourcity.cyclelourcity_web_server.middleware.CycleOurCityManager;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.Response;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.street.GeometryRating;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.street.RateTripRequest;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.street.RatedGeometriesResponse;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.street.RatedStreetsResponse;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.street.StreetEdgeRating;
import org.cycleourcity.driver.exceptions.UnknowStreetEdgeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This end-point allows authenticated users to classify the edges of
 * a certain route. This route must be part of a trip previously requested
 * by them.
 * 
 * @author Rodrigo Lourenço
 *
 */
@Path("/rate")
public class RateStreetsResource {
	
	private final static Logger LOG = LoggerFactory.getLogger(RatedStreetsResponse.class);
	
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();

	@Path("/test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RateTripRequest test(){
		
		StreetEdgeRating[] aux = {new StreetEdgeRating("xyz", 1, 2, 3, 4)};
		
		return new RateTripRequest(123,321, aux);
	}
	
	/**
	 * TODO: this method will fail as the db does not support geometries
	 * 
	 * <b>RatedStreetEdges.php</b>
	 * <br>
	 * Fetches all the geometries of all street edges.
	 * <br>
	 * <b>WHY? - what is the purpose of this function</b>

	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RatedStreetsResponse getRatedStreets(){
		
		LOG.info("Fetching the geometries of all rated streets.");
		
		List<String> geometriesList = manager.getRatedStreetEdgesGeometries();
		
		String[] geometries = new String[geometriesList.size()];
		geometriesList.toArray(geometries);
		
		return new RatedStreetsResponse(geometries);
	}
	
	@Path("/geometries")
	@Produces(MediaType.APPLICATION_JSON)
	public RatedGeometriesResponse getRatedGeometries(){
		
		GeometryRating[] safeties, elevations;
		
		
		try {
			List<GeometryRating> safetyList = manager.getSafetyRatedGeometries();
			List<GeometryRating> elevationList = manager.getElevationRatedGeometries();
			
			safeties = new GeometryRating[safetyList.size()];
			elevations=new GeometryRating[elevationList.size()];
			
			safetyList.toArray(safeties);
			elevationList.toArray(elevations);
			
			return new RatedGeometriesResponse(safeties, elevations);
			
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		
		
		
	}
	
	/**
	 * <b>InsertUserFeedback.php</b>
	 *  
	 * <b>Errors:</b>
	 * <br>
	 * <ul>
	 * 	<li>User is not authenticated.</li>
	 * 	<li>Trip edge not found (WHY?)</li>
	 * 	<li>Invalid rate range for safety</li>
	 * 	<li>Invalid rate range for elevation</li>
	 * 	<li>Invalid rate range for pavement</li>
	 * 	<li>Invalid rate range for rails</li>
	 * 	<li>Unexpected error</li>
	 * </ul>
	 * 
	 * @param r 
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response rateTrip(RateTripRequest r){
		
		LOG.info("Classifying the streets in trip "+r.getTripId()+" from user"+r.getUserId());
		
		int tripId = r.getTripId();
		int userId = r.getUserId();
		StreetEdgeRating[] ratings = r.getRatings();
		int ratingsCount = ratings.length;

		
		String error="";
		int failed = 0;
		StreetEdgeRating aux;
		boolean isLast = false;
		
		for(int i=0; i < ratingsCount; i++){
			
			aux = ratings[i];
			
			if(i >= (ratingsCount -1))
				isLast = true;
			
			try {
				manager.classifyStreetEdge(
						userId, tripId, 
						aux.getStreetEdgeId(),
						aux.getSafetyRate(),
						aux.getElevationRate(),
						aux.getPavementRate(),
						aux.getRailsRate(),
						isLast);
			} catch (UnknowStreetEdgeException e) {
				e.printStackTrace();
				failed++;
				error += "\n"+e.getMessage();
			} 
		}
		
		//TODO: change this
		if(failed>0)
			return new Response(500, ""+failed+" streets were not classified. Reasons: "+error);	
		else
			return new Response(500, "Unable to handle trip "+r.getTripId()+". Method not implemented yet!");
	}
}
