package com.intuit.integration.pos2qbo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.oauth2.exception.OAuthException;

// Data Service Factory for the QBO OAuth2Platform Client APIs.
// The context is created before each call to API.
// Thus, a new Access Token is obtained before each call to API.
@Service
public class DataServiceFactory {
	
	@Autowired
	ContextFactory contextFactory;
	
	// Common place to initialize Intuit Data Service
	public DataService getDataService(String qboCompanyId, String qboRefreshToken) throws OAuthException, FMSException {
		return new DataService(contextFactory.getContext(qboCompanyId, qboRefreshToken));
	}

}
