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

package org.cycleourcity.server.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cycleourcity.driver.exceptions.ExpiredTokenException;
import org.cycleourcity.driver.exceptions.NonMatchingPasswordsException;
import org.cycleourcity.driver.exceptions.UnableToPerformOperation;
import org.cycleourcity.driver.exceptions.UnableToRegisterUserException;
import org.cycleourcity.driver.exceptions.UserRegistryException;
import org.cycleourcity.server.middleware.CycleOurCityManager;
import org.cycleourcity.server.resources.elements.user.UserRegistryRequest;
import org.cycleourcity.server.resources.elements.user.UserRegistryResponse;
import org.cycleourcity.server.security.Secured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/users")
public class UsersResource {

	private final static Logger LOG = LoggerFactory.getLogger(UsersResource.class);
	private CycleOurCityManager manager = CycleOurCityManager.getInstance();
	
	/**
	 * <b>Register.php & RegisterUser.php</b>
	 * @return
	 */
	@Path("/register")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(UserRegistryRequest r){
		
		String token, errorMsg;
		
		try {
			token = manager.registerUser(
					r.getUsername(),
					r.getEmail(),
					r.getPassword(),
					r.getConfirmPassword());
			
			return Response.ok(new UserRegistryResponse(token)).build();
			
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
		return Response.status(Response.Status.BAD_REQUEST).entity(errorMsg).build();
	}
	
	/**
	 * <b>ActivateAccount.php</b>
	 * @return
	 */
	@Path("/activate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(@QueryParam("token")String token){
		
		String error;
		try {
			manager.activateUser(token);
			return Response.ok().build();
		} catch (ExpiredTokenException e) {
			LOG.error(e.getMessage());
			error = e.getMessage();
		} catch (UnableToPerformOperation e) {
			LOG.error(e.getMessage());
			error = e.getMessage();
		}
		
		return Response.status(Response.Status.BAD_GATEWAY).encoding(error).build();
	}
	
	/**
	 * <b>ResetPassword.php</b>
	 * 
	 * Sends a password recovery token to the specified email.
	 * 
	 * @return
	 */
	@Path("/reset")
	@POST
	public void requestPasswordChange(@QueryParam("email")String email){
		
	}
	
	/**
	 * <b>ForgotPassword.php & ForgotPasswordJSON.php</b>
	 * 
	 * @return
	 */
	@Path("/change")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response unsecurePasswordChange(@QueryParam("token")String token){
		return Response.status(Response.Status.METHOD_NOT_ALLOWED).encoding("Not implemented yet").build();
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
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response securePasswordChange(){
		return Response.status(Response.Status.METHOD_NOT_ALLOWED).encoding("Not implemented yet").build();
	}
}
