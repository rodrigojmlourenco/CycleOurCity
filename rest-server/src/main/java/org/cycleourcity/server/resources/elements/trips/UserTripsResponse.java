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

package org.cycleourcity.server.resources.elements.trips;

import org.cycleourcity.driver.database.structures.SimplifiedTrip;

public class UserTripsResponse {

	private String error;
	private SimplifiedTrip[] trips;
	
	public UserTripsResponse(){}
	
	public UserTripsResponse(SimplifiedTrip[] options){
		this.trips = options;
	}
	
	public UserTripsResponse(SimplifiedTrip[] options, String error){
		this.trips = options;
		this.error = error;
	}

	public SimplifiedTrip[] getTrips() {
		return trips;
	}

	public void setTrips(SimplifiedTrip[] trips) {
		this.trips = trips;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
