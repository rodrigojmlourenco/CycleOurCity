package org.cycleourcity.cyclelourcity_web_server.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cycleourcity.cyclelourcity_web_server.middleware.CycleOurCityManager;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.planner.RoutePlanRequest;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.planner.RoutePlanResponse;
import org.cycleourcity.otp.planner.RoutePlanner;
import org.cycleourcity.otp.planner.exceptions.InvalidPreferenceSetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opentripplanner.api.model.TripPlan;
/**
 * This is the end-point for requesting route planning recommendations
 * 
 * @author Rodrigo Lourenço
 */
@Path("/route")
public class PlannerResource {

	
	private static Logger LOG = LoggerFactory.getLogger(PlannerResource.class);
	
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();

	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public RoutePlanRequest test(){
		return new RoutePlanRequest(
				38.7495721,-9.142133, //From
				38.7423355,-9.1399701, //To
				0.33f,0.33f,0.33f);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RoutePlanResponse planRoute(RoutePlanRequest request){

		String error = null;
		TripPlan plan;
		
		try {

			//Step 1 - Plan the route
			RoutePlanner planner = manager.planRoute(request);
			planner.run();
			plan = planner.getTripPlan();

			//Step 2 - Save the trip and its street edges
			manager.saveTrip(plan);
			
			return new RoutePlanResponse(planner.getTripPlan(), "success");

		} catch (InvalidPreferenceSetException e) {
			LOG.error(e.getMessage());
			error = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RoutePlanResponse(null, error);
	}

/*
| Largo do Andaluz                                 |         38.7276164 |          -9.1479395 |       38.7279015 |        -9.1478143 |
| Campo Grande                                     |         38.7487457 |          -9.1480487 |       38.7489487 |        -9.1480945 |
| Avenida Fontes Pereira de Melo                   |         38.7259932 |          -9.1495762 |       38.7260212 |        -9.1495627 |
| Avenida Sidónio Pais                             |         38.7314244 |          -9.1529096 |       38.7314251 |        -9.1527918 |
| Avenida das Forças Armadas                       |         38.7469948 |          -9.1567531 |       38.7470737 |        -9.1567343 |
| Avenida Elias Garcia                             |          38.739476 |          -9.1445111 |       38.7392668 |        -9.1459208 |
| Rua Guilhermina Suggia                           |          38.746855 |          -9.1323823 |       38.7469253 |        -9.1323811 |
| Rua António Pereira Carrilho                     |         38.7339249 |          -9.1351909 |       38.7339103 |        -9.1353067 |
| Rua Fialho de Almeida                            |         38.7338977 |             -9.1549 |       38.7346839 |        -9.1582133 |
| Avenida Visconde de Valmor                       |         38.7375496 |          -9.1510166 |       38.7374623 |        -9.1509679 |
| Avenida da Igreja                                |         38.7539287 |          -9.1414016 |       38.7536726 |        -9.1421491 |
| Avenida Professor Gama Pinto                     |         38.7501362 |            -9.15884 |       38.7502042 |        -9.1586321 |
| Praça Pasteur                                    |         38.7411277 |          -9.1353775 |       38.7411577 |        -9.1357632 |
| Avenida António José de Almeida                  |         38.7372242 |          -9.1457497 |       38.7372447 |        -9.1456121 |
| Avenida de Roma                                  |         38.7495721 |           -9.142133 |       38.7494338 |        -9.1420624 |
| Praça de Alvalade                                |         38.7531679 |           -9.144229 |       38.7529153 |         -9.144087 |
| Avenida João XXI                                 |         38.7422327 |          -9.1406201 |       38.7423355 |        -9.1399701 |
| Rua Capitão Ramires                              |         38.7429556 |          -9.1424933 |       38.7428603 |        -9.1424409 |
| Avenida Magalhães Lima                           |           38.73918 |          -9.1418995 |       38.7392779 |        -9.1415572 |
| Avenida Professor Gama Pinto                     |         38.7494375 |          -9.1579715 |       38.7493505 |        -9.1579283 |
| parking aisle                                    |         38.7506672 |          -9.1540321 |       38.7501453 |         -9.153742 |
| Rua Jorge Castilho                               |         38.7406249 |          -9.1287356 |       38.7404009 |        -9.1285032 |
| Rua António Pedro                                |         38.7329256 |          -9.1351286 |       38.7312455 |        -9.1353119 |
| Avenida Defensores de Chaves                     |         38.7364497 |          -9.1436186 |       38.7374989 |        -9.1438848 |
| Rua José Luís Rego                               |         38.7548355 |           -9.148899 |       38.7547654 |        -9.1493095 |
| Rua Brito Aranha                                 |         38.7409699 |           -9.140234 |       38.7408261 |        -9.1407367 |
| Avenida de Roma                                  |         38.7425867 |          -9.1383889 |       38.7433486 |        -9.1387592 |
| Avenida Almirante Reis                           |         38.7366392 |          -9.1338176 |       38.7359257 |         -9.133914 |
| Rua Domingos Bontempo                            |         38.7509753 |          -9.1336649 |       38.7517909 |        -9.1346007 |
| Rua Gomes Freire                                 |         38.7273037 |          -9.1419417 |        38.727671 |        -9.1422378 |
| Rua Américo Durão                                |         38.7409277 |          -9.1249458 |       38.7405216 |         -9.124749 |
| Rua Branca de Gonta Colaço                       |         38.7510066 |           -9.146968 |       38.7519424 |        -9.1475091 |
| Avenida Almirante Reis                           |         38.7263344 |          -9.1348834 |       38.7279592 |        -9.1347269 |
| Avenida José Régio                               |           38.75438 |          -9.1299727 |       38.7543973 |        -9.1302994 |
*/
}