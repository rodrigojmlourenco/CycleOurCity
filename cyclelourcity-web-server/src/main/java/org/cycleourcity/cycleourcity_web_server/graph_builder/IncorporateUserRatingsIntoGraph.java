package org.cycleourcity.cycleourcity_web_server.graph_builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.datatype.UserRating;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.AccountManagementLayer;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.AccountManager;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.StreetEdgeManagement;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.StreetEdgeManager;
import org.cycleourcity.cyclelourcity_web_server.otp_routing.PlainStreetEdge;
import org.cycleourcity.cycleourcity_web_server.graph_builder.Utils.Criterion;
import org.cycleourcity.cycleourcity_web_server.graph_builder.exceptions.EmptyMapException;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;

/**
 */
public class IncorporateUserRatingsIntoGraph {

	protected static final String
		NEW_GRAPH = System.getenv("HOME")+"/otp/graph/new/Graph.obj",
		OLD_GRAPH = System.getenv("HOME")+"/otp/graph/old/Graph.obj";
	
	/** The factors, which are basically constants, that characterize the safety values */
	private double[] _safetyRatingsToFactors;
	/** The factors, which are basically constants, that characterize the elevation values */
	private double[] _elevationRatingsToFactors;
	
	//<IdTroço, ratings>
	/** All the street edges classified in terms of safety */
	HashMap<Long,List<UserRating>> usersSafetyRatings;
	/** All the street edges classified in terms of elevation */
	HashMap<Long,List<UserRating>> usersElevationRatings;
	
	//<UserId, ratings>
	/** All the street edges classified, in terms of safety, by a specific user */
	HashMap<Long, ArrayList<StreetEdgeWithRating>> safetyRatingsByUser;
	/** All the street edges classified, in terms of elevation, by a specific user */
	HashMap<Long, ArrayList<StreetEdgeWithRating>> elevationRatingsByUser;
	

	/** The reputation off all users in terms of the safety criteria */
	HashMap<Long, Double> safetyReputationByUser;
	
	/** The reputation off all users in terms of the elevation criteria */
	HashMap<Long, Double> elevationReputationByUser;
	
	
	private ExportRatings _exportRatings;
	
	private Graph _graph;
	
	private AccountManagementLayer accManager 	= AccountManager.getManager();
	private StreetEdgeManagement streetManager 	= StreetEdgeManager.getManager(); 
	
	/**
	 * @param database The name of the database that contains the users classifications
	 * @param graphFileName The name of the file that holds the current version of the graph.
	 * @throws EmptyMapException 
	 */
	public IncorporateUserRatingsIntoGraph(String baseGraph) throws EmptyMapException{
		
		try {
			_graph = Graph.load(new File(baseGraph), Graph.LoadLevel.DEBUG);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			_exportRatings = new ExportRatings();
		} catch (EmptyMapException e) {
			streetManager.populateStreetEdges(_graph);
			throw e; 
		}
		
		/*
		_safetyRatingsToFactors = _exportRatings.exportCriterionFactors(Criterion.safety);
		_elevationRatingsToFactors = _exportRatings.exportCriterionFactors(Criterion.elevation);
		
		usersSafetyRatings = _exportRatings.exportSafetyRatings();
		usersElevationRatings = _exportRatings.exportElevationRatings();
		
		
		safetyRatingsByUser = new HashMap<Long, ArrayList<StreetEdgeWithRating>>();
		elevationRatingsByUser = new HashMap<Long, ArrayList<StreetEdgeWithRating>>();
		
		//int numUsers = _exportRatings.getNumberOfUsers();
		ArrayList<Long> users = (ArrayList<Long>) accManager.getAllUsersIDs();
		
		//for(int userId = 1; userId <= numUsers; userId++){
		for(Long userId : users){
			//TODO: handle this shit
			//ArrayList<StreetEdgeWithRating> safetyRatings = _exportRatings.exportSafetyRatingsByUserId(userId, usersSafetyRatings);
			//ArrayList<StreetEdgeWithRating> elevationRatings = _exportRatings.exportElevationRatingsByUserId(userId, usersElevationRatings); 
			
			//safetyRatingsByUser.put(userId, safetyRatings);
			//elevationRatingsByUser.put(userId, elevationRatings);
		}
		
		safetyReputationByUser = new HashMap<Long, Double>();
		elevationReputationByUser = new HashMap<Long, Double>();
		
		for(Long userId : users){
			computeReputation(userId);
		}
		*/
	}
	
