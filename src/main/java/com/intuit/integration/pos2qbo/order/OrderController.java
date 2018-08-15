package com.intuit.integration.pos2qbo.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.intuit.integration.pos2qbo.customer.CustomerService;
import com.intuit.integration.pos2qbo.helper.TaxCodeInfo;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.data.TaxCode;
import com.intuit.ipp.exception.FMSException;
import com.intuit.oauth2.exception.OAuthException;

@Controller
public class OrderController {

	@Autowired
	private OrderService posOrderService;

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private TaxCodeInfo taxCodeInfo;

	@Value("${QBODeepLinkPrefix}")
	private String qboDeepLinkPrefix;

	@PostMapping("/exportOrdersToQbo")
	public String exportOrdersToQbo(HttpServletRequest request, HttpSession session, Model model)
			throws FMSException, OAuthException {

		// Retrieve the request parameters.
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		String exportCustomers = request.getParameter("exportCustomers");

		// Get companies and tokens - used to call respective APIs
		String qboCompanyId = (String) session.getAttribute("qboCompanyId");
		String qboRefreshToken = (String) session.getAttribute("qboRefreshToken");
		String posCompany = (String) session.getAttribute("posCompanyName");
		String posAccessToken = (String) session.getAttribute("posAccessToken");
		
		TaxCode taxcode = taxCodeInfo.getTaxCode(qboCompanyId, qboRefreshToken);
		ReferenceType taxcodeRef = taxCodeInfo.getTaxCodeRef(taxcode);

		// Get pos orders
		PosOrders posOrders = posOrderService.getPosOrders(posCompany, posAccessToken, fromDate, toDate);

		// Export Shopify orders to QBO as sales receipts
		List<SalesReceipt> qboSalesReceipts = new ArrayList<SalesReceipt>();
		for (PosOrder posOrder : posOrders.getPosOrders()) {
			SalesReceipt qboSalesReceipt = new SalesReceipt();
			if (StringUtils.equals(exportCustomers, "true")) {
				ReferenceType customerRef = customerService.addCustomerToQboAndGetReference(qboCompanyId, qboRefreshToken,
						posCompany, posOrder.getCustomer());
				qboSalesReceipt.setCustomerRef(customerRef);
			}
			posOrderService.mapOrderToSalesReceipt(posOrder, qboSalesReceipt, taxcodeRef);
			qboSalesReceipts.add(qboSalesReceipt);
		}

		// Export pos orders as qbo sales receipts
		List<Result> results = posOrderService.createQboSalesReceipts(qboCompanyId, qboRefreshToken, qboSalesReceipts);

		// Format results so that it can be rendered in view
		formatResults(results, qboCompanyId);

		// Populate the model for the view to render
		model.addAttribute("results", results);
		model.addAttribute("totalOrders", results.size());
		
		return "5.done";
	}

	private void formatResults(List<Result> results, String qboCompanyId) {

		for (Result result : results) {
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			String reportDate = df.format(result.getDate());
			result.setDateStr(reportDate);

			String deepLink = qboDeepLinkPrefix + qboCompanyId + "&pagereq=salesreceipt?txnid=" + result.getEntityId();
			result.setLink(deepLink);
		}

	}
}
