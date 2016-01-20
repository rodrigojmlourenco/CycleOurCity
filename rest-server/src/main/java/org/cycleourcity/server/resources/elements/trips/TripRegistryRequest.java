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

import javax.xml.bind.annotation.XmlRootElement;

import org.cycleourcity.driver.database.structures.SimplifiedStreetEdge;

@XmlRootElement
public class TripRegistryRequest {

	private String tripName;
	private Long user;
	private SimplifiedStreetEdge[] streetEdges;
	
	public TripRegistryRequest(){}
	
	public TripRegistryRequest(Long user, String tripName, SimplifiedStreetEdge[] edges){
		this.tripName = tripName;
		this.user = user;
		this.streetEdges = edges;
	}

	public String getTripName() {
		return tripName;
	}

	public void setTripName(String tripName) {
		this.tripName = tripName;
	}

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public SimplifiedStreetEdge[] getStreetEdges() {
		return streetEdges;
	}

	public void setStreetEdges(SimplifiedStreetEdge[] streetEdges) {
		this.streetEdges = streetEdges;
	}
}