	/**
	 * Computes the reputation of a specific user, both in terms of the safety and elevation
	 * classification criteria.
	 * <br>
	 * The results of this computation are then store on maps, which will hold the reputations
	 * off all users. 
	 * 
	 * @param userId A long that uniquely identifies a specific user.
	 *
	private void computeReputation(Long userId){		
		double safetyReputation = consolidateSimilarityMeasureOfRatings(safetyRatingsByUser.get(userId), usersSafetyRatings, safetyRatingsByUser.get(userId).size(), 0.2);
	    double elevationReputation = consolidateSimilarityMeasureOfRatings(elevationRatingsByUser.get(userId), usersElevationRatings, elevationRatingsByUser.get(userId).size(), 0.2);
	    
	    safetyReputationByUser.put(userId, new Double(safetyReputation));
	    elevationReputationByUser.put(userId, new Double(elevationReputation));
	}
	*/
	
	/**
	 * Computes a user's reputation factor, given all of his classified street edges, and the
	 * classifications performed by the other users, according to some classification criteria.
	 * <br>
	 * This is a recursive function that computes the reputation for all of the user's streets
	 * <br>
	 * Two different reputation computations are specified according to the number of classifications
	 * performed by the user. First the reputation is computed by the following function:
	 * <br>
	 * <ul>
	 * 	<li>For more than 5 classifications, the result is multiplied by a factor of (1-y)</li>
	 * 	<li>Otherwise, the result is multiplied by a factor of (n-1)*y</li>
	 * </ul>
	 * 
	 * @param ratingsByUser List containing the list of street edges classified by a specific user
	 * @param usersRatings Map containing all the classified street edges, according to some classification criteria  
	 * @param n Number of classifications performed by the user
	 * @param y Reputation Factor (?) UNKNOWN - this is a percentage
	 *  
	 * @return The user's reputation factor
	 *
	private double consolidateSimilarityMeasureOfRatings(
			ArrayList<StreetEdgeWithRating> ratingsByUser,
			HashMap<Long, ArrayList<UserRating>> usersRatings,
			int n, double y){
		
		if(n == 0) return 0;
		
		
		if(n > 5){
			double value = computeSimilarityMeasureOfRating(ratingsByUser.get(ratingsByUser.size() - n), usersRatings)*y; 
			return value + consolidateSimilarityMeasureOfRatings(ratingsByUser, usersRatings, n - 1, y)*(1-y);
		} else{
			double value = computeSimilarityMeasureOfRating(ratingsByUser.get(ratingsByUser.size() - n), usersRatings)*y; 
			return value + consolidateSimilarityMeasureOfRatings(ratingsByUser, usersRatings, n - 1, y)*(n-1)*y;
		}
		
	}
	*/
	
	/**
	 * Given a specific street edge and the classification provided by a specific user, this function
	 * compares the user's classification with the other classifications, provided by other users. This
	 * is performed in order to determine if the user's response corresponds to the ones provided by the
	 * overall population, and if therefore the user can be trusted.
	 * 
	 * @param sewr A specific street edge, which will be the basis of the computation
	 * @param usersRatings A map containing all the street edges classified by the users.
	 * 
	 * @return A user's reputation for a specific street edge. The reputation is a value that belongs to the [-1, 1] interval.
	 *
	private double computeSimilarityMeasureOfRating(
			StreetEdgeWithRating sewr,
			HashMap<Long, ArrayList<UserRating>> usersRatings){
		
		double average = computeAverage(usersRatings.get(sewr.getStreetEdgeId()));
		double standardDeviation = computeStandardDeviation(usersRatings.get(sewr.getStreetEdgeId()), average);
		
		if(sewr.getLastRating() >= (average - 0.5*standardDeviation) && sewr.getLastRating() <= (average + 0.5*standardDeviation)){
			return 1.0;
		}
		if(sewr.getLastRating() >= (average - 1*standardDeviation) && sewr.getLastRating() <= (average + 1*standardDeviation)){
			return 0.5;
		}
		if(sewr.getLastRating() >= (average - 1.5*standardDeviation) && sewr.getLastRating() <= (average + 1.5*standardDeviation)){
			return 0.0;
		}
		if(sewr.getLastRating() >= (average - 2.0*standardDeviation) && sewr.getLastRating() <= (average + 2.0*standardDeviation)){
			return -0.5;
		}
		else{
			return -1.0;
		}	
	}
	*/
	
