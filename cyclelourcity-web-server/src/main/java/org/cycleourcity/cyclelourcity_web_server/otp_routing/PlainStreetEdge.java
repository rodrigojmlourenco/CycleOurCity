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

package org.cycleourcity.cyclelourcity_web_server.otp_routing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.cycleourcity.cyclelourcity_web_server.otp_routing.utils.ElevationProfileSegment;
import org.opentripplanner.common.TurnRestriction;
import org.opentripplanner.common.TurnRestrictionType;
import org.opentripplanner.common.geometry.DirectionUtils;
import org.opentripplanner.common.geometry.PackedCoordinateSequence;
import org.opentripplanner.routing.alertpatch.Alert;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.util.ElevationUtils;
import org.opentripplanner.routing.vertextype.IntersectionVertex;
import org.opentripplanner.routing.vertextype.StreetVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;


import lombok.Getter;
import lombok.Setter;


/**
 * This represents a street segment.
 *
 * @author novalis
 *
 */
public class PlainStreetEdge extends StreetEdge implements Cloneable {

    private static Logger LOG = LoggerFactory.getLogger(PlainStreetEdge.class);

    private static final long serialVersionUID = 1L;

    private static final double GREENWAY_SAFETY_FACTOR = 0.1;

    private ElevationProfileSegment elevationProfileSegment;

    @Getter
    private double length;

    @Getter
    private LineString geometry;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private boolean wheelchairAccessible = true;

    @Getter @Setter
    private StreetTraversalPermission permission;

    @Getter @Setter
    private int streetClass = CLASS_OTHERPATH;

    /**
     * Marks that this edge is the reverse of the one defined in the source
     * data. Does NOT mean fromv/tov are reversed.
     */
    public boolean back;

    @Getter @Setter
    private boolean roundabout = false;

    @Getter
    private Set<Alert> notes;

    @Setter
    private boolean hasBogusName;

    @Getter @Setter
    private boolean noThruTraffic;

    /**
     * This street is a staircase
     */
    @Getter @Setter
    private boolean stairs;

    /**
     * The speed (meters / sec) at which an automobile can traverse
     * this street segment.
     */
    @Getter @Setter
    private float carSpeed;

    /** This street has a toll */
    @Getter @Setter
    private boolean toll;

    @Getter
    private Set<Alert> wheelchairNotes;

    @Getter
    private List<TurnRestriction> turnRestrictions = Collections.emptyList();

    @Getter
    public int inAngle;

    @Getter
    public int outAngle;



    public PlainStreetEdge(StreetVertex v1, StreetVertex v2, LineString geometry,
            String name, double length,
            StreetTraversalPermission permission, boolean back) {
    	
        // use a default car speed of ~25 mph for splitter vertices and the like
        // TODO(flamholz): do something smarter with the car speed here.
    	
        this(v1, v2, geometry, name, length, permission, back, 11.2f);
    }

    public PlainStreetEdge(StreetVertex v1, StreetVertex v2, LineString geometry,
            String name, double length,
            StreetTraversalPermission permission, boolean back, float carSpeed) {
        
    	super(v1, v2, geometry, name, length, permission, back);
    	
        this.geometry = geometry;
        this.length = length;
        this.elevationProfileSegment = new ElevationProfileSegment(length);
        this.name = name;
        this.permission = permission;
        this.back = back;
        this.carSpeed = carSpeed;
        if (geometry != null) {
            try {
                for (Coordinate c : geometry.getCoordinates()) {
                    if (Double.isNaN(c.x)) {
                        System.out.println("X DOOM");
                    }
                    if (Double.isNaN(c.y)) {
                        System.out.println("Y DOOM");
                    }
                }
                double angleR = DirectionUtils.getLastAngle(geometry);
                outAngle = ((int) (180 * angleR / Math.PI) + 180 + 360) % 360;
                angleR = DirectionUtils.getFirstAngle(geometry);
                inAngle = ((int) (180 * angleR / Math.PI) + 180 + 360) % 360;
            } catch (IllegalArgumentException iae) {
                LOG.error("exception while determining street edge angles. setting to zero. there is probably something wrong with this street segment's geometry.");
                inAngle = 0;
                outAngle = 0;
            }
        }
    }

