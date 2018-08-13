package com.intuit.integration.pos2qbo.order;

import java.io.Serializable;
import java.math.BigDecimal;

public class PosTaxLine implements Serializable {

	private static final long serialVersionUID = -8525584734688932898L;
	
	private BigDecimal price;
	private BigDecimal rate;
	private String title;
	
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
