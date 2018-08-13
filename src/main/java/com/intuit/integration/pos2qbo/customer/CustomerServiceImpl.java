package com.intuit.integration.pos2qbo.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.EmailAddress;
import com.intuit.ipp.data.PhysicalAddress;
import com.intuit.ipp.data.TelephoneNumber;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.IAuthorizer;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	@Autowired
	private CustomerMappingRepository customerMappingRepository;
	
	@Autowired
	private CustomerService customerService;

	@Override
	public Customer addPosCustomerToQbo(String qboCompanyId, String qboAccessToken, PosCustomer posCustomer) throws FMSException {
		
		// Check if the posCustomer is already added to QBO
		List<CustomerMapping> customerMapping = customerMappingRepository
				.findByPosCompanyAndPosCustomerIdAndQboCompany("craftdemo1", posCustomer.getId().toString(), "");
		
		Customer qboCustomer = new Customer();

		if(customerMapping != null && !customerMapping.isEmpty()) { // if customer is already added to QBO
			// get the reference to QBO customer
		} else { // else, add the customer to QBO
			customerService.mapCustomer(posCustomer, qboCustomer);
		}
		
		// Create an authorizer that we will pass to the context
		IAuthorizer oAuth2 = new OAuth2Authorizer(qboAccessToken);
		Context context = new Context(oAuth2, ServiceType.QBO, qboCompanyId);
		DataService service = new DataService(context);
		
		Customer savedCustomer = service.add(qboCustomer);

		return savedCustomer;
	}
	
	@Override
	public void mapCustomer(PosCustomer posCustomer, Customer qboCustomer) {
		qboCustomer.setFamilyName(posCustomer.getLastName());
		qboCustomer.setGivenName(posCustomer.getFirstName());
		qboCustomer.setDisplayName(posCustomer.getFirstName() + " " + posCustomer.getLastName());
		
		TelephoneNumber primaryPhone = new TelephoneNumber();
		primaryPhone.setFreeFormNumber(posCustomer.getDefaultAddress().getPhone());
		qboCustomer.setPrimaryPhone(primaryPhone);

		EmailAddress emailAddress = new EmailAddress();
		emailAddress.setAddress(posCustomer.getEmail());
		qboCustomer.setPrimaryEmailAddr(emailAddress);

		PhysicalAddress qboPhysicalAddress = new PhysicalAddress();
		qboPhysicalAddress.setLine1(posCustomer.getDefaultAddress().getAddress1());
		qboPhysicalAddress.setCity(posCustomer.getDefaultAddress().getCity());
		qboPhysicalAddress.setCountrySubDivisionCode(posCustomer.getDefaultAddress().getProvinceCode());
		qboPhysicalAddress.setCountry(posCustomer.getDefaultAddress().getCountry());
		qboPhysicalAddress.setCountryCode(posCustomer.getDefaultAddress().getCountryCode());
		qboPhysicalAddress.setPostalCode(posCustomer.getDefaultAddress().getZip());
		
		qboCustomer.setBillAddr(qboPhysicalAddress);
		qboCustomer.setShipAddr(qboPhysicalAddress);
	}


}
