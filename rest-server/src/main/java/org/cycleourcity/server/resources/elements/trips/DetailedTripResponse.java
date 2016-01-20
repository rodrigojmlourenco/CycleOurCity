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

import java.util.List;

import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.driver.database.structures.SimplifiedTripEdge;

public class DetailedTripResponse {

	private GeoLocation from;
	private GeoLocation to;
	private SimplifiedTripEdge[] streetEdges;
	
	public DetailedTripResponse(){}
	
	public DetailedTripResponse(SimplifiedTripEdge[] streetEdges, GeoLocation from, GeoLocation to){
		this.from 	= from;
		this.to		= to;
		
		this.streetEdges = streetEdges;
	}
	
	public DetailedTripResponse(List<SimplifiedTripEdge> streetEdges, GeoLocation from, GeoLocation to){
		this.from 	= from;
		this.to		= to;
		
		this.streetEdges = new SimplifiedTripEdge[streetEdges.size()];
		streetEdges.toArray(this.streetEdges);
	}

	public GeoLocation getFrom() {
		return from;
	}

	public void setFrom(GeoLocation from) {
		this.from = from;
	}

	public GeoLocation getTo() {
		return to;
	}

	public void setTo(GeoLocation to) {
		this.to = to;
	}

	public SimplifiedTripEdge[] getStreetEdges() {
		return streetEdges;
	}

	public void setStreetEdges(SimplifiedTripEdge[] streetEdges) {
		this.streetEdges = streetEdges;
	}
}
