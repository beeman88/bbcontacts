package com.billingboss.bbcontacts;

import java.util.HashMap;

public class ContactRow extends HashMap {

	public String name;
	public String number;
	public String email;
	public static String KEY_NAME = "name";
	public static String KEY_NUMBER = "number";
	public static String KEY_EMAIL = "email";	

	public ContactRow(String name, String number, String email) {
		this.name = name;
		this.number = number;
		this.email = email;	    
	}	

	@Override
	public String get(Object k) {
		String key = (String) k;
		if (KEY_NAME.equals(key))
			return name;
		else if (KEY_NUMBER.equals(key))
			return number;
		else if (KEY_EMAIL.equals(key))
			return email;
		return null;
	}	
}
