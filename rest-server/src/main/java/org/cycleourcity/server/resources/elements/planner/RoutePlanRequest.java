/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package org.cycleourcity.server.resources.elements.planner;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoutePlanRequest {

	private double fromLat, fromLon, toLat, toLon;
	private float safetyPref, elevationPref, timePref;
	
	public RoutePlanRequest(){}
	
	public RoutePlanRequest(double fromLat, double fromLon,
			double toLat, double toLon,
			float safetyPref, float elevationPref, float timePref){
		
		this.fromLat= fromLat;
		this.fromLon= fromLon;
		this.toLat 	= toLat;
		this.toLon	= toLon;
		
		this.safetyPref 	= safetyPref;
		this.elevationPref 	= elevationPref;
		this.timePref 		= timePref;
	}

	public double getFromLat() {
		return fromLat;
	}

	public void setFromLat(double fromLat) {
		this.fromLat = fromLat;
	}

	public double getFromLon() {
		return fromLon;
	}

	public void setFromLon(double fromLon) {
		this.fromLon = fromLon;
	}

	public double getToLat() {
		return toLat;
	}

	public void setToLat(double toLat) {
		this.toLat = toLat;
	}

	public double getToLon() {
		return toLon;
	}

	public void setToLon(double toLon) {
		this.toLon = toLon;
	}

	public float getSafetyPref() {
		return safetyPref;
	}

	public void setSafetyPref(float safetyPref) {
		this.safetyPref = safetyPref;
	}

	public float getElevationPref() {
		return elevationPref;
	}

	public void setElevationPref(float elevationPref) {
		this.elevationPref = elevationPref;
	}

	public float getTimePref() {
		return timePref;
	}

	public void setTimePref(float timePref) {
		this.timePref = timePref;
	}
}
