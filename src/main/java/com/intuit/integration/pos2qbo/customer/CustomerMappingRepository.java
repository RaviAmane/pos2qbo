package com.intuit.integration.pos2qbo.customer;

import java.util.List;

import org.springframework.data.repository.Repository;

public interface CustomerMappingRepository extends Repository<CustomerMapping, Long> {
	List<CustomerMapping> findByPosCompanyAndPosCustomerIdAndQboCompany(String posCompany, String posCustomerId, String qboCompany);
}
