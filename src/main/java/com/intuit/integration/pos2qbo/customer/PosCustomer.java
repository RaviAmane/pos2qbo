package com.intuit.integration.pos2qbo.customer;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PosCustomer implements Serializable {

	private static final long serialVersionUID = 282421459104836139L;
	private BigInteger id;
	private String email;
	@JsonProperty("first_name")
	private String firstName;
	@JsonProperty("last_name")
	private String lastName;
	@JsonProperty("default_address")
	private PosPhysicalAddress defaultAddress;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public PosPhysicalAddress getDefaultAddress() {
		return defaultAddress;
	}
	public void setDefaultAddress(PosPhysicalAddress defaultAddress) {
		this.defaultAddress = defaultAddress;
	}
	

}
