package com.intuit.integration.pos2qbo.order;

import java.util.List;

import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.exception.FMSException;
import com.intuit.oauth2.exception.OAuthException;

public interface OrderService {
	
	List<Result> createQboSalesReceipts(String qboCompanyId, String qboAccessToken, List<SalesReceipt> qboSalesReceipts)  throws FMSException, OAuthException ;
	void mapOrderToSalesReceipt(PosOrder posOrder, SalesReceipt salesReceipt, ReferenceType taxcodeRef);
	public void mapLineItems(PosLineItem posLineItem, Line qboLine, ReferenceType taxcodeRef);
	public PosOrders getPosOrders(String posCompany, String posAccessToken, String fromDate, String toDate);

}
