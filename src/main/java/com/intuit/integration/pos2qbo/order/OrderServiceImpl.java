package com.intuit.integration.pos2qbo.order;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.intuit.integration.pos2qbo.DataServiceFactory;
import com.intuit.ipp.data.EmailAddress;
import com.intuit.ipp.data.Line;
import com.intuit.ipp.data.LineDetailTypeEnum;
import com.intuit.ipp.data.OperationEnum;
import com.intuit.ipp.data.PhysicalAddress;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.SalesItemLineDetail;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.data.TxnTaxDetail;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.BatchOperation;
import com.intuit.ipp.services.DataService;
import com.intuit.oauth2.exception.OAuthException;

@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger logger = Logger.getLogger(OrderServiceImpl.class);
	
	@Autowired
	DataServiceFactory dataServiceFactory;
	
	@Override
	public List<Result> createQboSalesReceipts(String qboCompanyId, String qboRefreshToken, List<SalesReceipt> qboSalesReceipts) throws FMSException, OAuthException {

		DataService service = dataServiceFactory.getDataService(qboCompanyId, qboRefreshToken);

		BatchOperation batchOperation = new BatchOperation();
		int batchId = 0;
		for (SalesReceipt qboSalesReceipt : qboSalesReceipts) {
			batchOperation.addEntity(qboSalesReceipt, OperationEnum.CREATE, "" + ++batchId);
		}
		
		service.executeBatch(batchOperation);
		
		// Retrieve the newly created sales receipts
		SalesReceipt savedSalesReceipt;
		List<Result> results = new ArrayList<Result>();
		for(int i=1; i<=batchId; i++) {
			savedSalesReceipt = (SalesReceipt) batchOperation.getEntity(""+i);
			Result result = new Result();
			result.setDate(savedSalesReceipt.getTxnDate());
			result.setEntityId(savedSalesReceipt.getId());
			results.add(result);
		}
		
		logger.info("Successfully created [" + batchId + "] Sales Receipts in QBO.");
		
		return results;
	}

	@Override
	public PosOrders getPosOrders(String posCompany, String posAccessToken, String fromDate, String toDate) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Shopify-Access-Token", posAccessToken);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		String posApiUrl = constructPosApiUrl(posCompany, fromDate, toDate);
		ResponseEntity<PosOrders> ordersResponse = restTemplate.exchange(posApiUrl, HttpMethod.GET, entity,
				PosOrders.class);
		PosOrders posOrders = ordersResponse.getBody();
		logger.info("Retrieved [" + posOrders.getPosOrders().size() + "] orders from POS.");
		return posOrders;
	}

	private String constructPosApiUrl(String posCompanyName, String fromDate, String toDate) {
		String secondPartOfPosEndpointUrl = ".myshopify.com/admin/orders.json?financial_status=paid" +
											"&updated_at_min=" + fromDate + "&updated_at_max=" + toDate;
		logger.info("POS URL: " + "https://" + posCompanyName + secondPartOfPosEndpointUrl);
		return "https://" + posCompanyName + secondPartOfPosEndpointUrl;
	}

	@Override
	public void mapOrderToSalesReceipt(PosOrder posOrder, SalesReceipt salesReceipt) {

		int lindId = 1;
		List<Line> qboLines = new ArrayList<Line>();
		for (PosLineItem posLineItem : posOrder.getPosLineItems()) {
			Line qboLine = new Line();
			qboLine.setId("" + lindId++);
			qboLine.setDescription(posLineItem.getName());
			mapLineItems(posLineItem, qboLine);
			qboLines.add(qboLine);
		}
		salesReceipt.setLine(qboLines);

		salesReceipt.setDocNumber(posOrder.getOrderNumber());
		salesReceipt.setTotalAmt(posOrder.getTotalPrice());
		TxnTaxDetail txnTaxDetail = new TxnTaxDetail();
		txnTaxDetail.setTotalTax(posOrder.getTotalTax());
		salesReceipt.setTxnTaxDetail(txnTaxDetail);
		salesReceipt.setTxnStatus("Paid");
		salesReceipt.setTxnDate(posOrder.getUpdatedAt());
		ReferenceType refType = new ReferenceType();
		refType.setValue(posOrder.getCurrency());
		salesReceipt.setCurrencyRef(refType);
		EmailAddress emailAddr = new EmailAddress();
		emailAddr.setAddress(posOrder.getEmail());
		salesReceipt.setBillEmail(emailAddr);

		// Set Billing address
		PhysicalAddress qboBillingAddress = new PhysicalAddress();
		if (posOrder.getBillingAddress() != null) {
			qboBillingAddress.setLine1(posOrder.getBillingAddress().getAddress1());
			qboBillingAddress.setCity(posOrder.getBillingAddress().getCity());
			qboBillingAddress.setCountrySubDivisionCode(posOrder.getBillingAddress().getProvinceCode());
			qboBillingAddress.setCountry(posOrder.getBillingAddress().getCountry());
			qboBillingAddress.setCountryCode(posOrder.getBillingAddress().getCountryCode());
			qboBillingAddress.setPostalCode(posOrder.getBillingAddress().getZip());
			salesReceipt.setShipAddr(qboBillingAddress);
		}

		// Set Shipping address
		PhysicalAddress qboShippingAddress = new PhysicalAddress();
		if (posOrder.getShippingAddress() != null) {
			qboShippingAddress.setLine1(posOrder.getShippingAddress().getAddress1());
			qboShippingAddress.setCity(posOrder.getShippingAddress().getCity());
			qboShippingAddress.setCountrySubDivisionCode(posOrder.getShippingAddress().getProvinceCode());
			qboShippingAddress.setCountry(posOrder.getShippingAddress().getCountry());
			qboShippingAddress.setCountryCode(posOrder.getShippingAddress().getCountryCode());
			qboShippingAddress.setPostalCode(posOrder.getShippingAddress().getZip());
			salesReceipt.setShipAddr(qboShippingAddress);
		}
	}

	@Override
	public void mapLineItems(PosLineItem posLineItem, Line qboLine) {

		SalesItemLineDetail qboSalesItemLineDetail = new SalesItemLineDetail();
		qboSalesItemLineDetail.setUnitPrice(posLineItem.getPrice());
		qboSalesItemLineDetail.setQty(posLineItem.getQuantity());

		for (PosTaxLine posTaxLine : posLineItem.getTaxLines()) {
			// TaxCode qboTaxCode = new TaxCode();
			qboSalesItemLineDetail.setTaxInclusiveAmt(posTaxLine.getPrice());
		}

		qboLine.setSalesItemLineDetail(qboSalesItemLineDetail);
		qboLine.setDescription(posLineItem.getTitle());
		qboLine.setAmount(posLineItem.getPrice());
		qboLine.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);
	}

}
