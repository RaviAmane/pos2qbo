package com.intuit.integration.pos2qbo.customer;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intuit.integration.pos2qbo.DataServiceFactory;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.EmailAddress;
import com.intuit.ipp.data.PhysicalAddress;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.TelephoneNumber;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.oauth2.exception.OAuthException;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class);

	@Autowired
	DataServiceFactory dataServiceFactory;

	@Autowired
	CustomerMappingRepository customerMappingRepository;
	
	@Override
	public ReferenceType addCustomerToQboAndGetReference(String qboCompanyId, String qboRefreshToken, String posCompany,
			PosCustomer posCustomer) throws OAuthException, FMSException {
		Customer qboCustomer = addPosCustomerToQbo(qboCompanyId, qboRefreshToken, posCompany, posCustomer);
		ReferenceType customerRef = new ReferenceType();
		customerRef.setName(qboCustomer.getDisplayName());
		customerRef.setValue(qboCustomer.getId());
		return customerRef;
	}

	private Customer addPosCustomerToQbo(String qboCompanyId, String qboRefreshToken, String posCompany,
			PosCustomer posCustomer) throws FMSException, OAuthException {

		// Check if we have already added this customer to QBO
		Customer qboCustomer = getSavedCustomerIfAnyFromCustomerMapping(posCompany, posCustomer.getId(), qboCompanyId);

		// If already added, just return added customer
		if (qboCustomer != null) {
			return qboCustomer;
		} else { // If not already added, then add it to QBO
			qboCustomer = new Customer();
			mapCustomer(posCustomer, qboCustomer);
			DataService service = dataServiceFactory.getDataService(qboCompanyId, qboRefreshToken);
			Customer savedCustomer = service.add(qboCustomer);
			addCustomerToCustomerMapping(posCompany, posCustomer.getId(), qboCompanyId, savedCustomer.getId(),
					savedCustomer.getDisplayName());
			logger.info("Saved customer [" + savedCustomer.getDisplayName() + "] to QBO.");
			return savedCustomer;
		}
	}

	// Returns the customer from CustomerMapping
	// Returns null, if no customer found in CustomerMapping.
	private Customer getSavedCustomerIfAnyFromCustomerMapping(String posCompany, BigInteger posCustomerId,
			String qboCompany) {

		List<CustomerMapping> customerMapping = customerMappingRepository
				.findByPosCompanyAndPosCustomerIdAndQboCompany(posCompany, posCustomerId, qboCompany);

		if (customerMapping.size() == 0)
			return null;
		else {
			Customer existingCustomer = new Customer();
			existingCustomer.setId(customerMapping.get(0).getQboCustomerId());
			existingCustomer.setDisplayName(customerMapping.get(0).getQboCustomerDisplayName());
			logger.info("Retrieved customer [" + existingCustomer.getDisplayName() + "] from CustomerMapping.");
			return existingCustomer;
		}
	}

	// When a customer is added to QBO,
	// this function adds it to the CustomerMapping table.
	private void addCustomerToCustomerMapping(String posCompany, BigInteger posCustomerId, String qboCompany,
			String qboCustomerId, String qboCustomerDisplayName) {
		CustomerMapping customerMapping = new CustomerMapping();
		customerMapping.setPosCompany(posCompany);
		customerMapping.setPosCustomerId(posCustomerId);
		customerMapping.setQboCompany(qboCompany);
		customerMapping.setQboCustomerId(qboCustomerId);
		customerMapping.setQboCustomerDisplayName(qboCustomerDisplayName);
		customerMappingRepository.save(customerMapping);
	}

	// Map the Shopify customer to that in QBO.
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
