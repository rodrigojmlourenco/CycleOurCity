package org.cycleourcity.cyclelourcity_web_server.resources.elements.street;

public class RatedStreetsResponse {

	private String[] geometries;
	
	public RatedStreetsResponse(){}
	
	public RatedStreetsResponse(String[] geometries){
		this.geometries = geometries;
	}

	public String[] getGeometries() {
		return geometries;
	}

	public void setGeometries(String[] geometries) {
		this.geometries = geometries;
	}
}
