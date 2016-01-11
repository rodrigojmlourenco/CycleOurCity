package org.cycleourcity.driver.database.structures;

import java.util.List;

public class StreetEdgeStatistics {

	private double id;
	private double average;
	private double standardDeviation;
	
	public StreetEdgeStatistics(double id, List<UserRating> ratings){
		this.id = id;
		
		average = computeAverage(ratings);
		standardDeviation = computeStandardDeviation(ratings, average);
	}
	
	public double getId(){ return this.id; }
	
	public double getAverageRating() { return average; }
	
	public double getStdDevRating(){ return standardDeviation; }
	
	/**
	 * Computes the average rating of a specific criterion for a specific street edge.
	 * 
	 * @param usersRatings List of users' rating for a specific street edge
	 * @return Average rating
	 */
	private double computeAverage(List<UserRating> usersRatings){
		double average = 0;
	
		
		for(UserRating userRating : usersRatings){
			int size = userRating.getRatings().size(); 
			average += userRating.getRatings().get(size - 1);
		}
		
		average = average / usersRatings.size();
		
		return average;
	}
	
	/**
	 * Computes the ratings' standard deviation of a specific criterion for a specific street edge.
	 * 
	 * @param usersRatings List of users' rating for a specific street edge
	 * @return Ratings' standard deviation
	 */
	private double computeStandardDeviation(List<UserRating> usersRatings, double average){
		int numRatings = usersRatings.size();
		double sum = 0;
		
		for(UserRating userRating : usersRatings){
			int size = userRating.getRatings().size(); 
			sum = sum + (userRating.getRatings().get(size - 1) - average) * (userRating.getRatings().get(size - 1) - average) ;
		}
		
		sum /= numRatings;
		
		return Math.sqrt(sum);
	}
}
