package org.cycleourcity.cyclelourcity_web_server.resources.elements.street;

public class GeometryRating {

	private String geometry;
	private int rate;
	
	public GeometryRating(){}
	
	public GeometryRating(String geometry, int rate){
		this.geometry = geometry;
		this.rate = rate;
	}

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
	
	
}
