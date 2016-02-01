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

package org.cycleourcity.server;

import java.io.IOException;
import java.net.URI;

import org.cycleourcity.server.app.CycleOurCityApp;
import org.cycleourcity.server.middleware.CycleOurCityManager;
import org.cycleourcity.server.middleware.CycleOurCitySecurityManager;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
	private static final String port = "8081";
    public static final String BASE_URI = "http://localhost:"+port+"/cycleourcity/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        //final ResourceConfig rc = new ResourceConfig().packages("org.cycleourcity.server.services");
    	
    	HttpServer server = null;
    	
    	try{
    		final ResourceConfig app = new CycleOurCityApp();
    		server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), app);
    		return server;
    	}catch(Exception e){
    		if(server != null) server.shutdown();
    		throw e;
    	}
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

    	final HttpServer server = startServer();
    	
    	CycleOurCityManager.getInstance();
    	CycleOurCitySecurityManager.getManager();
    	
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

