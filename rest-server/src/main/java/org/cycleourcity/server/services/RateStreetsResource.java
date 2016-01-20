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

import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnknowStreetEdgeException;
import org.cycleourcity.driver.exceptions.UnknownUserException;
import org.cycleourcity.server.middleware.CycleOurCityManager;
import org.cycleourcity.server.resources.elements.street.GeometryRating;
import org.cycleourcity.server.resources.elements.street.RateTripRequest;
import org.cycleourcity.server.resources.elements.street.RatedGeometriesResponse;
import org.cycleourcity.server.resources.elements.street.RatedStreetsResponse;
import org.cycleourcity.server.resources.elements.street.StreetEdgeRating;
import org.cycleourcity.server.security.Secured;
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
@Path("/streets")
public class RateStreetsResource {
	
	private final static Logger LOG = LoggerFactory.getLogger(RateStreetsResource.class);
	
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();

	/*
	@Path("/test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RateTripRequest test(){
		
		StreetEdgeRating[] aux = {new StreetEdgeRating("xyz", 1, 2, 3, 4)};
		
		return new RateTripRequest(123,321, aux);
	}
	*/
	
	/**
	 * <b>NOTE: </b>After some static code analysis it was determined that
	 * this function is never called.
	 * <br>
	 * <b>RatedStreetEdges.php</b>
	 * <br>
	 * Fetches all the geometries of all street edges.
	 * <br>
	 * <b>WHY? - what is the purpose of this function</b>
	 * 
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

	/**
	 * <b>NOTE: </b>After some static code analysis it was determined that
	 * this function is never called.
	 * @return
	 */
	//@Path("/geometries")
	//@GET
	@Deprecated
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
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response rateTrip(
			RateTripRequest r,
			@Context SecurityContext context){

		int userId;
		int tripId = r.getTripId();
		StreetEdgeRating[] ratings = r.getRatings();
		int ratingsCount = ratings.length;
		
		try {
			userId = manager.getUserId(context.getUserPrincipal().getName());
		} catch (UnknownUserException e1) {
			LOG.error(e1.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(e1.getMessage())
					.build();
		} catch (UnableToPerformOperation e1) {
			LOG.error(e1.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(e1.getMessage())
					.build();
		}
		
		
		LOG.info("Classifying the streets in trip "+tripId+" from user"+userId);
		
		
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
			} 
		}
		
		if(failed>0)
			return Response.ok("Success, however "+failed+" street were not classified").build();
		else
			return Response.ok().build();
	}
}