package org.cycleourcity.cyclelourcity_web_server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.cycleourcity.cyclelourcity_web_server.database.MariaDriver;
import org.cycleourcity.cyclelourcity_web_server.datatype.CriteriaFactor;
import org.cycleourcity.cyclelourcity_web_server.utils.CriteriaUtils.Criteria;
import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

public class MyResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        String responseMsg = target.path("myresource").request().get(String.class);
        assertEquals("Got it!", responseMsg);
    }
    
    public static void main(String[] args){
    	MariaDriver driver = MariaDriver.getDriver();
    	try {
			List<CriteriaFactor> out = driver.getCriterionFactors(Criteria.elevation);
			
			for(CriteriaFactor cf : out)
				System.out.println(cf);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
