package com.intuit.integration.pos2qbo.oauth2;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PosBearerToken implements Serializable{

	private static final long serialVersionUID = -8589712573856458261L;
	
	@JsonProperty("access_token")
	private String accessToken;
	
	private String scope;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
}
