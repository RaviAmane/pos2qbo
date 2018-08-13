package com.intuit.integration.pos2qbo.customer;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PosPhysicalAddress implements Serializable {

	private static final long serialVersionUID = -6815344424119766934L;
	
	private String name;
	private String address1;
	private String phone;
	private String city;
	private String zip;
	private String province;
	@JsonProperty("province_code")
	private String provinceCode;
	private String country;
	@JsonProperty("country_code")
	private String countryCode;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
