package org.cycleourcity.driver.database.structures;

public class GeoLocation {
	
	private final double latitude;
	private final double longitude;
	
	public GeoLocation(double d, double e){
		this.latitude  = d;
		this.longitude = e;
	}
	
	public double getLatitude(){ return this.latitude; }
	
	public double getLongitude(){ return this.longitude; }

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
