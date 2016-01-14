package org.cycleourcity.cyclelourcity_web_server.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.cycleourcity.cyclelourcity_web_server.middleware.CycleOurCityManager;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.Response;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.user.UserRegistryRequest;

@Path("/users")
public class UsersResource {

	private CycleOurCityManager manager = CycleOurCityManager.getInstance();
	
	/**
	 * <b>Login.php</b>
	 * @return
	 */
	@Path("/login")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(){
		return new Response(500, "Method not implemented yet!");
	}
	
	/**
	 * <b>LogOut.php</b>
	 * @return
	 */
	@Path("/logout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(){
		return new Response(500, "Method not implemented yet!");
	}
	
	/**
	 * <b>Register.php & RegisterUser.php</b>
	 * @return
	 */
	@Path("/register")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(UserRegistryRequest r){
		
		boolean success;
		
		success = manager.registerUser(
				r.getUsername(),
				r.getEmail(),
				r.getPassword(),
				r.getConfirmPassword());
		
		return new Response(500, "Method not implemented yet!");
	}
	
	/**
	 * <b>ActivateAccount.php</b>
	 * @return
	 */
	@Path("/activate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(String token){
		return new Response(500, "Method not implemented yet!");
	}
	
	/**
	 * <b>ResetPassword.php</b>
	 * @return
	 */
	@Path("/reset")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetPassword(){
		return new Response(500, "Method not implemented yet!");
	}
	
	/**
	 * <b>ForgotPassword.php & ForgotPasswordJSON.php</b>
	 * 
	 * @return
	 */
	@Path("/forgot")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestPassword(){
		return new Response(500, "Method not implemented yet!");
	}
	
	/**
	 * TODO: what is the difference between this one and reset???
	 * 
	 * <b>ChangePassword.php & ChangePasswordJSON.php</b>
	 * 
	 * @return
	 */
	@Path("/change")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(){
		return new Response(500, "Method not implemented yet!");
	}
}
