package org.cycleourcity.server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cycleourcity.server.middleware.CycleOurCitySecurityManager;
import org.cycleourcity.server.security.Role;
import org.cycleourcity.server.security.Secured;

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
		
		if(!manager.validateUser(user, password))
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		String token = manager.issueToken(user);
		
		return Response.ok(token).build();
	}
	
	/**
	 * <b>LogOut.php</b>
	 * @return
	 */
	@Path("/logout")
	@POST
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(){
		return Response.status(Response.Status.NOT_FOUND).build();
	}
	
	@Path("/test")
	@GET
	@Secured(Role.user)
	@Produces(MediaType.APPLICATION_JSON)
	public Response testValidation(){
		
		return Response.status(Response.Status.OK).build();
		
	}
}
