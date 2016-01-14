package org.cycleourcity.cyclelourcity_web_server.resources.elements.trips;

public class UserTripsResponse {

	private TripOption[] trips;
	
	public UserTripsResponse(){}
	
	public UserTripsResponse(TripOption[] options){
		this.trips = options;
	}

	public TripOption[] getTrips() {
		return trips;
	}

	public void setTrips(TripOption[] trips) {
		this.trips = trips;
	}
	
	
}
