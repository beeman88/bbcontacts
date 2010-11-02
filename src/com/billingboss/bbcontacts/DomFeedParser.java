package com.billingboss.bbcontacts;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import android.util.Log;

public class DomFeedParser extends BaseFeedParser {
	
	private static final String TAG = "DomFeedParser";	

	protected DomFeedParser() {
		super();
	}

	public List<Customer> parse() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<Customer> customers = new ArrayList<Customer>();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.getInputStream());
			Element root = dom.getDocumentElement();
			NodeList entries = root.getElementsByTagName(ENTRY);
			for (int i=0;i<entries.getLength();i++){
				Customer customer = new Customer();
				Element entry = (Element) entries.item(i);
				
				Element payload = getElementByTagName(PAYLOAD, entry);
				Element tradingAccount = getElementByTagName(TRADING_ACCOUNT, payload);
				String bb_id = tradingAccount.getAttribute(KEY);
				String name = getTagValue(NAME, tradingAccount);				

				customer.setName(name);
				customer.setBBId(bb_id);
				customers.add(customer);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		return customers;
	}
	
    private static Element getElementByTagName(String tag, Element element) {
        return (Element) element.getElementsByTagName(tag).item(0);
    }	
	
    private static String getTagValue(String tag, Element element) {
        Element firstElementWithName = (Element) element.getElementsByTagName(tag).item(0);
        Node firstChild = firstElementWithName.getFirstChild() ;
        String value = firstChild.getNodeValue() ;
        
        return value ;
    }	
	
	
}
