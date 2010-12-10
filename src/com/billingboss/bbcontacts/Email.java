package com.billingboss.bbcontacts;

public class Email {
 	private String address;
 	private String type;
 	public String getAddress() {
 		return address;
 	}
 	public void setAddress(String address) {
 		this.address = address;
 	}
 	public String getType() {
 		return type;
 	}
 	public void setType(String t) {
 		this.type = t;
 	}
 	
 	public Email(String a, String t) {
 		this.address = a;
 		this.type = t;
 	}
 }
