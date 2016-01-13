package org.cycleourcity.otp.planner;

import java.util.List;

import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.otp.planner.preferences.UserPreferences;
import org.opentripplanner.api.model.TripPlan;
import org.opentripplanner.api.resource.GraphPathToTripPlanConverter;
import org.opentripplanner.routing.core.OptimizeType;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.impl.GraphPathFinder;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.standalone.Router;

public class SimpleBicycleRoutePlanner extends RoutePlanner{

	public SimpleBicycleRoutePlanner(Router router, GeoLocation from, GeoLocation to, UserPreferences preferences) {

		super(router, null);

		RoutingRequest request = new RoutingRequest();
		request.setFromString(from.toString());
		request.setToString(to.toString());
		request.setOptimize(OptimizeType.TRIANGLE);
		request.setTriangleNormalized(
				preferences.getSafetyPreference(),
				preferences.getSlopePreference(),
				preferences.getTimePreference());
		request.setMode(TraverseMode.BICYCLE);

		setRoutingRequest(request);
	}

	@Override
	public TripPlan planRoute() {

		GraphPathFinder pathFinder = new GraphPathFinder(getRouter());
		List<GraphPath> paths = pathFinder.graphPathFinderEntryPoint(getRoutingRequest());
		TripPlan plan = GraphPathToTripPlanConverter.generatePlan(paths, getRoutingRequest());

		return plan;
	}
}
