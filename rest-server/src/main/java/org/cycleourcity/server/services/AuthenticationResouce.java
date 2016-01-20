package org.cycleourcity.server.services;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.cycleourcity.server.middleware.CycleOurCitySecurityManager;
import org.cycleourcity.server.security.Secured;

import com.google.gson.JsonObject;

@Path("/auth")
public class AuthenticationResouce {
	
	private CycleOurCitySecurityManager manager = CycleOurCitySecurityManager.getManager();
	
	/**
	 * <b>Login.php</b>
	 * @return
	 */
	@Path("/login")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("user")String user, @QueryParam("password")String password){
		
		//Step 1 - Check if the user's account is activated
		if(!manager.isActiveUser(user))
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(user+" has not activated his account yet")
					.build();
		
		//Step 2 - Validate the provided password against the one stored in the database
		if(!manager.validateUser(user, password))
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity("Invalid password or username")
					.build();
		
		//TODO: criar uma lista de users blacklisted, quando estes falham a password com demasiada frequencia

		//Step 3 - Issue a new token and provide it to the user
		JsonObject result = new JsonObject();
		result.addProperty("token", manager.issueToken(user));
		return Response.ok().entity(result.toString()).build();
	}
	
	/**
	 * <b>LogOut.php</b>
	 * @return
	 */
	@Path("/logout")
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public void logout(@Context SecurityContext securityContext){
		manager.invalidateUserToken(securityContext.getUserPrincipal().getName());
	}
	
	
}
