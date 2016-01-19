package org.cycleourcity.driver.database.structures;

public class CustomStreetEdge {

	private String Id;
	private String name;
	private GeoLocation from, to;
	private int otpID;
	private String geometry;
	
	public CustomStreetEdge(String id, String name, GeoLocation from, GeoLocation to, int otpId, String geometry){
		this.Id = id;
		this.name = name;
		this.from = from;
		this.to= to;
		this.otpID = otpId;
		this.geometry = geometry;
	}
	
	public String getId(){ return this.Id; }
	
	public String getName(){ return this.name; }
	
	public GeoLocation getFrom() { return this.from; }
	
	public GeoLocation getTo() { return this.to; }
	
	public int getOTPID(){return this.otpID;}
	
	public int getOtpID() {
		return otpID;
	}

	public void setOtpID(int otpID) {
		this.otpID = otpID;
	}

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public void setId(String id) {
		Id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFrom(GeoLocation from) {
		this.from = from;
	}

	public void setTo(GeoLocation to) {
		this.to = to;
	}

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
