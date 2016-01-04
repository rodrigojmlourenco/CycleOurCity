package org.cycleourcity.cyclelourcity_web_server.otp_routing.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

public class DeprecatedElevationUtils {

	public static double getLengthMultiplierFromElevation(CoordinateSequence elev) {

        double trueLength = 0;
        double flatLength = 0;
        double lastX = elev.getX(0);
        double lastY = elev.getY(0);
        for (int i = 1; i < elev.size(); ++i) {
            Coordinate c = elev.getCoordinate(i);
            double x = c.x - lastX;
            double y = c.y - lastY;
            trueLength += Math.sqrt(x * x + y * y);
            flatLength += x;
            lastX = c.x;
            lastY = c.y;
        }
        if (flatLength == 0) {
            return 0;
        }
        return trueLength / flatLength;
    }
}
