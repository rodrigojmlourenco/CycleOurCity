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

public class StreetEdgeRating {

	private String streetEdgeId;
	private int 	elevationRate 	= -1,
					safetyRate 		= -1,
					pavementRate 	= -1,
					railsRate		= -1;
	
	public StreetEdgeRating(){}
	
	public StreetEdgeRating(String streetEdgeId, 
			int elevation, int safety, int pavement, int rails){
		
		this.streetEdgeId = streetEdgeId;
		
		this.safetyRate 	=	safety;
		this.pavementRate	= pavement;
		this.railsRate		= rails;
		this.elevationRate	= elevation;
	}

	public String getStreetEdgeId() {
		return streetEdgeId;
	}

	public void setStreetEdgeId(String streetEdgeId) {
		this.streetEdgeId = streetEdgeId;
	}

	public int getElevationRate() {
		return elevationRate;
	}

	public void setElevationRate(int elevationRate) {
		this.elevationRate = elevationRate;
	}

	public int getSafetyRate() {
		return safetyRate;
	}

	public void setSafetyRate(int safetyRate) {
		this.safetyRate = safetyRate;
	}

	public int getPavementRate() {
		return pavementRate;
	}

	public void setPavementRate(int pavementRate) {
		this.pavementRate = pavementRate;
	}

	public int getRailsRate() {
		return railsRate;
	}

	public void setRailsRate(int railsRate) {
		this.railsRate = railsRate;
	}
}
