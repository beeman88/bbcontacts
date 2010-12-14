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
		HOME(1),
		WORK(2),
		OTHER(3),
		MOBILE(4);

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
			return emailTypes.HOME.name();
		case 2:
			return emailTypes.WORK.name();
		case 3:
			return emailTypes.OTHER.name();
		case 4:
			return emailTypes.MOBILE.name();
		default:
			return "";
		}
	}

	// class method
	static public int getIndexByName(String name) {

		if (name.equals(emailTypes.HOME.name())) {
			return 1;
		}
		else if	(name.equals(emailTypes.WORK.name())) {
			return 2;
		}
		else if	(name.equals(emailTypes.OTHER.name())) {
			return 3;
		}
		else if	(name.equals(emailTypes.MOBILE.name())) {
			return 4;
		}
		else {
			return 0;
		}
	}
}
