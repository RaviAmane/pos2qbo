package com.intuit.integration.pos2qbo.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PosLineItem implements Serializable {

	private static final long serialVersionUID = -3214575762376827454L;
	
	private Long product_id;
	private String title;
	private String name;
	private String sku;
	private BigDecimal price;
	private BigDecimal quantity;
	private Boolean taxable;
	@JsonProperty("tax_lines")
	private List<PosTaxLine> taxLines = new ArrayList<PosTaxLine>();
	
	public Long getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Long product_id) {
		this.product_id = product_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public Boolean getTaxable() {
		return taxable;
	}
	public void setTaxable(Boolean taxable) {
		this.taxable = taxable;
	}
	
	public List<PosTaxLine> getTaxLines() {
		return taxLines;
	}
	public void setTaxLines(List<PosTaxLine> taxLines) {
		this.taxLines = taxLines;
	}
	@Override
	public String toString() {
		return "PosLineItem [product_id=" + product_id + ", name=" + name + ", sku=" + sku + ", price=" + price
				+ ", quantity=" + quantity + ", taxable=" + taxable + "]";
	}
}