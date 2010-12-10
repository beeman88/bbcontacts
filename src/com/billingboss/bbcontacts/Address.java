package com.billingboss.bbcontacts;

public class Address {
 	private String poBox;
 	private String street;
 	private String city;
 	private String state;
 	private String postalCode;
 	private String country;
 	private String type;
 	private String asString = "";
 	
 	public String getType() {
 		return isNotNullAndEmpty(type);
 	}
 	public void setType(String type) {
 		this.type = type;
 	}
 	public String getPoBox() {
 		return isNotNullAndEmpty(poBox);
 	}
 	public void setPoBox(String poBox) {
 		this.poBox = poBox;
 	}
 	public String getStreet() {
 		return isNotNullAndEmpty(street);
 	}
 	public void setStreet(String street) {
 		this.street = street;
 	}
 	public String getCity() {
 		return isNotNullAndEmpty(city);
 	}
 	public void setCity(String city) {
 		this.city = city;
 	}
 	public String getState() {
 		return isNotNullAndEmpty(state);
 	}
 	public void setState(String state) {
 		this.state = state;
 	}
 	public String getPostalCode() {
 		return isNotNullAndEmpty(postalCode);
 	}
 	public void setPostalCode(String postalCode) {
 		this.postalCode = postalCode;
 	}
 	public String getCountry() {
 		return isNotNullAndEmpty(country);
 	}
 	public void setCountry(String country) {
 		this.country = country;
 	}
 	public String toString() {
 		if (this.asString.length() > 0) {
 			return(this.asString);
 		} else {
 			String addr = "";
 			if (this.getPoBox() != null) {
 				addr = addr + this.getPoBox() + "n";
 			}
 			if (this.getStreet() != null) {
 				addr = addr + this.getStreet() + "n";
 			}
 			if (this.getCity() != null) {
 				addr = addr + this.getCity() + ", ";
 			}
 			if (this.getState() != null) {
 				addr = addr + this.getState() + " ";
 			}
 			if (this.getPostalCode() != null) {
 				addr = addr + this.getPostalCode() + " ";
 			}
 			if (this.getCountry() != null) {
 				addr = addr + this.getCountry();
 			}
 			return(addr);
 		}
 	}
 	
 	public Address(String asString, String type) {
 		this.asString = asString;
 		this.type = type;
 	}
 	
 	public Address(String poBox, String street, String city, String state, 
 			String postal, String country, String type) {
 		this.setPoBox(poBox);
  		this.setStreet(street);
 		this.setCity(city);
 		this.setState(state);
 		this.setPostalCode(postal);
 		this.setCountry(country);
 		this.setType(type);
 	}
 	
 	public String isNotNullAndEmpty(String str) {
 	    if((null == str) || (str.length() == 0)) {
 	        return "";
 	    }
        return str;
 	}
 }
