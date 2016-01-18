package org.cycleourcity.cyclelourcity_web_server.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.cycleourcity.cyclelourcity_web_server.middleware.CycleOurCityManager;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.Response;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.user.UserRegistryRequest;
import org.cycleourcity.cyclelourcity_web_server.resources.elements.user.UserRegistryResponse;
import org.cycleourcity.driver.exceptions.ExpiredTokenException;
import org.cycleourcity.driver.exceptions.NonMatchingPasswordsException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnableToRegisterUserException;
import org.cycleourcity.driver.exceptions.UserRegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/users")
public class UsersResource {

	private final String NAME = UsersResource.class.getSimpleName();
	
	private final static Logger LOG = LoggerFactory.getLogger(UsersResource.class);
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();
	
	@Path("/test")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String test(){
		return NAME;
	}
	
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

	@Path("/test/register")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserRegistryRequest testRegisterStructrure(){
		return new UserRegistryRequest("bonobo", "bob@somemail.org", "passWord12345!", "passWord12345!");
	}
	
	/**
	 * <b>Register.php & RegisterUser.php</b>
	 * @return
	 */
	@Path("/register")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public UserRegistryResponse register(UserRegistryRequest r){
		
		String token, errorMsg;
		
		try {
			token = manager.registerUser(
					r.getUsername(),
					r.getEmail(),
					r.getPassword(),
					r.getConfirmPassword());
			
			return new UserRegistryResponse(token, "success");
			
		} catch (UserRegistryException e) {
			errorMsg = e.getMessage();
			LOG.error(errorMsg);
		} catch (NonMatchingPasswordsException e) {
			errorMsg = e.getMessage();
			LOG.error(errorMsg);
		} catch (UnableToRegisterUserException e) {
			errorMsg = e.getMessage();
			LOG.error(errorMsg);
		} catch (UnableToPerformOperation e) {
			errorMsg = e.getMessage();
			LOG.error(errorMsg);
		}
		return new UserRegistryResponse("", errorMsg);
	}
	
	/**
	 * <b>ActivateAccount.php</b>
	 * @return
	 */
	@Path("/activate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(@QueryParam("token")String token){
		
		Response response;
		String error;
		try {
			manager.activateUser(token);
			return new Response(200, "success");
		} catch (ExpiredTokenException e) {
			LOG.error(e.getMessage());
			error = e.getMessage();
		} catch (UnableToPerformOperation e) {
			LOG.error(e.getMessage());
			error = e.getMessage();
		}
		
		return new Response(500, error);
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
