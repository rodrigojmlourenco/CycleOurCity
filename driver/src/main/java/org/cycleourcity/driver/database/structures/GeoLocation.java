package org.cycleourcity.driver.database.structures;

public class GeoLocation {
	
	private double latitude;
	private double longitude;
	
	public GeoLocation(){}
	
	public GeoLocation(double d, double e){
		this.latitude  = d;
		this.longitude = e;
	}
	
	public double getLatitude(){ return this.latitude; }
	
	public double getLongitude(){ return this.longitude; }
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return ""+latitude+","+longitude;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		GeoLocation aux = (GeoLocation) obj;
		
		return latitude == aux.getLatitude() && longitude == aux.getLongitude();
		
	}
	
	
}
