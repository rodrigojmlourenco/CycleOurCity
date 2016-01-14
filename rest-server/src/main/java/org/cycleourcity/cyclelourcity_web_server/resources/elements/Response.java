
package org.cycleourcity.cyclelourcity_web_server.resources.elements;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {

	private int code;
	private String error;
	
	public Response(){}
	
	public Response(int code, String error){
		this.code	= code;
		this.error 	= error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
