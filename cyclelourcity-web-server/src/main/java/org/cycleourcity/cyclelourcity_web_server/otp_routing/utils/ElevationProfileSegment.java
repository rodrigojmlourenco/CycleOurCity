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

package org.cycleourcity.cyclelourcity_web_server.otp_routing.utils;

import java.io.Serializable;

import org.opentripplanner.common.MavenVersion;
import org.opentripplanner.common.geometry.PackedCoordinateSequence;
import org.opentripplanner.routing.util.ElevationUtils;
import org.opentripplanner.routing.util.SlopeCosts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is an helper for Edges and Vertexes to store various data about elevation profiles.
 * 
 */
public class ElevationProfileSegment implements Serializable {

    private static final long serialVersionUID = MavenVersion.VERSION.getUID();

    private static final Logger LOG = LoggerFactory.getLogger(ElevationProfileSegment.class);

    private PackedCoordinateSequence elevationProfile;

    private double length;

    private double slopeSpeedEffectiveLength;

    private double bicycleSafetyEffectiveLength;

    private double slopeWorkCost;

    private double maxSlope;

    protected boolean slopeOverride;

    private boolean flattened;
    
    //nfn
    @Setter @Getter
    private int safetyId;
    
    @Setter @Getter
    private int elevationId;
    
    @Setter @Getter
    private double safetyCost; 
    
    @Setter @Getter
    private double elevationCost; 
    

    public ElevationProfileSegment(double length) {
        this.length = length;
        slopeSpeedEffectiveLength = length;
        bicycleSafetyEffectiveLength = length;
        slopeWorkCost = length;
        
        //-1 significa que não há classificações
        safetyId = -1;
        elevationId = -1;
        
        elevationCost = getLength();
        safetyCost = getBicycleSafetyEffectiveLength();
    }

    public double getMaxSlope() {
        return maxSlope;
    }

    public void setSlopeOverride(boolean slopeOverride) {
        this.slopeOverride = slopeOverride;
    }

    public boolean getSlopeOverride() {
        return slopeOverride;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getLength() {
        return length;
    }

    public void setSlopeSpeedEffectiveLength(double slopeSpeedEffectiveLength) {
        this.slopeSpeedEffectiveLength = slopeSpeedEffectiveLength;
    }

    /* sem importar os dados da elevacao devolve o comprimento do arco */
    public double getSlopeSpeedEffectiveLength() {
        return slopeSpeedEffectiveLength;
    }

    public void setBicycleSafetyEffectiveLength(double bicycleSafetyEffectiveLength) {
        this.bicycleSafetyEffectiveLength = bicycleSafetyEffectiveLength;
    }

    public double getBicycleSafetyEffectiveLength() {
        return bicycleSafetyEffectiveLength;
    }

    public void setSlopeWorkCost(double slopeWorkCost) {
        this.slopeWorkCost = slopeWorkCost;
    }

    public double getSlopeWorkCost() {
        return slopeWorkCost;
    }

    public PackedCoordinateSequence getElevationProfile() {
        return elevationProfile;
    }

    public PackedCoordinateSequence getElevationProfile(double start, double end) {
        return ElevationUtils.getPartialElevationProfile(elevationProfile, start, end);
    }

    public void setElevationProfile(PackedCoordinateSequence elevationProfile) {
        this.elevationProfile = elevationProfile;
    }

    public boolean setElevationProfile(PackedCoordinateSequence elev, boolean computed,
            boolean slopeLimit) {
        if (elev == null || elev.size() < 2) {
            return false;
        }

        if (slopeOverride && !computed) {
            return false;
        }

        elevationProfile = elev;

        // compute the various costs of the elevation changes
        //double lengthMultiplier = ElevationUtils.getLengthMultiplierFromElevation(elev);
        double lengthMultiplier = DeprecatedElevationUtils.getLengthMultiplierFromElevation(elev);
        if (Double.isNaN(lengthMultiplier)) {
            LOG.error("lengthMultiplier from elevation profile is NaN, setting to 1");
            lengthMultiplier = 1;
        }

        length *= lengthMultiplier;
        bicycleSafetyEffectiveLength *= lengthMultiplier;

        SlopeCosts costs = ElevationUtils.getSlopeCosts(elev, slopeLimit);
        //slopeSpeedEffectiveLength = costs.slopeSpeedEffectiveLength; TODO este deixou de existir, qual o novo?
        slopeSpeedEffectiveLength = costs.slopeSpeedFactor;
        maxSlope = costs.maxSlope;
        slopeWorkCost = costs.slopeWorkFactor;
        bicycleSafetyEffectiveLength += costs.slopeSafetyCost;
        flattened = costs.flattened;
        
        return costs.flattened;
    }

    public String toString() {
        String out = "";
        if (elevationProfile == null || elevationProfile.size() == 0) {
            return "(empty elevation profile)";
        }
        for (int i = 0; i < elevationProfile.size(); ++i) {
            Coordinate coord = elevationProfile.getCoordinate(i);
            out += "(" + coord.x + "," + coord.y + "), ";
        }
        return out.substring(0, out.length() - 2);
    }

    public boolean isFlattened() {
        return flattened;
    }

    //nfn
	public double getSafetyCostWithUserFeedback(){
		return safetyCost;
	}
	
	public double getElevationCostWithUserFeedback(){
		return elevationCost;
	}
		
	public int getElevationId(){ return elevationId; }
	
	public void setElevationId(int elevationId) { this.elevationId = elevationId; }
	
	public int getSafetyId(){ return safetyId; }
	
	public void setSafetyId(int safetyId){ this.safetyId = safetyId; }
	
	public double getElevationCost(){ return elevationCost; }
	
	public void setElevationCost(double elevationCost){ this.elevationCost = elevationCost; }
	
	public double getSafetyCost() { return safetyCost; }
	
	public void setSafetyCost(double safetyCost){ this.safetyCost = safetyCost; }

}