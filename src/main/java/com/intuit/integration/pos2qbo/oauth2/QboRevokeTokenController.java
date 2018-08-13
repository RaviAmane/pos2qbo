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
import com.intuit.oauth2.data.PlatformResponse;

@Controller
public class QboRevokeTokenController {
	
	@Autowired
	QboOAuth2PlatformClientFactory factory;
	
	private static final Logger logger = Logger.getLogger(QboRevokeTokenController.class);
	
    /**
     * Call to revoke tokens 
     * @param session
     * @return
     * @throws JSONException 
     */
	@ResponseBody
    @RequestMapping("/revokeToken")
    public String revokeToken(HttpSession session) throws JSONException {
		
        try {
        	OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
        	String refreshToken = (String)session.getAttribute("refresh_token");
        	PlatformResponse response = client.revokeToken(refreshToken);
            logger.info("Raw result for revoke token request: " + response.getStatus());
            return new JSONObject().put("response", "Revoke successful").toString();
        }
        catch (Exception ex) {
        	logger.error("Exception while calling revokeToken ", ex);
        	return new JSONObject().put("response", "Failed").toString();
        }    
    }
}
