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