    //@Override
    public boolean canTraverse(RoutingRequest options) {
        if (options.wheelchairAccessible) {
            if (!wheelchairAccessible) {
                return false;
            }
            if (elevationProfileSegment.getMaxSlope() > options.maxSlope) {
                return false;
            }
        }

        return canTraverse(options.modes);
    }

    @Override
    public boolean canTraverse(TraverseModeSet modes) {
        return permission.allows(modes);
    }

    private boolean canTraverse(RoutingRequest options, TraverseMode mode) {
        if (options.wheelchairAccessible) {
            if (!wheelchairAccessible) {
                return false;
            }
            if (elevationProfileSegment.getMaxSlope() > options.maxSlope) {
                return false;
            }
        }
        return permission.allows(mode);
    }

    @Override
    public PackedCoordinateSequence getElevationProfile() {
        return elevationProfileSegment.getElevationProfile();
    }

    //nfn
    public void setElevationId(int elevationId){
        elevationProfileSegment.setElevationId(elevationId);
    	
    }

    public void setSafetyId(int safetyId){
        elevationProfileSegment.setSafetyId(safetyId);
    }


    public void setElevationCost(double factor){
        elevationProfileSegment.setElevationCost(elevationProfileSegment.getLength() * factor);
    }

    public void setSafetyCost(double factor){
        elevationProfileSegment.setSafetyCost(elevationProfileSegment.getLength() * factor);
    }

    public double getElevationCost(){
        return elevationProfileSegment.getElevationCostWithUserFeedback();
    }

    public double getSafetyCost(){
        return elevationProfileSegment.getSafetyCostWithUserFeedback();
    }

    //@Override
    public boolean setElevationProfile(PackedCoordinateSequence elev, boolean computed) {
        return elevationProfileSegment.setElevationProfile(elev, computed, permission.allows(StreetTraversalPermission.CAR));
    }

    @Override
    public boolean isElevationFlattened() {
        return elevationProfileSegment.isFlattened();
    }

    @Override
    public double getDistance() {
        return length;
    }

    /*
     * TODO: escolhi não override
    @Override
    public State traverse(State s0) {
        final RoutingRequest options = s0.getOptions();
        return doTraverse(s0, options, s0.getNonTransitMode());
    }
    */

