package com.billingboss.bbcontacts;

import java.util.HashMap;

public class ContactRow extends HashMap<String, String> {
	
	private static final long serialVersionUID = 1L;	

	public String contact_id;
	public String organization;
	public String display_name;
	public String first_name;
	public String last_name;
	public Address address;
	public String website;
	public String company_phone;	
	public String email;
	public String contact_phone;
	
	public static String KEY_CONTACT_ID = "contact_id";
	public static String KEY_ORGANIZATION = "organization";
	public static String KEY_DISPLAY_NAME = "display_name";
	public static String KEY_FIRST_NAME = "first_name";
	public static String KEY_LAST_NAME = "last_name";
	public static String KEY_PO_BOX = "po_box";
	public static String KEY_STREET = "street";
	public static String KEY_CITY = "city";
	public static String KEY_STATE = "state";
	public static String KEY_POSTAL_CODE = "postal_code";	
	public static String KEY_COUNTRY = "country";
	public static String KEY_WEBSITE = "website";
	public static String KEY_COMPANY_PHONE = "company_phone";
	public static String KEY_EMAIL = "email";
	public static String KEY_CONTACT_PHONE = "contact_phone";

	public ContactRow(String contact_id,
					  String organization,
					  String display_name,
					  Address address,
					  String website,
					  String company_phone,
					  String email,
					  String contact_phone) {
		this.contact_id = contact_id;
		this.organization = organization;
		this.display_name = display_name;
		// assign first and last name from display name
		splitName();
		this.address = address;	
		this.website = website;
		this.company_phone = company_phone;
		this.email = email;
		this.contact_phone = contact_phone;
	}	

	@Override
	public String get(Object k) {
		String key = (String) k;
		if (KEY_CONTACT_ID.equals(key))
			return contact_id;
		else if (KEY_ORGANIZATION.equals(key))
			return organization;
		else if (KEY_DISPLAY_NAME.equals(key))
			return display_name;
		else if (KEY_FIRST_NAME.equals(key))
			return first_name;
		else if (KEY_LAST_NAME.equals(key))
	  		return last_name;
		else if (KEY_PO_BOX.equals(key))
	  		return address.getPoBox();
		else if (KEY_STREET.equals(key))
	  		return address.getStreet();
		else if (KEY_CITY.equals(key))
	  		return address.getCity();
		else if (KEY_STATE.equals(key))
	  		return address.getState();
		else if (KEY_POSTAL_CODE.equals(key))
	  		return address.getPostalCode();
		else if (KEY_WEBSITE.equals(key))
	  		return website;
		else if (KEY_COMPANY_PHONE.equals(key))
			return company_phone;
		else if (KEY_COUNTRY.equals(key))
	  		return address.getCountry();		
		else if (KEY_EMAIL.equals(key))
			return email;
		else if (KEY_CONTACT_PHONE.equals(key))
			return contact_phone;
		return null;
	}	
	
	private void splitName() {
		try {
			// make sure first and last name are not null
			this.first_name = "";
			this.last_name = "";
			
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
