package org.cycleourcity.cyclelourcity_web_server.resources.elements.street;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RatedGeometriesResponse {

	private GeometryRating[] safetyRatings;
	private GeometryRating[] elevationRatings;
	
	public RatedGeometriesResponse(){}
	
	public RatedGeometriesResponse(GeometryRating[] safeties, GeometryRating[] elevations){
		this.safetyRatings = safeties;
		this.elevationRatings = elevations;
	}

	public GeometryRating[] getSafetyRatings() {
		return safetyRatings;
	}

	public void setSafetyRatings(GeometryRating[] safetyRatings) {
		this.safetyRatings = safetyRatings;
	}

	public GeometryRating[] getElevationRatings() {
		return elevationRatings;
	}

	public void setElevationRatings(GeometryRating[] elevationRatings) {
		this.elevationRatings = elevationRatings;
	}
}
