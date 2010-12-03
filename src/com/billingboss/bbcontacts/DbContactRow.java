package com.billingboss.bbcontacts;

import java.util.HashMap;

public class DbContactRow extends HashMap<String, String> {
	
	private static final long serialVersionUID = 1L;	

	public String contact_id;
	public String customer_id;
	public static String KEY_CONTACT_ID = "contact_id";
	public static String KEY_CUSTOMER_ID = "customer_id";


	public DbContactRow(String contact_id, String customer_id) {
		this.contact_id = contact_id;
		this.customer_id = customer_id;
    
	}	

	@Override
	public String get(Object k) {
		String key = (String) k;
		if (KEY_CONTACT_ID.equals(key))
			return contact_id;
		else if (KEY_CUSTOMER_ID.equals(key))
			return customer_id;
		return null;
	}	
}
