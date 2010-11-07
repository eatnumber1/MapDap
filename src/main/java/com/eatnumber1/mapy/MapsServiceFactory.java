package com.eatnumber1.mapy;

import com.google.gdata.client.maps.MapsService;
import com.google.gdata.util.AuthenticationException;

/**
 * @author Russell Harmon
 * @since Nov 6, 2010
 */
public class MapsServiceFactory {
	private String username;
	private String password;
	private String serviceName;

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName( String serviceName ) {
		this.serviceName = serviceName;
	}

	public MapsService produce() throws AuthenticationException {
		MapsService service = new MapsService(serviceName);
		service.setUserCredentials(username, password);
		return service;
	}
}
