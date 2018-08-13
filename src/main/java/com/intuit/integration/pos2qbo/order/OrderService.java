package com.intuit.integration.pos2qbo.order;

import java.util.List;

import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.exception.FMSException;

public interface OrderService {
	
	List<Result> createQboSalesReceipts(String qboCompanyId, String qboAccessToken, List<SalesReceipt> qboSalesReceipts) throws FMSException;
	void mapOrderToSalesReceipt(PosOrder posOrder, SalesReceipt salesReceipt);
	public void mapLineItems(PosLineItem posLineItem, Line qboLine);
	public PosOrders getPosOrders(String posCompany, String posAccessToken, String fromDate, String toDate);

}
