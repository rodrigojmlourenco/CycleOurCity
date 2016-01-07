package org.cycleourcity.driver.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.cycleourcity.driver.StreetEdgeManagementDriver;
import org.cycleourcity.driver.database.StreetEdgesDriver;
import org.cycleourcity.driver.database.TripsDriver;
import org.cycleourcity.driver.database.impl.MariaDriver;
import org.cycleourcity.driver.database.structures.CustomStreetEdge;
import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;
import org.cycleourcity.driver.database.structures.SimplifiedTripEdge;
import org.cycleourcity.driver.database.structures.Trip;
import org.cycleourcity.driver.database.structures.UserRating;
import org.cycleourcity.driver.exceptions.StreetEdgeNotFoundException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnknowStreetEdgeException;
import org.cycleourcity.driver.exceptions.UnsupportedCriterionException;
import org.cycleourcity.driver.utils.CriteriaUtils.Criteria;

public class StreetEdgeManagementDriverImpl implements StreetEdgeManagementDriver{
	
	private final TripsDriver tripsDriver;
	private final StreetEdgesDriver streetEdgesDriver;
	
	private final static StreetEdgeManagementDriverImpl MANAGER = new StreetEdgeManagementDriverImpl();
	
	
	private StreetEdgeManagementDriverImpl(){
		tripsDriver = MariaDriver.getDriver();
		streetEdgesDriver = MariaDriver.getDriver();
	}
	
	public static StreetEdgeManagementDriverImpl getManager(){
		return MANAGER;
	}

	@Override
	public List<SimplifiedStreetEdge> getStreetEdgesWithElevation() {
	
		try {
			return streetEdgesDriver.getAllStreetEdgesWithElevation();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<SimplifiedStreetEdge> getStreetEdgesWithSafety() {
		try {
			return streetEdgesDriver.getAllStreetEdgesWithSafety();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Trip getTrip(int tripID) throws StreetEdgeNotFoundException {
		
		Trip trip;
		GeoLocation from, to;
		SimplifiedTripEdge fromEdge, toEdge;
		List<SimplifiedTripEdge> tripEdges;
		
		try {
			
			tripEdges = tripsDriver.getTripStreetEdges(tripID);
			
			if(tripEdges == null || tripEdges.isEmpty()) return null;
			
			fromEdge= tripEdges.get(0);
			toEdge 	= tripEdges.get(tripEdges.size()-1);
			
			from= streetEdgesDriver.getSteetEdgeFromLocation(fromEdge.getStreetEdgeID());
			to	= streetEdgesDriver.getSteetEdgeToLocation(toEdge.getStreetEdgeID());
			
			trip = new Trip(from, to, tripEdges);
			return trip;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<Integer> getUserTrips(int userID) {
		
		try {
			return tripsDriver.getUsersTrips(userID);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<String> getAllDistinctGeometries() {
		
		try {
			return streetEdgesDriver.getAllDistinctGeometries();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean validElevationFactor(int factor){
		return factor > 0 && factor <= 6;
	}
	
	private boolean validSafetyFactor(int factor){
		return factor > 0 && factor <= 6;
	}
	
	private boolean validPavementFactor(int factor){
		return factor > 0 && factor <= 4;
	}
	
	private boolean validPavementRails(int factor){
		return factor > 0 && factor <= 3;
	}
	
	@Override
	public boolean classifyStreetEdge(long tripID, long streetEdgeID, int safety, int elevation, int pavement, int rails, long userID, boolean last) 
			throws UnknowStreetEdgeException {

		try {
			if(!tripsDriver.tripContainStreetEdge(tripID, streetEdgeID))
				throw new UnknowStreetEdgeException();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnknowStreetEdgeException();
		}
		
		if(safety != -1 && validSafetyFactor(safety))
			try {
				streetEdgesDriver.classifyStreetEdge(Criteria.safety, streetEdgeID, safety, userID);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		if(elevation != -1 && validElevationFactor(elevation));
			try {
				streetEdgesDriver.classifyStreetEdge(Criteria.elevation, streetEdgeID, elevation, userID);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		if(pavement != -1 && validPavementFactor(pavement))
			try {
				streetEdgesDriver.classifyStreetEdge(Criteria.pavement, streetEdgeID, pavement, userID);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if(rails != -1 && validPavementRails(rails))
			try {
				streetEdgesDriver.classifyStreetEdge(Criteria.rails, streetEdgeID, rails, userID);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if(last){
			try {
				tripsDriver.deleteTripStreetEdges(tripID);
				tripsDriver.deleteTrip(tripID);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

	
	
	@Override
	public boolean isEmptyMap(){
		try {
			return streetEdgesDriver.isEmptyMap();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void populateStreetEdges(List<CustomStreetEdge> streetEdges){
		
		int i=0;
		double id;
		boolean error = false;;
		String name, geometry;
		GeoLocation from, to;
		
		for(CustomStreetEdge se : streetEdges){
			
			id		= se.getId();
			name 	= se.getName();
			from	= se.getFrom();
			to		= se.getTo();
			geometry= "DIFFERENT FROM OG VERSION";
			
			
			try {
				streetEdgesDriver.insertStreetEdge(id, name, from, to, geometry);
				i++;
			} catch (SQLException e) {
				error = true;
				e.printStackTrace();
			}
		
			if((i % 1000) == 0)
				System.out.print("|");
		}
		
		if(error) 
			System.out.println("\nNot all street edges were successfully inserted.");
		else 
			System.out.println("\nAll "+i+" street edges inserted successfully.");
		
	}

	@Override
	public HashMap<Long, List<UserRating>> getAllSafetyRatings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Long, List<UserRating>> getAllPavementRatings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Long, List<UserRating>> getAllRailsRatings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Long, List<UserRating>> getAllElevationRatings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getAllSafetyFactorsIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getAllElevationFactorsIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getAllPavementFactorsIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getAllRailsFactorsIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean clearAndUpdateConsolidatedElevationRatings(HashMap<Integer, Integer> ratings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean clearAndUpdateConsolidatedSafetyRatings(HashMap<Integer, Integer> ratings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean clearAndUpdateConsolidatedPavementRatings(HashMap<Integer, Integer> ratings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean clearAndUpdateConsolidatedRailsRatings(HashMap<Integer, Integer> ratings) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveTrip(long userID, String tripName, List<SimplifiedTripEdge> streetEdges) 
			throws UnableToPerformOperation{
		
		int tripId;
		List<Integer> userTrips;
		
		try {
			tripsDriver.insertTrip(userID, tripName);

			userTrips = tripsDriver.getUsersTrips(userID);
			tripId = userTrips.get(userTrips.size()-1);
			
			for(SimplifiedTripEdge se : streetEdges)
				tripsDriver.insertTripStreetEdge(tripId, se.getStreetEdgeID(), se.isBicycleMode());
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnableToPerformOperation(e.getMessage());
		} catch (NullPointerException e){
			e.printStackTrace();
			throw new UnableToPerformOperation(e.getMessage());
		}
	}
}
