package com.billingboss.bbcontacts;

public class Email {
	private String address;
	private String type;

	// constructor
	public Email(String a, String t) {
		this.address = a;
		this.type = t;
	}

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

	// class variable
	// 1 Home, 2 Work, 3 Other, 4 Mobile	
	static public enum emailTypes {
		Home(1),
		Work(2),
		Other(3),
		Mobile(4);

		private final int index;

		private emailTypes(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}		
	}

	// class method
	static public String getNameByIndex(int index) {
		switch(index) {
		case 1:
			return emailTypes.Home.name();
		case 2:
			return emailTypes.Work.name();
		case 3:
			return emailTypes.Other.name();
		case 4:
			return emailTypes.Mobile.name();
		default:
			return "";
		}
	}

	// class method
	static public int getIndexByName(String name) {

		if (name.equals(emailTypes.Home.name())) {
			return 1;
		}
		else if	(name.equals(emailTypes.Work.name())) {
			return 2;
		}
		else if	(name.equals(emailTypes.Other.name())) {
			return 3;
		}
		else if	(name.equals(emailTypes.Mobile.name())) {
			return 4;
		}
		else {
			return 0;
		}
	}
}
