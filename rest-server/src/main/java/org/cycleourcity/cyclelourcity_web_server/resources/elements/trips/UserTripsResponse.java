package org.cycleourcity.cyclelourcity_web_server.resources.elements.trips;

import org.cycleourcity.driver.database.structures.SimplifiedTrip;

public class UserTripsResponse {

	private String error;
	private SimplifiedTrip[] trips;
	
	public UserTripsResponse(){}
	
	public UserTripsResponse(SimplifiedTrip[] options){
		this.trips = options;
	}
	
	public UserTripsResponse(SimplifiedTrip[] options, String error){
		this.trips = options;
		this.error = error;
	}

	public SimplifiedTrip[] getTrips() {
		return trips;
	}

	public void setTrips(SimplifiedTrip[] trips) {
		this.trips = trips;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
