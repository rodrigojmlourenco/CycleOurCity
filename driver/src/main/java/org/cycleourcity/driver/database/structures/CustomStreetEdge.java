package org.cycleourcity.driver.database.structures;

public class CustomStreetEdge {

	private String Id;
	private String name;
	private GeoLocation from, to;
	private int otpID;
	
	public CustomStreetEdge(String id, String name, GeoLocation from, GeoLocation to, int otpId){
		this.Id = id;
		this.name = name;
		this.from = from;
		this.to= to;
		this.otpID = otpId;
	}
	
	public String getId(){ return this.Id; }
	
	public String getName(){ return this.name; }
	
	public GeoLocation getFrom() { return this.from; }
	
	public GeoLocation getTo() { return this.to; }
	
	public int getOTPID(){return this.otpID;}
	
	@Override
	public String toString() {
		return Id + " | " + name + " | " + from + " | " + to;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		CustomStreetEdge aux = (CustomStreetEdge) obj;
		
		return from.equals(aux.getFrom()) && to.equals(aux.getTo());
	}
}
