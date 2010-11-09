package com.billingboss.bbcontacts;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.net.Uri;
import android.util.Log;

public class DomFeedParser extends BaseFeedParser {
	
	private String startIndex;
	private String totalResults;
	private String itemsPerPage;
	
	private static final String TAG = "DomFeedParser";

	protected DomFeedParser() {
		super();
	}

	public String getStartIndex() {
		return this.startIndex;
	}
	
	private void setStartIndex (String startIndex) {
		this.startIndex = startIndex;
	}
	
	public String getTotalResults() {
		return this.totalResults;
	}
	
	private void setTotalResults (String totalResults) {
		this.totalResults = totalResults;
	}

	public String getItemsPerPage() {
		return this.itemsPerPage;
	}
	
	private void setItemsPerPage (String itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	
	public List<Customer> parse() {
		List<Customer> customers = new ArrayList<Customer>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.getInputStream());
			Element root = dom.getDocumentElement();
			
			this.setTotalResults(getTagValue(TOTAL_RESULTS, root));
			this.setStartIndex(getTagValue(OPENSEARCH_START_INDEX, root));
			this.setItemsPerPage(getTagValue(ITEMS_PER_PAGE, root));
			
			NodeList entries = root.getElementsByTagName(ENTRY);
			for (int i=0; i<entries.getLength(); i++){
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
    	try {
    		return (Element) element.getElementsByTagName(tag).item(0);
    	}
    	catch (Exception e) {
    		Log.e(TAG, e.getLocalizedMessage());    		
    		return null;
    	}
    }	
	
    private static String getTagValue(String tag, Element element) {
    	try {
	        Element firstElementWithName = (Element) element.getElementsByTagName(tag).item(0);
	        Node firstChild = firstElementWithName.getFirstChild() ;
	        return firstChild.getNodeValue() ;
    	}
	    catch (Exception e) {
    		Log.e(TAG, e.getLocalizedMessage());	    	
	    	return null;
	    }
    }	
	
}

/*			// get links looking for next link
NodeList links = root.getElementsByTagName(LINK);
// no next link means there is no more paging			
this.setNext(false);			
for (int i=0; i<links.getLength(); i++) {
	Element link = (Element) links.item(i);
	String rel = link.getAttribute(REL);
	if (rel.compareTo(NEXT) == 0) {
		this.setNext(true);
		String href = link.getAttribute(HREF);
		
		try {
			Uri uri = Uri.parse(href);
			// set the new start index with the next link startIndex for the next request
			this.setStartIndex(uri.getQueryParameter(START_INDEX));
		}
		catch ( Exception e ) {
			this.setNext(false);
		}
		break;
	}
}*/
