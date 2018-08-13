package com.intuit.integration.pos2qbo.oauth2;

public enum PosScope {
	
	ReadOrders("read_orders"),
	ReadAllOrders("read_all_orders"), // we will not use this scope; it needs pre-approval
	ReadCustomers("read_customers"), 
	ReadProducts("read_products"), 				
	ReadProductListings("read_product_listings"), // we will probably not use this scope	
	ReadDraftOrders("read_draft_orders");
	
	private final String value;

	PosScope(final String value) {
        this.value = value;
    }

	public String value() {
        return value;
    }
}
