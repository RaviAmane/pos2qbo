package com.intuit.integration.pos2qbo.oauth2;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;

@Controller
public class QboRefreshTokenController {
	
	@Autowired
	QboOAuth2PlatformClientFactory factory;
	
	private static final Logger logger = Logger.getLogger(QboRefreshTokenController.class);
	
    /**
     * Call to refresh tokens 
     * 
     * @param session
     * @return
     * @throws JSONException 
     */
	@ResponseBody
    @RequestMapping("/refreshToken")
    public String refreshToken(HttpSession session) throws JSONException {
		
        try {
        	OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
        	String refreshToken = (String)session.getAttribute("refresh_token");
        	BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);
            session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
            session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());
            String jsonString = new JSONObject()
                    .put("access_token", bearerTokenResponse.getAccessToken())
                    .put("refresh_token", bearerTokenResponse.getRefreshToken()).toString();
            logger.info("Refresh token successful.");
            return jsonString;
        }
        catch (Exception ex) {
        	logger.error("Exception while calling refreshToken ", ex);
        	return new JSONObject().put("response","Failed").toString();
        }    
    }
}
