package com.intuit.integration.pos2qbo.helper;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intuit.integration.pos2qbo.DataServiceFactory;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.TaxCode;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.oauth2.exception.OAuthException;

@Service
public class TaxCodeInfo {

	private static final Logger logger = Logger.getLogger(TaxCodeInfo.class);

	@Autowired
	DataServiceFactory dataServiceFactory;

	// For the purpose of this PoC, we simply get the first and only tax code defined.
	// Ideally this needs to be matched accurately.
	public TaxCode getTaxCode(String qboCompanyId, String qboRefreshToken) throws OAuthException, FMSException {
		DataService service = dataServiceFactory.getDataService(qboCompanyId, qboRefreshToken);
		List<TaxCode> taxcodes = (List<TaxCode>) service.findAll(new TaxCode());
		logger.info("Successfully obtained the tax code reference." + taxcodes.size());
		return taxcodes.get(0);
	}

	public ReferenceType getTaxCodeRef(TaxCode taxcode) {
		ReferenceType taxcodeRef = new ReferenceType();
		taxcodeRef.setName(taxcode.getName());
		taxcodeRef.setValue(taxcode.getId());
		return taxcodeRef;
	}
}
