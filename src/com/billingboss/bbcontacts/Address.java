package com.billingboss.bbcontacts;

import java.util.ArrayList;

public class Address extends ContractAddress {
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
 	
 	public Address() {
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
 	
	// 1 Home, 2 Work, 3 Other	
	public enum addressTypes {
		Home(1),
		Work(2),
		Other(3);

		private final int index;

		private addressTypes(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}		
	}

	// class method
	public String getNameByIndex(int index) {
		switch(index) {
		case 1:
			return addressTypes.Home.name();
		case 2:
			return addressTypes.Work.name();
		case 3:
			return addressTypes.Other.name();
		default:
			return "";
		}
	}

	// class method
	public int getIndexByName(String name) {

		if (name.equals(addressTypes.Home.name())) {
			return 1;
		}
		else if	(name.equals(addressTypes.Work.name())) {
			return 2;
		}
		else if	(name.equals(addressTypes.Other.name())) {
			return 3;
		}
		else {
			return 0;
		}
	}
	
	public ArrayList<String> typeToArrayList() {
		ArrayList<String> list = new ArrayList<String>() ;
		list.add(addressTypes.Home.name());
		list.add(addressTypes.Work.name());
		list.add(addressTypes.Other.name());
		return list;
	}	
 	
 }

