package com.intuit.integration.pos2qbo.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.intuit.integration.pos2qbo.customer.PosCustomer;
import com.intuit.integration.pos2qbo.customer.CustomerService;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.exception.FMSException;

@Controller
public class OrderController {

	private static final Logger logger = Logger.getLogger(OrderController.class);

	@Autowired
	private OrderService posOrderService;
	@Autowired
	private CustomerService qboCustomerService;
	
	@Value("${QBODeepLinkPrefix}")
	private String qboDeepLinkPrefix;

	@PostMapping("/exportOrdersToQbo")
	public String exportOrdersToQbo(HttpServletRequest request, HttpSession session, Model model) throws FMSException {

		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		String exportCustomers = request.getParameter("exportCustomers");

		logger.info("fromDate: " + fromDate);
		logger.info("toDate: " + toDate);
		logger.info("exportCustomers: " + exportCustomers);

		// Get companies and tokens - used to call respective APIs
		String qboCompanyId = (String) session.getAttribute("qboCompanyId");
		String qboAccessToken = (String) session.getAttribute("qboAccessToken");
		String posCompany = (String) session.getAttribute("posCompanyName");
		String posAccessToken = (String) session.getAttribute("posAccessToken");

		// Get pos orders
		PosOrders posOrders = posOrderService.getPosOrders(posCompany, posAccessToken, fromDate, toDate);

		// Map pos orders to qbo sales receipts
		List<SalesReceipt> qboSalesReceipts = new ArrayList<SalesReceipt>();
		for (PosOrder posOrder : posOrders.getPosOrders()) {
			SalesReceipt qboSalesReceipt = new SalesReceipt();
			if (StringUtils.equals(exportCustomers, "true")) {
				ReferenceType customerRef = getCustomerReference(qboCompanyId, qboAccessToken, posOrder.getCustomer());
				qboSalesReceipt.setCustomerRef(customerRef);
			}
			posOrderService.mapOrderToSalesReceipt(posOrder, qboSalesReceipt);
			qboSalesReceipts.add(qboSalesReceipt);
		}

		// Export pos orders as qbo sales receipts
		List<Result> results = posOrderService.createQboSalesReceipts(qboCompanyId, qboAccessToken, qboSalesReceipts);
		
		// Format results so that it can be rendered in view
		formatResults(results, qboCompanyId);
		
		// Populate the model for the view to render
		model.addAttribute("results", results);
		model.addAttribute("totalOrders", results.size());
		return "5.done";
	}

	private void formatResults(List<Result> results, String qboCompanyId) {

		for(Result result : results) {
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			String reportDate = df.format(result.getDate());
			result.setDateStr(reportDate);
			
			String deepLink = qboDeepLinkPrefix + qboCompanyId + "&pagereq=salesreceipt?txnid=" + result.getEntityId();
			result.setLink(deepLink);
		}
		
	}

	private ReferenceType getCustomerReference(String qboCompanyId, String qboAccessToken, PosCustomer posCustomer)
			throws FMSException {
		Customer qboCustomer = qboCustomerService.addPosCustomerToQbo(qboCompanyId, qboAccessToken, posCustomer);
		ReferenceType customerRef = new ReferenceType();
		customerRef.setName(qboCustomer.getDisplayName());
		customerRef.setValue(qboCustomer.getId());
		return customerRef;
	}
}
