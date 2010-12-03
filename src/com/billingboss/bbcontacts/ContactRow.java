package com.billingboss.bbcontacts;

import java.util.HashMap;

public class ContactRow extends HashMap<String, String> {
	
	private static final long serialVersionUID = 1L;	

	public String contact_id;
	public String customer_id;
	public String display_name;
	public String first_name;
	public String last_name;
	public String email;
	public String phone;
	
	public static String KEY_CONTACT_ID = "contact_id";
	public static String KEY_CUSTOMER_ID = "customer_id";
	public static String KEY_DISPLAY_NAME = "display_name";
	public static String KEY_FIRST_NAME = "first_name";
	public static String KEY_LAST_NAME = "last_name";
	public static String KEY_EMAIL = "email";
	public static String KEY_PHONE = "phone";

	public ContactRow(String contact_id, 
					  String customer_id,
					  String display_name,
					  //String first_name,
					  //String last_name,
					  String email,
					  String phone) {
		this.contact_id = contact_id;
		this.customer_id = customer_id;
		this.display_name = display_name;
		// assign first and last name from display name
		splitName();
		this.email = email;
		this.phone = phone;
	}	

	@Override
	public String get(Object k) {
		String key = (String) k;
		if (KEY_CONTACT_ID.equals(key))
			return contact_id;
		else if (KEY_CUSTOMER_ID.equals(key))
			return customer_id;
		else if (KEY_DISPLAY_NAME.equals(key))
			return display_name;
		else if (KEY_FIRST_NAME.equals(key))
			return first_name;
		else if (KEY_LAST_NAME.equals(key))
	  		return last_name;
		else if (KEY_EMAIL.equals(key))
			return email;
		else if (KEY_PHONE.equals(key))
			return phone;
		return null;
	}	
	
	private void splitName() {
		try {
		
			String[] names = this.display_name.split(" ");
			if (names.length == 0) {
				return;
			}
			
			if (names.length == 1) {
				this.first_name = names[0];
				return;
			}
			
			// get the last name
			this.last_name = names[names.length - 1];
			
			this.first_name = "";
			for (int i = 0; i < names.length - 1; i++) {
				this.first_name = this.first_name + names[i];
				// add a blank between names
				if (i < names.length - 2) {
					this.first_name = this.first_name + " ";
				}
			}
		}
		catch (Exception e) {
			
		}
	}
}
