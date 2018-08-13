package com.intuit.integration.pos2qbo.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PosOrders implements Serializable{

	private static final long serialVersionUID = 4228920223500461631L;
	
	private List<PosOrder> posOrders = new ArrayList<PosOrder>();

	@JsonProperty("orders") // /admin/orders.json returns json object "orders"
	public List<PosOrder> getPosOrders() {
		return posOrders;
	}

	public void setPosOrders(List<PosOrder> posOrders) {
		this.posOrders = posOrders;
	}
}

