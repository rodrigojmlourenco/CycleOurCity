package org.cycleourcity.cyclelourcity_web_server.security;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.cycleourcity.cyclelourcity_web_server.middleware.CycleOurCitySecurityManager;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter{

	
	private CycleOurCitySecurityManager manager = CycleOurCitySecurityManager.getManager();
	
	private String extractUsername(UriInfo uri){
		try{
			return uri.getQueryParameters().get("user").get(0);
		}catch(NullPointerException e){
			return "";
		}
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		final String username;
		
		System.out.println("HERE HERE HERE HERE ");
		
		// Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {

            // Validate the token
            username = validateToken(token);
            
    		requestContext.setSecurityContext(new SecurityContext() {
    			
    			@Override
    			public boolean isUserInRole(String role) {
    				return true;
    			}
    			
    			@Override
    			public boolean isSecure() {
    				return false;
    			}
    			
    			@Override
    			public Principal getUserPrincipal() {
    				return new Principal() {
    					
    					@Override
    					public String getName() {
    						return username;
    					}
    				};
    			}
    			
    			@Override
    			public String getAuthenticationScheme() {
    				return null;
    			}
    		});

        } catch (Exception e) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private String validateToken(String token) throws Exception {

    	// Check if it was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
    	manager.validateToken(token);
    	return manager.extractSubject(token);
    }

}