    /*
    private State doTraverse(State s0, RoutingRequest options, TraverseMode traverseMode) {
        Edge backEdge = s0.getBackEdge();
        if (backEdge != null
                && (options.arriveBy ? (backEdge.getToVertex() == fromv)
                        : (backEdge.getFromVertex() == tov))) {
            // no illegal U-turns
            // NOTE(flamholz): unclear how this handles legal U-turns. e.g. when
            // you need to pull a U to turn left at the previous intersection.
            return null;
        }
        if (!canTraverse(options, traverseMode)) {
            if (traverseMode == TraverseMode.BICYCLE) {
                // try walking bike since you can't ride here
                return doTraverse(s0, options.bikeWalkingOptions,
                        TraverseMode.WALK);
            }
            return null;
        }

        // Automobiles have variable speeds depending on the edge type
        double speed = calculateSpeed(options, traverseMode);

        double time = length / speed;
        double weight;
        // TODO(flamholz): factor out this bike, wheelchair and walking specific logic to somewhere central.
        if (options.wheelchairAccessible) {
            weight = elevationProfileSegment.getSlopeSpeedEffectiveLength() / speed;
        } else if (traverseMode.equals(TraverseMode.BICYCLE)) {
            time = elevationProfileSegment.getSlopeSpeedEffectiveLength() / speed;
            switch (options.optimize) {
            case SAFE:
                weight = elevationProfileSegment.getBicycleSafetyEffectiveLength() / speed;
                break;
            case GREENWAYS:
                weight = elevationProfileSegment.getBicycleSafetyEffectiveLength() / speed;
                if (elevationProfileSegment.getBicycleSafetyEffectiveLength() / length <= GREENWAY_SAFETY_FACTOR) {
                    // greenways are treated as even safer than they really are
                    weight *= 0.66;
                }
                break;
            case FLAT:
                //see notes in StreetVertex on speed overhead
                weight = length / speed + elevationProfileSegment.getSlopeWorkCost();
                break;
            case QUICK:
                weight = elevationProfileSegment.getSlopeSpeedEffectiveLength() / speed;
                break;
            case TRIANGLE:
                double quick;
                double safety;
                double slope;

                //System.out.println("PSE ID: " + getId());
                //nfn
                
                //TODO: não sei se isto está correcto
                boolean routingWithUserFeedback = 
                		options.triangleSafetyFactor != 0 
                		|| options.triangleSlopeFactor != 0
                		|| options.triangleSlopeFactor != 0;
                
                if(routingWithUserFeedback == true){
                        //System.out.println("Routing with users feedback: TRUE");
                    quick = elevationProfileSegment.getLength();
                    safety = elevationProfileSegment.getSafetyCost();
                    slope = elevationProfileSegment.getElevationCost();
                }
                else{
                        //System.out.println("Routing with users feedback: FALSE");
                    quick = elevationProfileSegment.getSlopeSpeedEffectiveLength();
                    safety = elevationProfileSegment.getBicycleSafetyEffectiveLength();
                    slope = elevationProfileSegment.getSlopeWorkCost();
                }

                //System.out.println("PSE ID: " + getId() + " Quick = " + quick + " Safety = " + safety + " Slope = " + slope);

                
                weight = quick * options.triangleTimeFactor + slope
                        * options.triangleSlopeFactor + safety
                        * options.triangleSafetyFactor;
                weight /= speed;

                //System.out.println("PSE ID: " + getId() + " Weight: " + weight);
                break;
            default:
                weight = length / speed;
            }
        } else {
        	
            if (options.walkingBike) {
                // take slopes into account when walking bikes
                time = elevationProfileSegment.getSlopeSpeedEffectiveLength() / speed;
            }
            weight = time;
            if (traverseMode.equals(TraverseMode.WALK)) {
                // take slopes into account when walking
                double costs = ElevationUtils.getWalkCostsForSlope(length, elevationProfileSegment.getMaxSlope());
                // as the cost walkspeed is assumed to be for 4.8km/h (= 1.333 m/sec) we need to adjust
                // for the walkspeed set by the user
                weight = costs * ( 1.3333 / speed );
                time = weight; //treat cost as time, as in the current model it actually is the same (this can be checked for maxSlope == 0)
                /*
                // debug code
                if(weight > 100){
                    double timeflat = length / speed;
                    System.out.format("line length: %.1f m, slope: %.3f ---> slope costs: %.1f , weight: %.1f , time (flat):  %.1f %n", length, elevationProfileSegment.getMaxSlope(), costs, weight, timeflat);
                }
                *
            }
        }
		

        if (isStairs()) {
            weight *= options.stairsReluctance;
        } else {
            weight *= options.walkReluctance;
        }

        StateEditor s1 = s0.edit(this);
        s1.setBackMode(traverseMode);

        /*TODO: não devemos precisar disto, uma vez que o objectivo é planeamento de rotas em bike
        if (wheelchairNotes != null && options.wheelchairAccessible) {
            s1.addAlerts(wheelchairNotes);
        }*

        PlainStreetEdge backPSE;
        if (backEdge != null && backEdge instanceof PlainStreetEdge) {
            backPSE = (PlainStreetEdge) backEdge;
            double backSpeed = backPSE.calculateSpeed(options, traverseMode);
            final double realTurnCost;

            /* Compute turn cost.
             *
             * This is a subtle piece of code. Turn costs are evaluated differently during
             * forward and reverse traversal. During forward traversal of an edge, the turn
             * *into* that edge is used, while during reverse traversal, the turn *out of*
             * the edge is used.
             *
             * However, over a set of edges, the turn costs must add up the same (for
             * general correctness and specifically for reverse optimization). This means
             * that during reverse traversal, we must also use the speed for the mode of
             * the backEdge, rather than of the current edge.
             *
            if (options.arriveBy && tov instanceof IntersectionVertex) { // arrive-by search
                if (!canTurnOnto(backPSE, s0, traverseMode)) {
                    return null;
                }
                realTurnCost = ((IntersectionVertex) tov).computeTraversalCost(
                        this, backPSE, traverseMode, options, (float) speed, (float) backSpeed);
            } else if (fromv instanceof IntersectionVertex) { // depart-after search
                if (!backPSE.canTurnOnto(this, s0, traverseMode)) {
                    return null;
                }
                realTurnCost = ((IntersectionVertex) fromv).computeTraversalCost(
                        backPSE, this, traverseMode, options, (float) backSpeed, (float) speed);
            } else { // in case this is a temporary edge not connected to an IntersectionVertex
                realTurnCost = 0;
            }

            if (traverseMode != TraverseMode.CAR) {
                s1.incrementWalkDistance(realTurnCost / 100); //just a tie-breaker
            }

            weight += realTurnCost;
            long turnTime = (long) realTurnCost;
            if (turnTime != realTurnCost) {  // round up.
                turnTime++;
            }
            time += turnTime;
        }

        int timeLong = (int) time;
        if (timeLong != time) {
            timeLong++;
        }
        s1.incrementTimeInSeconds(timeLong);

        if (traverseMode != TraverseMode.CAR) {
            s1.incrementWalkDistance(length);
        }

        s1.incrementWeight(weight);
        if (s1.weHaveWalkedTooFar(options)) {
            return null;
        }

        s1.addAlerts(notes);

        if (this.toll && traverseMode == TraverseMode.CAR) {
            s1.addAlert(Alert.createSimpleAlerts("Toll road"));
        }

        return s1.makeState();
    }
    */
    
