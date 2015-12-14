package org.cycleourcity.cyclelourcity_web_server.datatype;

public class GeoLocation {
	
	private final float latitude;
	private final float longitude;
	
	public GeoLocation(float latitude, float longitude){
		this.latitude  = latitude;
		this.longitude = longitude;
	}
	
	public float getLatitude(){ return this.latitude; }
	
	public float getLongitude(){ return this.longitude; }

}
