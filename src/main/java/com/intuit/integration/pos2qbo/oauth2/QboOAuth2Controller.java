package com.intuit.integration.pos2qbo.oauth2;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.data.UserInfoResponse;
import com.intuit.oauth2.exception.InvalidRequestException;
import com.intuit.oauth2.exception.OAuthException;
import com.intuit.oauth2.exception.OpenIdException;

@Controller
public class QboOAuth2Controller {

	private static final Logger logger = Logger.getLogger(QboOAuth2Controller.class);

	@Autowired
	QboOAuth2PlatformClientFactory factory;

	@GetMapping("/")
	public String home() {
		return "1.home"; // the landing page of app
	}

	// Controller mapping for signInWithIntuit button
	@GetMapping("/signInWithIntuit")
	public View signInWithIntuit(HttpSession session) {

		OAuth2Config oauth2Config = factory.getOAuth2Config();
		String csrf = oauth2Config.generateCSRFToken();
		session.setAttribute("intuitCsrfToken", csrf);
		String redirectUri = factory.getPropertyValue("IntuitOAuth2AppRedirectUri");

		try {
			List<Scope> scopes = new ArrayList<Scope>();
			scopes.add(Scope.OpenIdAll); // we are only interested in signing in.
			RedirectView redirectView = new RedirectView(oauth2Config.prepareUrl(scopes, redirectUri, csrf), true, true,
					false);
			return redirectView;
		} catch (InvalidRequestException e) {
			logger.error("Exception calling signInWithIntuit ", e);
		}

		return null;
	}

	// Redirect handler for "Signing in with Intuit".
	// This is the redirect handler you configure in app on developer.intuit.com.
	// The main purpose of "Signing in with Intuit" is to authenticate the user and retrive the user name and email.
	@GetMapping("/intuitOAuth2Redirect")
	public String callBackFromIntuitOAuth(@RequestParam("code") String authCode, @RequestParam("state") String state,
			@RequestParam(value = "realmId", required = false) String realmId, HttpSession session) {

		try {
			// Ensure the CSRF token returned matches with what we sent.
			String csrfToken = (String) session.getAttribute("intuitCsrfToken");
			if (!csrfToken.equals(state)) {
				logger.error("CSRF token mismatch. Existing.");
				return null;
			} else {

				OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
				String redirectUri = factory.getPropertyValue("IntuitOAuth2AppRedirectUri");

				// Retrieve openid profile received after Signin with Intuit
				BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(authCode, redirectUri);
				String idToken = bearerTokenResponse.getIdToken();

				// In case of OpenIdConnect, when we request OpenIdScopes during authorization,
				// we will also receive IDToken from Intuit.
				// We first need to validate that the IdToken actually came from Intuit.
				if (StringUtils.isNotBlank(idToken)) {
					try {
						if (!client.validateIDToken(idToken)) {
							logger.error("Invalid IdToken.");
							return null;
						}
						saveUserDetails(client, bearerTokenResponse, session); // save the user datails and navigate to connectToPos
						return "2.connectToPos"; // the page we go to after "Signin with Intuit"
					} catch (OpenIdException e) {
						logger.error("Exception validating IdToken ", e);
						return null;
					}
				}
			}
		} catch (OAuthException e) {
			logger.error("Exception in Intuit callback handler ", e);
		}
		return null;
	}

	// Controller mapping for connectToQuickbooks button
	@GetMapping("/connectToQbo")
	public View connectToQuickbooks(HttpSession session) {

		OAuth2Config oauth2Config = factory.getOAuth2Config();
		String redirectUri = factory.getPropertyValue("QBOOAuth2AppRedirectUri");
		String csrf = oauth2Config.generateCSRFToken();
		session.setAttribute("qboCsrfToken", csrf);

		try {
			List<Scope> scopes = new ArrayList<Scope>();
			scopes.add(Scope.Accounting);
			String url = oauth2Config.prepareUrl(scopes, redirectUri, csrf);
			RedirectView redirectView = new RedirectView(url, true, true, false);
			return redirectView;
		} catch (InvalidRequestException e) {
			logger.error("Exception calling connectToQuickbooks ", e);
		}

		return null;
	}

	// Redirect handler for "Connect to QBO".
	// This is the redirect handler you configure in app on developer.intuit.com.
	// The main purpose of "Connect to QBO" is to get the access and refresh tokens.
	// Authorization code has a short lifetime.
	// Hence proceed to exchange the authorization code for bearerToken.
	@GetMapping("/qboOAuth2Redirect")
	public String callBackFromQboOAuth(@RequestParam("code") String authCode, @RequestParam("state") String state,
			@RequestParam(value = "realmId", required = false) String realmId, HttpSession session) {

		try {
			// Ensure the CSRF token returned matches with what we sent.
			String csrfToken = (String) session.getAttribute("qboCsrfToken");
			if (!csrfToken.equals(state)) {
				logger.error("CSRF token mismatch. Existing.");
				return null;
			} else {

				OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
				String redirectUri = factory.getPropertyValue("QBOOAuth2AppRedirectUri");

				// Retrieve accessToken and refreshToken
				BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(authCode, redirectUri);
				
				// Save the tokens in the session.
				// We use these tokens to call QBO APIs.
				saveAccessDetails(client, bearerTokenResponse, session, realmId);
				return "4.connected"; // the page we go to after "Connect to QBO"
			}
		} catch (OAuthException e) {
			logger.error("Exception in QBO callback handler ", e);
		}
		return null;
	}

	private void saveUserDetails(OAuth2PlatformClient client, BearerTokenResponse bearerTokenResponse, HttpSession session) {
		try {
			String accessToken = bearerTokenResponse.getAccessToken();
			UserInfoResponse userInfo = client.getUserInfo(accessToken);
			session.setAttribute("givenName", userInfo.getGivenName());
			session.setAttribute("email", userInfo.getEmail());
			logger.info("User [" + userInfo.getEmail() + "] successfully signed in with Intuit.");
		} catch (Exception ex) {
			logger.error("Exception while retrieving user info ", ex);
		}
	}

	private void saveAccessDetails(OAuth2PlatformClient client, BearerTokenResponse bearerTokenResponse, HttpSession session, String qboCompanyId) {
		// TODO Ideally we should save the retryToken and AccessToken in database.
		// For the purpose of this PoC, we just save it in session.
		try {
			String accessToken = bearerTokenResponse.getAccessToken();
			String refreshToken = bearerTokenResponse.getRefreshToken();
			session.setAttribute("qboAccessToken", accessToken);
			session.setAttribute("qboRefreshToken", refreshToken);
			session.setAttribute("qboCompanyId", qboCompanyId);
			logger.info("User [" + session.getAttribute("email") + "] successfully successfully connected to QBO company [" + qboCompanyId + "].");
		} catch (Exception ex) {
			logger.error("Exception while retrieving user info ", ex);
		}
	}
}
