package org.cycleourcity.cyclelourcity_web_server.middleware.datalayer;

import java.sql.SQLException;
import java.util.List;

import org.cycleourcity.cyclelourcity_web_server.database.MariaDriver;
import org.cycleourcity.cyclelourcity_web_server.database.StreetEdgesDriver;
import org.cycleourcity.cyclelourcity_web_server.database.TripsDriver;
import org.cycleourcity.cyclelourcity_web_server.database.exception.StreetEdgeNotFoundException;
import org.cycleourcity.cyclelourcity_web_server.datatype.GeoLocation;
import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedElevationEdge;
import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedSafetyEdge;
import org.cycleourcity.cyclelourcity_web_server.datatype.SimplifiedTripEdge;
import org.cycleourcity.cyclelourcity_web_server.datatype.Trip;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnknowStreetEdgeException;
import org.cycleourcity.cyclelourcity_web_server.utils.CriteriaUtils.Criteria;
import org.cycleourcity.cyclelourcity_web_server.utils.exceptions.UnsupportedCriterionException;

public class StreetEdgeManager implements StreetEdgeManagement{
	
	private final TripsDriver tripsDriver;
	private final StreetEdgesDriver streetEdgesDriver;
	
	private final static StreetEdgeManager MANAGER = new StreetEdgeManager();
	
	
	private StreetEdgeManager(){
		tripsDriver = MariaDriver.getDriver();
		streetEdgesDriver = MariaDriver.getDriver();
	}
	
	public static StreetEdgeManager getManager(){
		return MANAGER;
	}

	@Override
	public List<SimplifiedElevationEdge> getStreetEdgesWithElevation() {
	
		try {
			return streetEdgesDriver.getAllStreetEdgesWithElevation();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<SimplifiedSafetyEdge> getStreetEdgesWithSafety() {
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
	public boolean classifyStreetEdge(int tripID, int streetEdgeID, int safety, int elevation, int pavement, int rails, int userID, boolean last) 
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
}