	/**
	 * Computes the average rating of a specific criterion for a specific street edge.
	 * 
	 * @param usersRatings List of users' rating for a specific street edge
	 * @return Average rating
	 *
	private double computeAverage(ArrayList<UserRating> usersRatings){
		double average = 0;
		
		for(UserRating userRating : usersRatings){
			int size = userRating.getRatings().size(); 
			average += userRating.getRatings().get(size - 1);
		}
		
		average = average / usersRatings.size();
		
		return average;
	}
	*/
	
	/**
	 * Computes the ratings' standard deviation of a specific criterion for a specific street edge.
	 * 
	 * @param usersRatings List of users' rating for a specific street edge
	 * @return Ratings' standard deviation
	 *
	private double computeStandardDeviation(ArrayList<UserRating> usersRatings, double average){
		int numRatings = usersRatings.size();
		double sum = 0;
		
		for(UserRating userRating : usersRatings){
			int size = userRating.getRatings().size(); 
			sum = sum + (userRating.getRatings().get(size - 1) - average) * (userRating.getRatings().get(size - 1) - average) ;
		}
		
		sum /= numRatings;
		
		return Math.sqrt(sum);
	}
	*/
	
	/**
	 * Updates identifier and safety rating of a PlainStreetEdge identified by pse.
	 * <br>
 	 * <b>Note:</b> This is the method that actually updates the graph.
	 * 
	 * @param pse Integer that identifies a specific PlainStreetEdge
	 * @param factor The rating
	 * @param id The new identifier
	 * 
	 * @see PlainStreetEdge
	 */
	private void setSafetyFactorAndIdToPlainStreetEdge(int pse, double factor, int id){
		
		StreetEdge edge = (StreetEdge) _graph.getEdgeById(pse);
		edge.setBicycleSafetyFactor((float) factor);
		/*PlainStreetEdge streetEdge = Edge _graph.getEdgeById(pse);
		streetEdge.setSafetyCost(factor);
		streetEdge.setSafetyId(id);*/
	}
	

	/**
	 * Updates identifier and elevation rating of a PlainStreetEdge identified by pse.
	 * <br>
	 * <b>Note:</b> This is the method that actually updates the graph.
	 * 
	 * @param pse Integer that identifies a specific PlainStreetEdge
	 * @param factor The rating
	 * @param id The new identifier
	 * 
	 * @see PlainStreetEdge
	 *
	private void setElevationFactorAndIdToPlainStreetEdge(int pse, double factor, int id){
		
		PlainStreetEdge streetEdge = (PlainStreetEdge) _graph.getEdgeById(pse);
		
		streetEdge.setElevationCost(factor);
		streetEdge.setElevationId(id);
	}
	*/
	
	/**
	 * Computes the overall classification rating for a specific street edge.
	 * <br>
	 * A street edge's overall classification rating is computed the sum of
	 * all classification times the users' reputation, and divided by the
	 * sum of the users' reputations.
	 * <br>
	 * Otherwise, even if only one user has classified that specific street edge, 
	 * and even if he presents a bad reputation (0), then his classification is 
	 * the one adopted to characterize that street according to the defined criterion.
	 * <br>
	 * 
	 * <b>Note</b> The criteria 3 and 4 are not implemented.  
	 * 
	 * @param usersRatings A list of all the users' ratings for a specific street edge.
	 * @param criterion The criterion being computed, where: 1 - safety; 2 - elevation; 3 - pavement; 4 - rails.
	 * 
	 * @return The street edge's overall rating, for the specified criterion.
	 */
	private long computeOverallRating(List<UserRating> usersRatings, Criterion criterion){
		
		double sum = 0;
		double denominator = 0;
		
		
		HashMap<Long, Double> criterionReputationByUser = null;
		
		if(criterion == Criterion.safety){
			criterionReputationByUser = safetyReputationByUser;
		}
		else if(criterion == Criterion.elevation){
			criterionReputationByUser = elevationReputationByUser;
		}

		
		double userReputation;
		for(UserRating rating : usersRatings){
			
			userReputation = criterionReputationByUser.get(rating.getUserId());

			if(userReputation > 0){
				sum += rating.getRatings().get(rating.getRatings().size() - 1) * userReputation;
				denominator += userReputation; 
			}
		}
		
		if(sum == 0) return usersRatings.get(0).getRatings().get(0);
		else 		 return Math.round((double) sum / denominator);
	}

