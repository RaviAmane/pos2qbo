package com.intuit.integration.pos2qbo.customer;

import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.oauth2.exception.OAuthException;

public interface CustomerService {
	
	// This function add the customer to QBO
	// Returns the reference to newly added customer (or to the existing customer)
	ReferenceType addCustomerToQboAndGetReference(String qboCompanyId, String qboRefreshToken, String posCompany,
			PosCustomer customer) throws OAuthException, FMSException;

}