    /**
     * From the new version of StreetEdge
     */
    private StateEditor doTraverse(State s0, RoutingRequest options, TraverseMode traverseMode) {
        boolean walkingBike = options.walkingBike;
        boolean backWalkingBike = s0.isBackWalkingBike();
        TraverseMode backMode = s0.getBackMode();
        Edge backEdge = s0.getBackEdge();
        if (backEdge != null) {
            // No illegal U-turns.
            // NOTE(flamholz): we check both directions because both edges get a chance to decide
            // if they are the reverse of the other. Also, because it doesn't matter which direction
            // we are searching in - these traversals are always disallowed (they are U-turns in one direction
            // or the other).
            // TODO profiling indicates that this is a hot spot.
            if (this.isReverseOf(backEdge) || backEdge.isReverseOf(this)) {
                return null;
            }
        }

        // Ensure we are actually walking, when walking a bike
        backWalkingBike &= TraverseMode.WALK.equals(backMode);
        walkingBike &= TraverseMode.WALK.equals(traverseMode);

        /* Check whether this street allows the current mode. If not and we are biking, attempt to walk the bike. */
        if (!canTraverse(options, traverseMode)) {
            if (traverseMode == TraverseMode.BICYCLE) {
                return doTraverse(s0, options.bikeWalkingOptions, TraverseMode.WALK);
            }
            return null;
        }

        // Automobiles have variable speeds depending on the edge type
        double speed = calculateSpeed(options, traverseMode, s0.getTimeInMillis());
        
        double time = getDistance() / speed;
        double weight;
        // TODO(flamholz): factor out this bike, wheelchair and walking specific logic to somewhere central.
        if (options.wheelchairAccessible) {
            weight = getSlopeSpeedEffectiveLength() / speed;
        } else if (traverseMode.equals(TraverseMode.BICYCLE)) {
            time = getSlopeSpeedEffectiveLength() / speed;
            switch (options.optimize) {
            case SAFE:
                weight = bicycleSafetyFactor * getDistance() / speed;
                break;
            case GREENWAYS:
                weight = bicycleSafetyFactor * getDistance() / speed;
                if (bicycleSafetyFactor <= GREENWAY_SAFETY_FACTOR) {
                    // greenways are treated as even safer than they really are
                    weight *= 0.66;
                }
                break;
            case FLAT:
                /* see notes in StreetVertex on speed overhead */
                weight = getDistance() / speed + getSlopeWorkCostEffectiveLength();
                break;
            case QUICK:
                weight = getSlopeSpeedEffectiveLength() / speed;
                break;
            case TRIANGLE:
                double quick = getSlopeSpeedEffectiveLength();
                double safety = bicycleSafetyFactor * getDistance();
                // TODO This computation is not coherent with the one for FLAT
                double slope = getSlopeWorkCostEffectiveLength();
                weight = quick * options.triangleTimeFactor + slope
                        * options.triangleSlopeFactor + safety
                        * options.triangleSafetyFactor;
                weight /= speed;
                break;
            default:
                weight = getDistance() / speed;
            }
        } else {
            if (walkingBike) {
                // take slopes into account when walking bikes
                time = getSlopeSpeedEffectiveLength() / speed;
            }
            weight = time;
            if (traverseMode.equals(TraverseMode.WALK)) {
                // take slopes into account when walking
                // FIXME: this causes steep stairs to be avoided. see #1297.
                double costs = ElevationUtils.getWalkCostsForSlope(getDistance(), getMaxSlope());
                // as the cost walkspeed is assumed to be for 4.8km/h (= 1.333 m/sec) we need to adjust
                // for the walkspeed set by the user
                double elevationUtilsSpeed = 4.0 / 3.0;
                weight = costs * (elevationUtilsSpeed / speed);
                time = weight; //treat cost as time, as in the current model it actually is the same (this can be checked for maxSlope == 0)
                /*
                // debug code
                if(weight > 100){
                    double timeflat = length / speed;
                    System.out.format("line length: %.1f m, slope: %.3f ---> slope costs: %.1f , weight: %.1f , time (flat):  %.1f %n", length, elevationProfile.getMaxSlope(), costs, weight, timeflat);
                }
                */
            }
        }

        if (isStairs()) {
            weight *= options.stairsReluctance;
        } else {
            // TODO: this is being applied even when biking or driving.
            weight *= options.walkReluctance;
        }

        StateEditor s1 = s0.edit(this);
        s1.setBackMode(traverseMode);
        s1.setBackWalkingBike(walkingBike);

        /* Compute turn cost. */
        StreetEdge backPSE;
        if (backEdge != null && backEdge instanceof StreetEdge) {
            backPSE = (StreetEdge) backEdge;
            RoutingRequest backOptions = backWalkingBike ?
                    s0.getOptions().bikeWalkingOptions : s0.getOptions();
            double backSpeed = backPSE.calculateSpeed(backOptions, backMode, s0.getTimeInMillis());
            final double realTurnCost;  // Units are seconds.

            // Apply turn restrictions
            if (options.arriveBy && !canTurnOnto(backPSE, s0, backMode)) {
                return null;
            } else if (!options.arriveBy && !backPSE.canTurnOnto(this, s0, traverseMode)) {
                return null;
            }

            /*
             * This is a subtle piece of code. Turn costs are evaluated differently during
             * forward and reverse traversal. During forward traversal of an edge, the turn
             * *into* that edge is used, while during reverse traversal, the turn *out of*
             * the edge is used.
             *
             * However, over a set of edges, the turn costs must add up the same (for
             * general correctness and specifically for reverse optimization). This means
             * that during reverse traversal, we must also use the speed for the mode of
             * the backEdge, rather than of the current edge.
             */
            if (options.arriveBy && tov instanceof IntersectionVertex) { // arrive-by search
                IntersectionVertex traversedVertex = ((IntersectionVertex) tov);

                realTurnCost = backOptions.getIntersectionTraversalCostModel().computeTraversalCost(
                        traversedVertex, this, backPSE, backMode, backOptions, (float) speed,
                        (float) backSpeed);
            } else if (!options.arriveBy && fromv instanceof IntersectionVertex) { // depart-after search
                IntersectionVertex traversedVertex = ((IntersectionVertex) fromv);

                realTurnCost = options.getIntersectionTraversalCostModel().computeTraversalCost(
                        traversedVertex, backPSE, this, traverseMode, options, (float) backSpeed,
                        (float) speed);                
            } else {
                // In case this is a temporary edge not connected to an IntersectionVertex
                LOG.debug("Not computing turn cost for edge {}", this);
                realTurnCost = 0; 
            }

            if (!traverseMode.isDriving()) {
                s1.incrementWalkDistance(realTurnCost / 100);  // just a tie-breaker
            }

            long turnTime = (long) Math.ceil(realTurnCost);
            time += turnTime;
            weight += options.turnReluctance * realTurnCost;
        }
        

        if (walkingBike || TraverseMode.BICYCLE.equals(traverseMode)) {
            if (!(backWalkingBike || TraverseMode.BICYCLE.equals(backMode))) {
                s1.incrementTimeInSeconds(options.bikeSwitchTime);
                s1.incrementWeight(options.bikeSwitchCost);
            }
        }

        if (!traverseMode.isDriving()) {
            s1.incrementWalkDistance(getDistance());
        }

        /* On the pre-kiss/pre-park leg, limit both walking and driving, either soft or hard. */
        int roundedTime = (int) Math.ceil(time);
        if (options.kissAndRide || options.parkAndRide) {
            if (options.arriveBy) {
                if (!s0.isCarParked()) s1.incrementPreTransitTime(roundedTime);
            } else {
                if (!s0.isEverBoarded()) s1.incrementPreTransitTime(roundedTime);
            }
            if (s1.isMaxPreTransitTimeExceeded(options)) {
                if (options.softPreTransitLimiting) {
                    weight += calculateOverageWeight(s0.getPreTransitTime(), s1.getPreTransitTime(),
                            options.maxPreTransitTime, options.preTransitPenalty,
                                    options.preTransitOverageRate);
                } else return null;
            }
        }
        
        /* Apply a strategy for avoiding walking too far, either soft (weight increases) or hard limiting (pruning). */
        if (s1.weHaveWalkedTooFar(options)) {

            // if we're using a soft walk-limit
            if( options.softWalkLimiting ){
                // just slap a penalty for the overage onto s1
                weight += calculateOverageWeight(s0.getWalkDistance(), s1.getWalkDistance(),
                        options.getMaxWalkDistance(), options.softWalkPenalty,
                                options.softWalkOverageRate);
            } else {
                // else, it's a hard limit; bail
                LOG.debug("Too much walking. Bailing.");
                return null;
            }
        }

        s1.incrementTimeInSeconds(roundedTime);
        
        s1.incrementWeight(weight);

        return s1;
    }

