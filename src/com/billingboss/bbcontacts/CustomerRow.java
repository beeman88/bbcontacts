package com.billingboss.bbcontacts;

import java.util.HashMap;

public class CustomerRow extends HashMap {

	public String name;
	public String bb_id;
	public static String KEY_NAME = "name";
	public static String KEY_BB_ID = "bb_id";


	public CustomerRow(String name, String bb_id) {
		this.name = name;
		this.bb_id = bb_id;
    
	}	

	@Override
	public String get(Object k) {
		String key = (String) k;
		if (KEY_NAME.equals(key))
			return name;
		else if (KEY_BB_ID.equals(key))
			return bb_id;
		return null;
	}	
}
