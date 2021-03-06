package com.intuit.integration.pos2qbo.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import com.intuit.oauth2.exception.InvalidRequestException;

@Controller
public class PosOauth2Controller {
	
	private static final Logger logger = Logger.getLogger(PosOauth2Controller.class);
	
	@Value("${POSOAuth2AppClientId}")
	private String posClientId;

	@Value("${POSOAuth2AppClientSecret}")
	private String posClientSecret;
	
	@Value("${POSOAuth2AppRedirectUri}")
	private String redirectUri;

	@Value("${POSOAuth2AuthEndpoint}")
	private String posOAuth2AuthEndpoint;

	@Value("${POSOAuth2AccessTokenEndpoint}")
	private String posOAuth2AccessTokenEndpoint;
	    
	// Controller mapping for connectToPos button
	@PostMapping("/connectToPos")
	public View connectToPos(HttpServletRequest request, HttpSession session) {
		
		// Keep the pos company name that user passed in session for furure use
		session.setAttribute("posCompanyName", request.getParameter("posCompanyName"));
		
		// Construct the pos auth URL based on the company name passed in
		String posAuthEndpoint = constructPosEndpoint((String) session.getAttribute("posCompanyName"), true);

		String csrf = generateCSRFToken();
		session.setAttribute("posCsrfToken", csrf);
		
		try {
			List<PosScope> scopes = new ArrayList<PosScope>();
			scopes.add(PosScope.ReadCustomers);
			scopes.add(PosScope.ReadOrders);
			scopes.add(PosScope.ReadProducts);
			scopes.add(PosScope.ReadDraftOrders);
			String url = prepareUrl(posAuthEndpoint, posClientId, scopes, redirectUri, csrf);
			RedirectView redirectView = new RedirectView(url, true, true, false);
			return redirectView;
		} catch (InvalidRequestException e) {
			logger.error("Exception calling connectToPos ", e);
		}
		
		return null;
	}
	
	// This is the redirect handler we configured in POS.
	// Authorization code has a short lifetime. Hence proceed to exchange the
	// Authorization Code for BearerToken.
	@GetMapping("/posOAauth2Redirect")
	public String callBackFromOAuth(@RequestParam("code") String authCode, @RequestParam("state") String state, HttpSession session) {

		// Ensure the CSRF token returned matches with what we sent.
		String csrfToken = (String) session.getAttribute("posCsrfToken");
		if (!csrfToken.equals(state)) {
			logger.error("CSRF token mismatch. Existing.");
			return null;
		}
		else {
			
			String posOAuthAccessTokenEndpoint = constructPosEndpoint((String) session.getAttribute("posCompanyName"), false);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("client_id", posClientId);
			map.add("client_secret", posClientSecret);
			map.add("code", authCode);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			// Retrieve accessToken
			PosBearerToken posBearerToken = restTemplate.postForObject(posOAuthAccessTokenEndpoint, request, PosBearerToken.class);
			
			// Save the access token in session so that we use it for subsequent API calls
			session.setAttribute("posAccessToken", posBearerToken.getAccessToken());
			logger.info("User [" + session.getAttribute("email") + "] successfully successfully connected to Shopify company [" + session.getAttribute("posCompanyName") + "].");
			return "3.connectToQbo"; // the page we go to after "Connect to QBO"
		}
	}


	private String prepareUrl(String authEndpoint, String posClientId, List<PosScope> scopes, String redirectUri, String csrfToken) throws InvalidRequestException  {
		
		if(scopes == null || scopes.isEmpty() || redirectUri.isEmpty() || csrfToken.isEmpty()) {
			logger.error("Invalid request for prepareUrl ");
			throw new InvalidRequestException("Invalid request for prepareUrl");
		}
		try {
			return authEndpoint 
					+ "?client_id=" + posClientId 
					+ "&response_type=code&scope=" + URLEncoder.encode(buildScopeString(scopes), "UTF-8") 
					+ "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") 
					+ "&state=" + csrfToken;
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception while preparing url for redirect ", e);
			throw new InvalidRequestException(e.getMessage(), e);
		}
	}
	
	private String buildScopeString(List<PosScope> scopes) {
		StringBuilder sb = new StringBuilder();
		for (PosScope scope: scopes) {
			sb.append(scope.value() + ",");
		}
		return StringUtils.stripEnd(sb.toString(), ",");
	}

	// This function constructs the pos OAuth2 endpoints depending on the posCompanyName
	// Note: Shopify has different auth and accessToken end points.
	private String constructPosEndpoint(String posCompanyName, boolean auth) {
		String secondPartOfPosEndpointUrl = auth? posOAuth2AuthEndpoint : posOAuth2AccessTokenEndpoint;
		return "https://" + posCompanyName + secondPartOfPosEndpointUrl;
	}

	// Our own CSRF token generator
	private String generateCSRFToken() {
		String csrf = UUID.randomUUID().toString();
		csrf.replace("-", "");
		return csrf;
	}
}
