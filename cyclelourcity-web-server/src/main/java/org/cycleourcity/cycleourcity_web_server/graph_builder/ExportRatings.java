package org.cycleourcity.cycleourcity_web_server.graph_builder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.datatype.UserRating;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.StreetEdgeManagement;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.StreetEdgeManager;
import org.cycleourcity.cycleourcity_web_server.graph_builder.Utils.Criterion;
import org.cycleourcity.cycleourcity_web_server.graph_builder.Utils.DatabaseFields;
import org.cycleourcity.cycleourcity_web_server.graph_builder.exceptions.EmptyMapException;

/**
 * 
 */
public class ExportRatings {
    
	//Database driver abstraction layer
	private StreetEdgeManagement manager;
	
	public ExportRatings() throws EmptyMapException{
		manager = StreetEdgeManager.getManager();
		
		if(manager.isEmptyMap())
			throw new EmptyMapException();
	}
	
	
	
	/**
	 * Given a set of ratings, which pertains to the specified criterion, the method returns
	 * the set of ratings as a map indexed by the street edges, and comprised of users ratings.
	 *  
	 * @param ratingSet Set containing all the ratings
	 * @param criteria Specified criterion 
	 * 
	 * @return Map of all user-based ratings.
	 */
    private HashMap<Long, ArrayList<UserRating>> exportRatings(ResultSet ratingSet, Criterion criteria){
    	
    	
    	//A chave é o id do troço
		HashMap<Long, ArrayList<UserRating>> streetEdgesMap = new HashMap<Long, ArrayList<UserRating>>();
    	
    	String ratingLabel; 
    	switch (criteria) {
		case safety:
			ratingLabel = DatabaseFields.SAFETY_CRITERION;
			break;
		case elevation:
			ratingLabel = DatabaseFields.ELEVATION_CRITERION;
			break;
		case pavement:
			ratingLabel = DatabaseFields.PAVEMENT_CRITERION;
			break;
		case rails:
			ratingLabel = DatabaseFields.RAILS_CRITERION;
			break;
		default:
			return null;
		}
    	
		
		try {
			
			Long streetEdgeId, rating, userId;
			String username;
			
			while(ratingSet.next()){
				
				streetEdgeId = ratingSet.getLong(DatabaseFields.STREETEDGE_ID);
				rating = ratingSet.getLong(ratingLabel);
				userId = ratingSet.getLong(DatabaseFields.USER_ID);
				username = ratingSet.getString(DatabaseFields.USERNAME );
				
				//New entry
				//Creates and updates a new UserRating object
				//and then adds it to the Street Edges' Map
				if(!streetEdgesMap.containsKey(streetEdgeId)){
					UserRating userRating = new UserRating(userId, username);
					userRating.addRating(rating);
					
					ArrayList<UserRating> usersList = new ArrayList<UserRating>();
					usersList.add(userRating);
					
					streetEdgesMap.put(streetEdgeId, usersList);
				}
				else{ //Otherwise, updates the already existing entry
					
					ArrayList<UserRating> usersList = streetEdgesMap.get(streetEdgeId);
					
					//será que o utilizador já classificou o troço?
					UserRating userAlreadyExists = null;
					
					for(UserRating ur : usersList){
						if(ur.getUserId().equals(userId)){
							userAlreadyExists = ur;
							break;
						}
					}
					
					if(userAlreadyExists != null){
						//já classificou o troço, por isso adiciono ao objecto já existente.
						userAlreadyExists.addRating(rating);
					}
					else{ //Otherwise, creates a new entry for the newly found user
						UserRating userRating = new UserRating(userId, username);
						userRating.addRating(rating);
						
						usersList.add(userRating);
						
						streetEdgesMap.put(streetEdgeId, usersList);						
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return streetEdgesMap;
    }
    
    //devolve mapa <troco, classifacoes>
	public HashMap<Long, List<UserRating>> exportSafetyRatings(){		
		return manager.getAllSafetyRatings();		
	}

	public HashMap<Long, List<UserRating>> exportElevationRatings(){
		return manager.getAllElevationRatings();	
	}
	
	public HashMap<Long, List<UserRating>> exportPaveRatings(){
		return manager.getAllPavementRatings();	
	}
	
	public HashMap<Long, List<UserRating>> exportRailsRatings(){
		return manager.getAllRailsRatings();
	}
	
	/*
	 * A lista já está ordenada: O primeiro elemento correponde à última classificação (classificação mais recente)
	 *
	public ArrayList<StreetEdgeWithRating> exportSafetyRatingsByUserId(Long userId, HashMap<Long, ArrayList<UserRating>> streetEdgesRatings){
		ResultSet result = _dataAcessLayer.getAllSafetyRatingsByUserId(userId);
		return exportRatingsByUserId(result, "IdSafety", streetEdgesRatings);
	}
	
	public ArrayList<StreetEdgeWithRating> exportElevationRatingsByUserId(Long userId, HashMap<Long, ArrayList<UserRating>> streetEdgesRatings){
		ResultSet result = _dataAcessLayer.getAllElevationRatingsByUserId(userId);
		return exportRatingsByUserId(result, "IdElevation", streetEdgesRatings);
	}
	
	public ArrayList<StreetEdgeWithRating> exportPaveRatingsByUserId(Long userId, HashMap<Long, ArrayList<UserRating>> streetEdgesRatings){
		ResultSet result = _dataAcessLayer.getAllPaveRatingsByUserId(userId);
		return exportRatingsByUserId(result, "IdPave", streetEdgesRatings);
	}
	
	public ArrayList<StreetEdgeWithRating> exportRailsRatingsByUserId(Long userId, HashMap<Long, ArrayList<UserRating>> streetEdgesRatings){
		ResultSet result = _dataAcessLayer.getAllRailsRatingsByUserId(userId);
		return exportRatingsByUserId(result, "IdRails", streetEdgesRatings);
	}
	
	
	private ArrayList<StreetEdgeWithRating> exportRatingsByUserId(Criterion criterion, HashMap<Long, ArrayList<UserRating>> streetEdgesRatings){
		return null;
	}
	
	private ArrayList<StreetEdgeWithRating> exportRatingsByUserId(ResultSet result, String columnRatingLabel, HashMap<Long, ArrayList<UserRating>> streetEdgesRatings){
		
		ArrayList<StreetEdgeWithRating> usersRatings = new ArrayList<StreetEdgeWithRating>();
		
		try {
			while(result.next()){
				Long streetEdgeId = result.getLong("IdStreetEdge");
				Long rating = result.getLong(columnRatingLabel);
				
				if(streetEdgesRatings.get(streetEdgeId).size() > 1){
					//há pelo menos dois utilizadores que classificaram esse troço
					boolean flag = false;
	
					for(StreetEdgeWithRating stwr : usersRatings){
						if(stwr.getStreetEdgeId().equals(streetEdgeId)){
							flag = true;
							break;
						}
					}
					
					if(flag == false){
						//se o troço não existir..
						usersRatings.add(new StreetEdgeWithRating(streetEdgeId, rating));
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return usersRatings;
	}
	*/
	
	/**
	 * Fetches the safety factor constants, as specified in the adopted database.
	 * 
	 * @return Array containing all the safety factors.
	 *
	@Deprecated
	public double[] exportSafetyFactors(){
		
		double[] safetyFactors = new double[6];
		ResultSet result = _dataAcessLayer.getAllSafetyFactors();
		
		try {
			while(result.next()){
				int id = result.getInt("Id");
				double safetyFactor = result.getDouble("SafetyFactor");
				safetyFactors[id - 1] = safetyFactor;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return safetyFactors;
	}*/
	
	
	/**
	 * Fetches the elevation factor constants, as specified in the adopted database.
	 * 
	 * @return Array containing all the elevation factors.
	 *
	@Deprecated
	public double[] exportElevationFactors(){
		double[] elevationFactors = new double[6];
		ResultSet result = _dataAcessLayer.getAllElevationFactors();
		
		try {
			while(result.next()){
				int id = result.getInt("Id");
				double elevationFactor = result.getDouble("ElevationFactor");
				elevationFactors[id - 1] = elevationFactor;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return elevationFactors;
	}*/
	
	/**
	 * Fetches the pavement factor constants, as specified in the adopted database.
	 * 
	 * @return Array containing all the pavement factors.
	 *
	@Deprecated
	public double[] exportPaveFactors(){
		double[] paveFactors = new double[4];
		ResultSet result = _dataAcessLayer.getAllPaveFactors();
		
		try {
			while(result.next()){
				int id = result.getInt("Id");
				double paveFactor = result.getDouble("PaveFactor");
				paveFactors[id - 1] = paveFactor;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return paveFactors;
	}*/
	
	/**
	 * Fetches the rails factor constants, as specified in the adopted database.
	 * 
	 * @return Array containing all the rails factors.
	 *
	@Deprecated
	public double[] exportRailsFactors(){
		double[] railsFactors = new double[3];
		ResultSet result = _dataAcessLayer.getAllRailsFactors();
		
		try {
			while(result.next()){
				int id = result.getInt("Id");
				double railsFactor = result.getDouble("RailsFactor");
				railsFactors[id - 1] = railsFactor;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return railsFactors;
	}*/
	
	/**
	 * Fetches all the constant factors associated with the given criterion.
	 * 
	 * @param criteria The criterion of interest.
	 * @return Array containing all the factor of the specified criterion.
	 */
	public double[] exportCriterionFactors(Criterion criteria){

		double[] factors;
		ResultSet result;
		
		switch (criteria) {
		case safety:
			return manager.getAllSafetyFactorsIDs();
		case elevation:
			return manager.getAllElevationFactorsIDs();
		case pavement:
			return manager.getAllPavementFactorsIDs();
		case rails:
			return manager.getAllRailsFactorsIDs();
		default:
			return null;
		}
	}
	
	public void insertConsolidadedElevationRatings(HashMap<Integer, Integer> consolidatedElevationRatings){
		manager.clearAndUpdateConsolidatedElevationRatings(consolidatedElevationRatings);
	}
	
	public void insertConsolidadedSafetyRatings(HashMap<Integer, Integer> consolidatedSafetyRatings){
		manager.clearAndUpdateConsolidatedSafetyRatings(consolidatedSafetyRatings);
	}
	
	public void insertConsolidadedPaveRatings(HashMap<Integer, Integer> consolidatedPaveRatings){
		manager.clearAndUpdateConsolidatedPavementRatings(consolidatedPaveRatings);
	}
	
	public void insertConsolidadedRailsRatings(HashMap<Integer, Integer> consolidatedRailsRatings){
		manager.clearAndUpdateConsolidatedRailsRatings(consolidatedRailsRatings);
	}
	
	/*
	public ArrayList<Long> getIdsOfUsers(){
		ArrayList<Long> users = new ArrayList<Long>();
		
		try{
			ResultSet result = _dataAcessLayer.getIdsOfUsers();
			users = new ArrayList<Long>();
			
			while(result.next()){
				users.add(result.getLong("Id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return users;
	}
	*/
}