    private double calculateOverageWeight(double firstValue, double secondValue, double maxValue,
            double softPenalty, double overageRate) {
        // apply penalty if we stepped over the limit on this traversal
        boolean applyPenalty = false;
        double overageValue;

        if(firstValue <= maxValue && secondValue > maxValue){
            applyPenalty = true;
            overageValue = secondValue - maxValue;
        } else {
            overageValue = secondValue - firstValue;
        }

        // apply overage and add penalty if necessary
        return (overageRate * overageValue) + (applyPenalty ? softPenalty : 0.0);
    }
    

    /**
     * Calculate the average automobile traversal speed of this segment, given
     * the RoutingRequest, and return it in meters per second.
     *
     * @param options
     * @return
     */
    private double calculateCarSpeed(RoutingRequest options) {
        return carSpeed;
    }

    /**
     * Calculate the speed appropriately given the RoutingRequest and traverseMode.
     *
     * @param options
     * @param traverseMode
     * @return
     */
    private double calculateSpeed(RoutingRequest options, TraverseMode traverseMode) {
        if (traverseMode == TraverseMode.CAR) {
            // NOTE: Automobiles have variable speeds depending on the edge type
            return calculateCarSpeed(options);
        }
        return options.getSpeed(traverseMode);
    }

    @Override
    public double weightLowerBound(RoutingRequest options) {
        return timeLowerBound(options) * options.walkReluctance;
    }

