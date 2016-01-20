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

package org.cycleourcity.server.app;

import javax.ws.rs.ApplicationPath;

import org.cycleourcity.server.security.AuthenticationFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("/")
public class CycleOurCityApp extends ResourceConfig{
	
	public CycleOurCityApp(){
		
		 // Register resources and providers using package-scanning.
        packages("org.cycleourcity.server.services");
 
        // Register my custom provider - not needed if it's in my.package.
        register(AuthenticationFilter.class);
        
        // Register an instance of LoggingFilter.
        //register(new LoggingFilter(LOGGER, true));
 
        // Enable Tracing support.
        property(ServerProperties.TRACING, "ALL");
		
	}
}
