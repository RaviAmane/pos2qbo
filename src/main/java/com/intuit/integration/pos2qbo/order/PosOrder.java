package com.intuit.integration.pos2qbo.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intuit.integration.pos2qbo.customer.PosCustomer;
import com.intuit.integration.pos2qbo.customer.PosPhysicalAddress;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PosOrder implements Serializable {

	private static final long serialVersionUID = 1101995750223702780L;

	private long id;
    private String name;
	private String email;
    @JsonProperty("order_number")
	private String orderNumber;
    private String status;
    @JsonProperty("total_price")
    private BigDecimal totalPrice; // The sum of all line item prices, discounts, shipping, taxes, and tips (must be positive).
    @JsonProperty("subtotal_price")
    private BigDecimal subtotalPrice; // The price of the order after discounts but before shipping, taxes and tips.
    @JsonProperty("total_tax")
    private BigDecimal totalTax; // The sum of all the taxes applied to the order (must be positive).
    @JsonProperty("taxes_included")
	private Boolean taxesIncluded; // Whether taxes are included in the order subtotal.
    private String currency;
    @JsonProperty("financial_status")
    private String financialStatus;
    @JsonProperty("line_items")
    private List<PosLineItem> posLineItems = new ArrayList<PosLineItem>();
    @JsonProperty("updated_at")
    private Date updatedAt;
    private PosCustomer customer;
	@JsonProperty("billing_address")
	private PosPhysicalAddress billingAddress;
	@JsonProperty("shipping_address")
	private PosPhysicalAddress shippingAddress;

    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public BigDecimal getSubtotalPrice() {
		return subtotalPrice;
	}
	public void setSubtotalPrice(BigDecimal subtotalPrice) {
		this.subtotalPrice = subtotalPrice;
	}
	public BigDecimal getTotalTax() {
		return totalTax;
	}
	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}
	public Boolean getTaxesIncluded() {
		return taxesIncluded;
	}
	public void setTaxesIncluded(Boolean taxesIncluded) {
		this.taxesIncluded = taxesIncluded;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getFinancialStatus() {
		return financialStatus;
	}
	public void setFinancialStatus(String financialStatus) {
		this.financialStatus = financialStatus;
	}
	public List<PosLineItem> getPosLineItems() {
		return posLineItems;
	}
	public void setPosLineItems(List<PosLineItem> posLineItems) {
		this.posLineItems = posLineItems;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public PosCustomer getCustomer() {
		return customer;
	}
	public void setCustomer(PosCustomer customer) {
		this.customer = customer;
	}
	public PosPhysicalAddress getBillingAddress() {
		return billingAddress;
	}
	public void setBillingAddress(PosPhysicalAddress billingAddress) {
		this.billingAddress = billingAddress;
	}
	public PosPhysicalAddress getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(PosPhysicalAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
    
}
