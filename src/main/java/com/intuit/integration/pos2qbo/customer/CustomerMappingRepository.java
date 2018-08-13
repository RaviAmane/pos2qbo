package com.intuit.integration.pos2qbo.customer;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerMappingRepository extends JpaRepository<CustomerMapping, Long> {
	List<CustomerMapping> findByPosCompanyAndPosCustomerIdAndQboCompany(String posCompany, BigInteger posCustomerId, String qboCompany);
}
