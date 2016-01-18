package org.cycleourcity.driver.database.structures;

public class SimplifiedTrip {
	
	private int tripID;
	private String name;
	
	public SimplifiedTrip(){}
	
	public SimplifiedTrip(int tripID, String name){
		this.name = name;
		this.tripID = tripID;
	}

	public int getTripID(){ return this.tripID; }
	
	public String getTripName(){ return this.name; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTripID(int tripID) {
		this.tripID = tripID;
	}
}
