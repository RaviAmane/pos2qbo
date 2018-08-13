package com.intuit.integration.pos2qbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.Environment;
import com.intuit.oauth2.config.OAuth2Config;

@Service
public class QboOAuth2PlatformClientFactory {
	
	// The spring Environment clashes with com.intuit.oauth2.config.Environment
	@Autowired
	org.springframework.core.env.Environment env;
	
	@Value("${QBOOAuth2AppClientId}")
	private String oauth2AppClientId;

	@Value("${QBOOAuth2AppClientSecret}")
	private String oauth2AppClientSecret;

	OAuth2PlatformClient client;
	OAuth2Config oauth2Config;
	
	public void init() {
		oauth2Config = new OAuth2Config
				.OAuth2ConfigBuilder(oauth2AppClientId, oauth2AppClientSecret) // set client app id, secret
				.callDiscoveryAPI(Environment.SANDBOX) // call discovery API to populate urls
				.buildConfig();
		client = new OAuth2PlatformClient(oauth2Config);
	}

	public OAuth2PlatformClient getOAuth2PlatformClient()  {
		init();
		return client;
	}
	
	public OAuth2Config getOAuth2Config()  {
		init();
		return oauth2Config;
	}
	
	public String getPropertyValue(String propertyName) {
		return env.getProperty(propertyName);
	}
}
