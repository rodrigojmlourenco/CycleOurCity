package org.cycleourcity.server.resources.elements.user;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserRegistryRequest {

	private String username;
	private String email;
	private String password;
	private String confirmPassword;
	
	public UserRegistryRequest(){}
	
	public UserRegistryRequest(String username, String email, String password, String confirm){
		this.username = username;
		this.email = email;
		this.password = password;
		this.confirmPassword = confirm;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
