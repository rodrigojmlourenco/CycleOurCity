package org.cycleourcity.otp.coc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cycleourcity.driver.AccountManagementDriver;
import org.cycleourcity.driver.database.structures.StreetEdgeStatistics;
import org.cycleourcity.driver.database.structures.StreetEdgeWithRating;
import org.cycleourcity.driver.database.structures.UserRating;
import org.cycleourcity.driver.impl.AccountManagementDriverImpl;
import org.cycleourcity.driver.utils.CriteriaUtils.Criteria;
import org.cycleourcity.otp.coc.exceptions.RepeatedIdsException;
import org.cycleourcity.otp.data.UserStats;
import org.cycleourcity.otp.exceptions.EmptyMapException;
import org.cycleourcity.otp.planner.exceptions.UnsupportedCriterionException;
import org.cycleourcity.otp.utils.SafetyUtils;
import org.cycleourcity.otp.utils.SlopeUtils;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class GraphIntegrator {


	private static Logger LOG = LoggerFactory.getLogger(GraphIntegrator.class);

	public static final double REPUTATION_FACTOR = 0.2;

	protected static final String
	NEW_GRAPH = System.getenv("HOME")+"/otp/graph/new/Graph.obj",
	OLD_GRAPH = System.getenv("HOME")+"/otp/graph/old/Graph.obj";

	/** The factors, which are basically constants, that characterize the safety values */
	private double[] _safetyRatingsToFactors;
	/** The factors, which are basically constants, that characterize the elevation values */
	private double[] _elevationRatingsToFactors;

	//<IdTroço, ratings>
	/** All the street edges classified in terms of safety */
	HashMap<String,List<UserRating>> usersSafetyRatings;
	/** All the street edges classified in terms of elevation */
	HashMap<String,List<UserRating>> usersElevationRatings;

	HashMap<String, StreetEdgeStatistics> safetyStats;
	HashMap<String, StreetEdgeStatistics> elevationStats;


	//<UserId, ratings>
	/** All the street edges classified, in terms of safety, by a specific user */
	HashMap<Long, List<StreetEdgeWithRating>> safetyRatingsByUser;
	/** All the street edges classified, in terms of elevation, by a specific user */
	HashMap<Long, List<StreetEdgeWithRating>> elevationRatingsByUser;


	/** The reputation off all users in terms of the safety criteria */
	//HashMap<Long, Double> safetyReputationByUser;

	/** The reputation off all users in terms of the elevation criteria */
	//HashMap<Long, Double> elevationReputationByUser;

	HashMap<Long, UserStats> userStatistics;

	private CycleOurCityBridge _exportRatings;

	private Graph _graph;

	private boolean hasData = false;

	private AccountManagementDriver accManager 	= AccountManagementDriverImpl.getManager();
	//private StreetEdgeManagementDriver streetManager 	= StreetEdgeManagementDriverImpl.getManager(); 

	public GraphIntegrator(Graph graph) throws RepeatedIdsException, EmptyMapException{

		_graph = graph;
		_exportRatings = new CycleOurCityBridge(graph);

		//These are fixed values
		_safetyRatingsToFactors 	= _exportRatings.exportCriterionFactors(Criteria.safety);
		_elevationRatingsToFactors 	= _exportRatings.exportCriterionFactors(Criteria.elevation);

		update();
	}

	public void update() throws EmptyMapException{

		// Step 0 - allocate all support structures
		safetyStats = new HashMap<>();
		elevationStats = new HashMap<>();

		userStatistics = new HashMap<>();

		safetyRatingsByUser = new HashMap<Long, List<StreetEdgeWithRating>>();
		elevationRatingsByUser = new HashMap<Long, List<StreetEdgeWithRating>>();

		usersSafetyRatings = _exportRatings.exportSafetyRatings();
		usersElevationRatings = _exportRatings.exportElevationRatings();

		// Step 1 - WTF? Populate the StreetEdgesWithRatings map?
		//int numUsers = _exportRatings.getNumberOfUsers();
		ArrayList<Long> users = (ArrayList<Long>) accManager.getAllUsersIDs();

		if(!(users == null) && !(users.size() == 0)){//Proceed only if there are registered users

			for(Long userId : users){
				List<StreetEdgeWithRating> safetyRatings = _exportRatings.exportSafetyRatingsByUserId(userId);
				List<StreetEdgeWithRating> elevationRatings = _exportRatings.exportElevationRatingsByUserId(userId); 

				safetyRatingsByUser.put(userId, safetyRatings);
				elevationRatingsByUser.put(userId, elevationRatings);
			}

			// Step 2 - Compute the rating's statistics of each street edge
			if(usersSafetyRatings != null)
				for(String id : usersSafetyRatings.keySet())
					safetyStats.put(id, new StreetEdgeStatistics(id, usersSafetyRatings.get(id)));
			if(usersElevationRatings != null)
				for(String id : usersElevationRatings.keySet())
					elevationStats.put(id, new StreetEdgeStatistics(id, usersElevationRatings.get(id)));

			// Step 3 - Compute the reputation of each user
			for(Long userId : users)
				computeReputation(userId);

			hasData = true;

		}else
			LOG.error("No users found, skipping ratings and reputation computations.");
	}

	/** Computes the reputation of a specific user, both in terms of the safety and elevation
	 * classification criteria.
	 * <br>
	 * The results of this computation are then store on maps, which will hold the reputations
	 * off all users. 
	 * 
	 * @param userId A long that uniquely identifies a specific user.
	 * @throws EmptyMapException 
	 */
	private void computeReputation(long userId) throws EmptyMapException{	

		double safetyReputation, elevationReputation;

		safetyReputation 	= consolidateSimilarityMeasureOfRatings(safetyRatingsByUser.get(userId), safetyStats,REPUTATION_FACTOR);
		elevationReputation = consolidateSimilarityMeasureOfRatings(elevationRatingsByUser.get(userId), safetyStats,REPUTATION_FACTOR);

		UserStats stats = new UserStats(userId, safetyReputation, elevationReputation);

		userStatistics.put(userId, stats);
	}

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
	 * @param streetEdgeRatings List containing the list of street edges classified by the user whose reputation is being compute
	 * @param stats A map containing the statistics of each street edge, which makes up the map.  
	 * @param y Reputation Factor (?) UNKNOWN - this is a percentage
	 *  
	 * @return The user's reputation
	 * @throws EmptyMapException 
	 */
	private double consolidateSimilarityMeasureOfRatings(
			List<StreetEdgeWithRating> streetEdgeRatings,
			HashMap<String, StreetEdgeStatistics> stats,
			double reputationFactor) throws EmptyMapException{

		if(streetEdgeRatings == null)
			throw new EmptyMapException();
		
		int n = streetEdgeRatings.size();

		if(n == 0) return 0;

		double value = 0;
		if(n > 5){

			for(StreetEdgeWithRating se : streetEdgeRatings){
				value += computeSimilarityMeasureOfRating(se, stats.get(se.getStreetEdgeId()));
				value *= reputationFactor;
			}

			return value * (1-reputationFactor);

		}else{

			for(StreetEdgeWithRating se : streetEdgeRatings){
				value += computeSimilarityMeasureOfRating(se, stats.get(se.getStreetEdgeId()));
				value *= reputationFactor;
			}

			return value * (n-1)*reputationFactor;
		}

	}

	/**
	 * Given a specific street edge and the classification provided by a specific user, this function
	 * compares the user's classification with the other classifications, provided by other users. This
	 * is performed in order to determine if the user's response corresponds to the ones provided by the
	 * overall population, and if therefore the user can be trusted.
	 * 
	 * @param sewr A specific street edge, which will be the basis of the computation
	 * @param stats An object that encapsulates the statistics (average and standard deviation) of a given street edge.
	 * 
	 * @return A user's reputation for a specific street edge. The reputation is a value that belongs to the [-1, 1] interval.
	 */
	private double computeSimilarityMeasureOfRating(
			StreetEdgeWithRating sewr,
			StreetEdgeStatistics stats){

		double average = stats.getAverageRating();
		double standardDeviation = stats.getStdDevRating();

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
	private void setSafetyFactorAndIdToPlainStreetEdge(String pse, int id){

		throw new UnsupportedOperationException();

		/*
		 * TODO: Graph should support search by string.
		 * 
		StreetEdge edge = (StreetEdge) _graph.getEdgeById(pse);
		edge.setBicycleSafetyFactor(SafetyUtils.getFactorFromId(id));
		 */
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
	 */
	private void setElevationFactorAndIdToPlainStreetEdge(String pse, int id){

		throw new UnsupportedOperationException();

		/*
		 * TODO: Graph should support search by string.
		 *
		StreetEdge streetEdge = (StreetEdge) _graph.getEdgeById(pse);

		streetEdge.setSlope(SlopeUtils.getFactorFromId(id));
		 */
	}

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
	private long computeOverallRating(List<UserRating> usersRatings, Criteria criterion){

		long userId;
		double userReputation = 0, sum=0, denominator=0;

		for(UserRating rating : usersRatings){

			userId = rating.getUserId();

			switch (criterion) {
			case safety:
				userReputation = userStatistics.get(userId).getSafetyReputation();
				break;
			case elevation:
				userReputation = userStatistics.get(userId).getSafetyReputation();
				break;
			}

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
	 * @throws UnsupportedCriterionException 
	 */
	public void updateGraph(Criteria criterion) throws UnsupportedCriterionException{

		if(!hasData){
			LOG.error("No users nor ratings registered in the database. Skipping this method.");
			return;
		}


		HashMap<String, Integer> consolidatedRatings = new HashMap<String, Integer>();

		HashMap<String, List<UserRating>> userRatings;
		switch (criterion) {
		case safety:
			userRatings = usersSafetyRatings;
			break;
		case elevation:
			userRatings = usersElevationRatings;
			break;
		case rails:
		case pavement:
		default:
			throw new UnsupportedCriterionException(criterion);
		}

		for(String streetEdgeId : userRatings.keySet()){
			int overallRating = (int) computeOverallRating(userRatings.get(streetEdgeId), criterion);
			consolidatedRatings.put(streetEdgeId, overallRating);

			double factor;
			switch (criterion) {
			case safety:
				factor = _safetyRatingsToFactors[overallRating - 1];			
				setSafetyFactorAndIdToPlainStreetEdge(
						streetEdgeId,
						overallRating);
				break;
			case elevation:
				factor = _elevationRatingsToFactors[overallRating - 1];
				setElevationFactorAndIdToPlainStreetEdge(
						streetEdgeId,
						overallRating);
				break;
			default:
				throw new UnsupportedCriterionException(criterion);
			}
		}

		switch (criterion) {
		case safety:
			_exportRatings.insertConsolidadedSafetyRatings(consolidatedRatings);
			return;
		case elevation:
			_exportRatings.insertConsolidadedElevationRatings(consolidatedRatings);
			return;
		case pavement:
		case rails:
		default:
			throw new UnsupportedCriterionException(criterion);
		}
	}


	/**
	 * Exports the modifications performed over the graph, into a new graph file.
	 * @param filename The new graph filename.
	 */
	public void saveChanges(String filename){
		try {
			_graph.save(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}