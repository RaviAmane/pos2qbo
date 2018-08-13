package com.intuit.integration.pos2qbo.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CustomerMapping {
	
	@Id
	@GeneratedValue
	private Long id;
	private String posCompany;
	private String qboCompany;
	private String posCustomerId;
	private String qboCustomerId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public String getPosCustomerId() {
		return posCustomerId;
	}
	public void setPosCustomerId(String posCustomerId) {
		this.posCustomerId = posCustomerId;
	}
	public String getQboCustomerId() {
		return qboCustomerId;
	}
	public void setQboCustomerId(String qboCustomerId) {
		this.qboCustomerId = qboCustomerId;
	}	
}