	/**
	 * Given all the street edges classified by the users, in terms of the safety criterion,
	 * this function updates the graph by updating the identifier and safety factor of
	 * those street edges.
	 *
	@Deprecated
	public void updateGraphSafetyRatings(){
		
		HashMap<Integer, Integer> consolidatedRatings = new HashMap<Integer, Integer>();
				
		for(Long streetEdgeId : usersSafetyRatings.keySet()){
			int overallRating = (int) computeOverallRating(usersSafetyRatings.get(streetEdgeId), Criterion.safety);
			consolidatedRatings.put(streetEdgeId.intValue(), overallRating);
			
			double overallSafetyFactor = _safetyRatingsToFactors[overallRating - 1];			
			setSafetyFactorAndIdToPlainStreetEdge(streetEdgeId.intValue(), overallSafetyFactor, overallRating);
		}
		
		_exportRatings.insertConsolidadedSafetyRatings(consolidatedRatings);
	}
	*/
	
	/**
	 * Given all the street edges classified by the users, in terms of the elevation criterion,
	 * this function updates the graph by updating the identifier and elevation factor of
	 * those street edges.
	 *
	@Deprecated
	public void updateGraphElevationRatings(){
		
		HashMap<Integer, Integer> consolidatedRatings = new HashMap<Integer, Integer>();
		
		for(Long streetEdgeId : usersElevationRatings.keySet()){
			int overallRating = (int) computeOverallRating(usersElevationRatings.get(streetEdgeId), Criterion.elevation);
			consolidatedRatings.put(streetEdgeId.intValue(), overallRating);
			
			double overallElevationFactor = _elevationRatingsToFactors[overallRating - 1];
			setElevationFactorAndIdToPlainStreetEdge(streetEdgeId.intValue(), overallElevationFactor, overallRating);
		}
		
		_exportRatings.insertConsolidadedElevationRatings(consolidatedRatings);
	}
	*/
	
	/**
	 * Given all the street edges classified by the users, in terms of the criterion,
	 * this function updates the graph by updating the identifier and criterion factor of
	 * those street edges.
	 * 
	 * @param criterion Criterion that identifies the classification criterion
	 */
	public void updateGraph(Criterion criterion){
		HashMap<Integer, Integer> consolidatedRatings = new HashMap<Integer, Integer>();
		
		for(Long streetEdgeId : usersElevationRatings.keySet()){
			int overallRating = (int) computeOverallRating(usersElevationRatings.get(streetEdgeId), criterion);
			consolidatedRatings.put(streetEdgeId.intValue(), overallRating);
			
			double factor;
			switch (criterion) {
			case safety:
				factor = _safetyRatingsToFactors[overallRating - 1];			
				setSafetyFactorAndIdToPlainStreetEdge(
						streetEdgeId.intValue(),
						factor,
						overallRating);
				break;
			case elevation:
				factor = _elevationRatingsToFactors[overallRating - 1];
				/*setElevationFactorAndIdToPlainStreetEdge(
						streetEdgeId.intValue(),
						factor,
						overallRating);*/ //TODO
				break;
			default:
				//TODO: throw exception or something, as the remaining criteria are not implemented
				break;
			}
		}
		
		_exportRatings.insertConsolidadedElevationRatings(consolidatedRatings);
	}
	
	
	/**
	 * Exports the modifications performed over the graph, into a new graph file.
	 * @param filename The new graph filename.
	 *
	public void saveChanges(String filename){
		try {
			_graph.save(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	public static void main(String[] args) {
		
		IncorporateUserRatingsIntoGraph graphUpdater;
		
		try {
			graphUpdater = new IncorporateUserRatingsIntoGraph(OLD_GRAPH);
			graphUpdater.updateGraph(Criterion.safety);
			//graphUpdater.updateGraph(Criterion.elevation);

			//graphUpdater.saveChanges(NEW_GRAPH);
			//System.out.println("Sucesso!");
		} catch (EmptyMapException e) {
			System.out.println("First time execution, street edges populated...");
			return;
		}
		
		 
	}
}
