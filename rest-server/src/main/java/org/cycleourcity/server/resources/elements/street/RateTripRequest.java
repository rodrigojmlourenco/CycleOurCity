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

package org.cycleourcity.server.resources.elements.street;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RateTripRequest {

	private int tripId;
	
	private StreetEdgeRating[] ratings;
	
	public RateTripRequest(){}
	
	public RateTripRequest(int userId, int tripId, StreetEdgeRating[] ratings){
		this.tripId = tripId;
		
		this.ratings = ratings;
	}
	
	public int getTripId() {
		return tripId;
	}
	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public StreetEdgeRating[] getRatings() {
		return ratings;
	}

	public void setRatings(StreetEdgeRating[] ratings) {
		this.ratings = ratings;
	}
}
