package com.billingboss.bbcontacts;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;


import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

public class AndroidSaxFeedParser extends BaseFeedParser {

	public AndroidSaxFeedParser() {
		super();
	}

	public List<Customer> parse() {
		final Customer currentCustomer = new Customer();
		RootElement root = new RootElement(FEED);
		final List<Customer> customers = new ArrayList<Customer>();
		Element entry = root.getChild(ENTRY);
		Element payload = entry.getChild(PAYLOAD);
		Element tradingAccount = payload.getChild(TRADING_ACCOUNT);
		payload.setEndElementListener(new EndElementListener(){
			public void end() {
				customers.add(currentCustomer.copy());
			}
		});
		
		tradingAccount.setStartElementListener(new StartElementListener() {
			public void start(Attributes attributes) {
				currentCustomer.setBBId(attributes.getValue(KEY));
			}
		});		
		
		payload.getChild(NAME).setEndTextElementListener(new EndTextElementListener(){
			public void end(String name) {
				currentCustomer.setName(name);
			}
		});
		
		try {
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return customers;
	}
}
