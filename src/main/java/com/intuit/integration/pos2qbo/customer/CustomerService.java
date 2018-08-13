package com.intuit.integration.pos2qbo.customer;

import com.intuit.ipp.data.Customer;
import com.intuit.ipp.exception.FMSException;

public interface CustomerService {
	
	Customer addPosCustomerToQbo(String qboCompanyId, String qboAccessToken, PosCustomer posCustomer) throws FMSException;
	void mapCustomer(PosCustomer posCustomer, Customer qboCustomer);

}
