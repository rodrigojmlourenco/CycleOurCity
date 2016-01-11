package org.cycleourcity.otp.coc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cycleourcity.driver.StreetEdgeManagementDriver;
import org.cycleourcity.driver.database.structures.CustomStreetEdge;
import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.StreetEdgeWithRating;
import org.cycleourcity.driver.database.structures.UserRating;
import org.cycleourcity.driver.impl.StreetEdgeManagementDriverImpl;
import org.cycleourcity.driver.utils.CriteriaUtils.Criteria;
import org.cycleourcity.otp.utils.Utils.Criterion;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Graph;

/**
 * 
 */
public class CycleOurCityBridge {

	//Database driver abstraction layer
	private StreetEdgeManagementDriver manager;

	protected CycleOurCityBridge(Graph graph) {
		manager = StreetEdgeManagementDriverImpl.getManager();


		if(manager.isEmptyMap()){
			List<CustomStreetEdge> cEdges = new ArrayList<>();
			for(StreetEdge e : graph.getStreetEdges()){

				GeoLocation from, to;

				from = new GeoLocation(
						e.getFromVertex().getY(),
						e.getFromVertex().getX());

				to = new GeoLocation(
						e.getToVertex().getY(),
						e.getToVertex().getX());

				CustomStreetEdge aux = 
						new CustomStreetEdge(
								e.getId(),
								e.getName(),
								from, to);

				cEdges.add(aux);
			}

			manager.populateStreetEdges(cEdges);
		}
	}
	
	/**
	 * Fetches all the constant factors associated with the given criterion.
	 * 
	 * @param criteria The criterion of interest.
	 * @return Array containing all the factor of the specified criterion.
	 */
	public double[] exportCriterionFactors(Criteria criteria){

		switch (criteria) {
		case safety:
			return manager.getAllSafetyFactors();
		case elevation:
			return manager.getAllElevationFactors();
		case pavement:
			return manager.getAllPavementFactors();
		case rails:
			return manager.getAllRailsFactors();
		default:
			return null;
		}
	}

	/**
	 * TODO: javadoc
	 * @return
	 */
	public HashMap<Double, List<UserRating>> exportSafetyRatings(){		
		return manager.getAllSafetyRatings();		
	}

	/**
	 * TODO: javadoc
	 * @return
	 */
	public HashMap<Double, List<UserRating>> exportElevationRatings(){
		return manager.getAllElevationRatings();	
	}

	/**
	 * TODO: javadoc
	 * @return
	 */
	public HashMap<Double, List<UserRating>> exportPaveRatings(){
		return manager.getAllPavementRatings();	
	}

	/**
	 * TODO: javadoc
	 * @return
	 */
	public HashMap<Double, List<UserRating>> exportRailsRatings(){
		return manager.getAllRailsRatings();
	}

	/**
	 * Fetches the list of street edges and corresponding safety ratings
	 * made by the specified user.
	 * 
	 * @param userId The user unique identifier
	 * 
	 * @return List of StreetEdgeWithRating
	 */
	public List<StreetEdgeWithRating> exportSafetyRatingsByUserId(Long userId){
		return manager.getAllSafetyRatings(userId);
	}

	/**
	 * Fetches the list of street edges and corresponding elevation ratings
	 * made by the specified user.
	 * 
	 * @param userId The user unique identifier
	 * 
	 * @return List of StreetEdgeWithRating
	 */
	public List<StreetEdgeWithRating> exportElevationRatingsByUserId(long userId){
		return manager.getAllElevationRatings(userId);
	}

	/**
	 * Fetches the list of street edges and corresponding pavement ratings
	 * made by the specified user.
	 * 
	 * @param userId The user unique identifier
	 * 
	 * @return List of StreetEdgeWithRating
	 */
	public List<StreetEdgeWithRating> exportPavementRatingsByUserId(long userId){
		return manager.getAllPavementRatings(userId);
	}

	/**
	 * Fetches the list of street edges and corresponding rails ratings
	 * made by the specified user.
	 * 
	 * @param userId The user unique identifier
	 * 
	 * @return List of StreetEdgeWithRating
	 */
	public List<StreetEdgeWithRating> exportRailsRatingsByUserId(long userId){
		return manager.getAllRailsRatings(userId);
	}
	
	// AWAITING REVIEW
	/////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////


	public void insertConsolidadedElevationRatings(HashMap<Double, Integer> consolidatedElevationRatings){
		manager.updateConsolidatedElevationRatings(consolidatedElevationRatings);
	}

	public void insertConsolidadedSafetyRatings(HashMap<Double, Integer> consolidatedSafetyRatings){
		manager.updateConsolidatedSafetyRatings(consolidatedSafetyRatings);
	}

	public void insertConsolidadedPaveRatings(HashMap<Double, Integer> consolidatedPaveRatings){
		manager.updateConsolidatedPavementRatings(consolidatedPaveRatings);
	}

	public void insertConsolidadedRailsRatings(HashMap<Double, Integer> consolidatedRailsRatings){
		manager.updateConsolidatedRailsRatings(consolidatedRailsRatings);
	}
}