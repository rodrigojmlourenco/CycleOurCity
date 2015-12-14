package org.cycleourcity.cyclelourcity_web_server.datatype;

public class SimplifiedTrip {
	
	private final int tripID;
	private final String name;
	
	public SimplifiedTrip(int tripID, String name){
		this.name = name;
		this.tripID = tripID;
	}

	public int getTripID(){ return this.tripID; }
	
	public String getTripName(){ return this.name; }
}