    @Override
    public double timeLowerBound(RoutingRequest options) {
    	return this.length / options.getStreetSpeedUpperBound();
    }

    public void setSlopeSpeedEffectiveLength(double slopeSpeedEffectiveLength) {
        elevationProfileSegment.setSlopeSpeedEffectiveLength(slopeSpeedEffectiveLength);
    }

    public double getSlopeSpeedEffectiveLength() {
        return elevationProfileSegment.getSlopeSpeedEffectiveLength();
    }

    public void setSlopeWorkCost(double slopeWorkCost) {
        elevationProfileSegment.setSlopeWorkCost(slopeWorkCost);
    }

    public double getWorkCost() {
        return elevationProfileSegment.getSlopeWorkCost();
    }

    public void setBicycleSafetyEffectiveLength(double bicycleSafetyEffectiveLength) {
        elevationProfileSegment.setBicycleSafetyEffectiveLength(bicycleSafetyEffectiveLength);
    }

    public double getBicycleSafetyEffectiveLength() {
        return elevationProfileSegment.getBicycleSafetyEffectiveLength();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    //@Override
    public PackedCoordinateSequence getElevationProfile(double start, double end) {
        return elevationProfileSegment.getElevationProfile(start, end);
    }

    public void setSlopeOverride(boolean slopeOverride) {
        elevationProfileSegment.setSlopeOverride(slopeOverride);
    }

    public void setNote(Set<Alert> notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PlainStreetEdge(" + name + ", " + fromv + " -> " + tov + ")";
    }

    public boolean hasBogusName() {
        return hasBogusName;
    }

    public boolean hasExplicitTurnRestrictions() {
        return this.turnRestrictions != null && this.turnRestrictions.size() > 0;
    }

    public void setWheelchairNote(Set<Alert> wheelchairNotes) {
        this.wheelchairNotes = wheelchairNotes;
    }

    public void addTurnRestriction(TurnRestriction turnRestriction) {
        if (turnRestrictions.isEmpty()) {
            turnRestrictions = new ArrayList<TurnRestriction>();
        }
        turnRestrictions.add(turnRestriction);
    }

    @Override
    public PlainStreetEdge clone() {
        return (PlainStreetEdge) super.clone();
    }

    public boolean canTurnOnto(Edge e, State state, TraverseMode mode) {
        for (TurnRestriction restriction : turnRestrictions) {
            /* FIXME: This is wrong for trips that end in the middle of restriction.to
             */

            if (restriction.type == TurnRestrictionType.ONLY_TURN) {
                if (restriction.to != e && restriction.modes.contains(mode) &&
                        restriction.active(state.getTimeSeconds())) {
                	
                    return false;
                }
            } else {
                if (restriction.to == e && restriction.modes.contains(mode) &&
                        restriction.active(state.getTimeSeconds())) {
                    return false;
                }
            }
        }
        return true;
    }

    //@Override
    public ElevationProfileSegment getElevationProfileSegment() {
        return elevationProfileSegment;
    }

    public boolean getToll() {
        return this.toll;
    }

    protected boolean detachFrom() {
        for (Edge e : fromv.getIncoming()) {
            if (!(e instanceof PlainStreetEdge)) continue;
            PlainStreetEdge pse = (PlainStreetEdge) e;
            ArrayList<TurnRestriction> restrictions = new ArrayList<TurnRestriction>(pse.turnRestrictions);
            for (TurnRestriction restriction : restrictions) {
                if (restriction.to == this) {
                    pse.turnRestrictions.remove(restriction);
                }
            }
        }
        return detachFrom();
    }

}
