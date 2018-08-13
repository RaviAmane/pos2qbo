package com.intuit.integration.pos2qbo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.IAuthorizer;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;

// Context factory for the QBO OAuth2Platform Client APIs.
// The context is created before each call to API.
// Thus, a new Access Token is obtained before each call to API.
@Service
public class ContextFactory {
	
	private static final Logger logger = Logger.getLogger(ContextFactory.class);
	
	@Autowired
	private QboOAuth2PlatformClientFactory factory;
	
	// Initializes the context for given company.
	// We get a new Access Token every time we get a new context.
	public Context getContext(String qboCompanyId, String qboRefreshToken) throws OAuthException, FMSException {
    	OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
    	// Get a new AccessToken
    	BearerTokenResponse bearerTokenResponse = client.refreshToken(qboRefreshToken);
    	logger.info("Obtained a new QBO AccessToken: " + bearerTokenResponse.getAccessToken());
		IAuthorizer oauth = new OAuth2Authorizer(bearerTokenResponse.getAccessToken());
		return new Context(oauth, ServiceType.QBO, qboCompanyId);
	}

}
