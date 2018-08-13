package com.intuit.integration.pos2qbo.customer;

import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// This entity holds the mappings between Shopify customers and QBO customers.
// This way, the application checks if the customer is already added to QBO
// before attempting to add it.
@Entity
public class CustomerMapping {
	
	@Id
	@GeneratedValue
	private BigInteger id;
	private String posCompany;
	private String qboCompany;
	private BigInteger posCustomerId;
	private String qboCustomerDisplayName;
	private String qboCustomerId;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getPosCompany() {
		return posCompany;
	}
	public void setPosCompany(String posCompany) {
		this.posCompany = posCompany;
	}
	public String getQboCompany() {
		return qboCompany;
	}
	public void setQboCompany(String qboCompany) {
		this.qboCompany = qboCompany;
	}
	public BigInteger getPosCustomerId() {
		return posCustomerId;
	}
	public void setPosCustomerId(BigInteger posCustomerId) {
		this.posCustomerId = posCustomerId;
	}
	public String getQboCustomerId() {
		return qboCustomerId;
	}
	public void setQboCustomerId(String qboCustomerId) {
		this.qboCustomerId = qboCustomerId;
	}
	public String getQboCustomerDisplayName() {
		return qboCustomerDisplayName;
	}
	public void setQboCustomerDisplayName(String qboCustomerDisplayName) {
		this.qboCustomerDisplayName = qboCustomerDisplayName;
	}
}
