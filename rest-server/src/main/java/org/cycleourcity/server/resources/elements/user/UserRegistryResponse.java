package org.cycleourcity.server.resources.elements.user;

import javax.xml.bind.annotation.XmlRootElement;

import org.cycleourcity.server.resources.elements.Response;

@XmlRootElement
public class UserRegistryResponse extends Response{

	private String message;
	private String activationToken;
	
	public UserRegistryResponse(){}
	
	public UserRegistryResponse(String token, String message){
		this.activationToken = token;
		this.message = message;
	}

	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(String activationToken) {
		this.activationToken = activationToken;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
