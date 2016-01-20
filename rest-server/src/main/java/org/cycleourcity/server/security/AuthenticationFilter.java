package org.cycleourcity.server.security;

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

import org.cycleourcity.server.middleware.CycleOurCitySecurityManager;
import org.cycleourcity.server.security.exceptions.InvalidAuthorizationTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter{

	private static Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	private CycleOurCitySecurityManager manager = CycleOurCitySecurityManager.getManager();
	
	/*
	private String extractUsername(UriInfo uri){
		try{
			return uri.getQueryParameters().get("user").get(0);
		}catch(NullPointerException e){
			return "";
		}
	}
	*/
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		final String username;
		
		LOG.info("Request caught");
		
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
    	if(!manager.validateToken(token))
    		throw new InvalidAuthorizationTokenException();
    	
    	return manager.validateAndExtractSubject(token);
    }

}
