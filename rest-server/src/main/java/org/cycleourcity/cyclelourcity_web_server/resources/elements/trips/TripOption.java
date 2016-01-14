package org.cycleourcity.cyclelourcity_web_server.resources.elements.trips;

public class TripOption {
	private int tripId;
	private String name;
	
	public TripOption(){}
	
	public TripOption(int tripId, String name){
		this.tripId = tripId;
		this.name = name;
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
