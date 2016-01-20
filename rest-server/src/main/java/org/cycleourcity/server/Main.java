package org.cycleourcity.server;

import org.cycleourcity.server.app.CycleOurCityApp;
import org.cycleourcity.server.middleware.CycleOurCityManager;
import org.cycleourcity.server.middleware.CycleOurCitySecurityManager;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/cycleourcity/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        //final ResourceConfig rc = new ResourceConfig().packages("org.cycleourcity.server.services");
    	final ResourceConfig app = new CycleOurCityApp();
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), app);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	
    	CycleOurCityManager.getInstance();
    	CycleOurCitySecurityManager.getManager();
    	
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}
