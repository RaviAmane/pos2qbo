package com.intuit.integration.pos2qbo.oauth2;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.Environment;
import com.intuit.oauth2.config.OAuth2Config;

@Service
@PropertySource(value="classpath:/application.properties", ignoreResourceNotFound=true)
public class QboOAuth2PlatformClientFactory {
	
	// The spring Environment clashes with com.intuit.oauth2.config.Environment
	@Autowired
	org.springframework.core.env.Environment env;

	OAuth2PlatformClient client;
	OAuth2Config oauth2Config;
	
	@PostConstruct
	public void init() {
		// Intitialize a single thread executor, this will ensure only one thread processes the queue
		String oauth2AppClientId = env.getProperty("QBOOAuth2AppClientId");
		String oauth2AppClientSecret = env.getProperty("QBOOAuth2AppClientSecret");
		oauth2Config = new OAuth2Config
				.OAuth2ConfigBuilder(oauth2AppClientId, oauth2AppClientSecret) // set client app id, secret
				.callDiscoveryAPI(Environment.SANDBOX) // call discovery API to populate urls
				.buildConfig();
		client  = new OAuth2PlatformClient(oauth2Config);
	}

	public OAuth2PlatformClient getOAuth2PlatformClient()  {
		return client;
	}
	
	public OAuth2Config getOAuth2Config()  {
		return oauth2Config;
	}
	
	public String getPropertyValue(String propertyName) {
		return env.getProperty(propertyName);
	}
}
