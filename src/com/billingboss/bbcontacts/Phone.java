package com.billingboss.bbcontacts;

import java.util.ArrayList;

public class Phone extends ContractAddress {
 	private String number;
 	private String type;
 	
 	public String getNumber() {
 		return number;
 	}
 
 	public void setNumber(String number) {
 		this.number = number;
 	}
 
 	public String getType() {
 		return type;
 	}
 
 	public void setType(String type) {
 		this.type = type;
 	}
 
 	public Phone(String n, String t) {
 		this.number = n;
 		this.type = t;
 	}
 	
 	public Phone() {
 	}
 	
	// a lot of types of Phones	
 	public enum phoneTypes {
		Home(1),
		Mobile(2),
		Work(3),
		FaxHome(4),
		FaxWork(5),
		Pager(6),
		Other(7),
		Callback(8),
		Car(9),
		CompanyMain(10),
		ISDN(11),
		Main(12),
		OtherFax(13),
		Radio(14),
		Telex(15),
		TTYTTD(16),
		WorkMobile(17),
		WorkPager(18),
		Assistant(19),
		MMS(20)
		;

		private final int index;

		private phoneTypes(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}		
	}

	public String getNameByIndex(int index) {
		switch(index) {
		case 1:
			return phoneTypes.Home.name();
		case 2:
			return phoneTypes.Mobile.name();
		case 3:
			return phoneTypes.Work.name();
		case 4:
			return phoneTypes.FaxHome.name();
		case 5:
			return phoneTypes.FaxWork.name();
		case 6:
			return phoneTypes.Pager.name();
		case 7:
			return phoneTypes.Other.name();
		case 8:
			return phoneTypes.Callback.name();
		case 9:
			return phoneTypes.Car.name();
		case 10:
			return phoneTypes.CompanyMain.name();
		case 11:
			return phoneTypes.ISDN.name();
		case 12:
			return phoneTypes.Main.name();
		case 13:
			return phoneTypes.OtherFax.name();
		case 14:
			return phoneTypes.Radio.name();
		case 15:
			return phoneTypes.Telex.name();
		case 16:
			return phoneTypes.TTYTTD.name();
		case 17:
			return phoneTypes.WorkMobile.name();
		case 18:
			return phoneTypes.WorkPager.name();
		case 19:
			return phoneTypes.Assistant.name();
		case 20:
			return phoneTypes.MMS.name();
		default:
			return "";
		}
	}

	public int getIndexByName(String name) {

		if (name.equals(phoneTypes.Home.name())) {
			return 1;
		}
		else if	(name.equals(phoneTypes.Mobile.name())) {
			return 2;
		}
		else if	(name.equals(phoneTypes.Work.name())) {
			return 3;
		}
		else if	(name.equals(phoneTypes.FaxHome.name())) {
			return 4;
		}
		else if	(name.equals(phoneTypes.FaxWork.name())) {
			return 5;
		}
		else if	(name.equals(phoneTypes.Pager.name())) {
			return 6;
		}
		else if	(name.equals(phoneTypes.Other.name())) {
			return 7;
		}
		else if	(name.equals(phoneTypes.Callback.name())) {
			return 8;
		}
		else if	(name.equals(phoneTypes.Car.name())) {
			return 9;
		}
		else if	(name.equals(phoneTypes.CompanyMain.name())) {
			return 10;
		}
		else if	(name.equals(phoneTypes.ISDN.name())) {
			return 11;
		}
		else if	(name.equals(phoneTypes.Main.name())) {
			return 12;
		}
		else if	(name.equals(phoneTypes.OtherFax.name())) {
			return 13;
		}
		else if	(name.equals(phoneTypes.Radio.name())) {
			return 14;
		}
		else if	(name.equals(phoneTypes.Telex.name())) {
			return 15;
		}
		else if	(name.equals(phoneTypes.TTYTTD.name())) {
			return 16;
		}
		else if	(name.equals(phoneTypes.WorkMobile.name())) {
			return 17;
		}
		else if	(name.equals(phoneTypes.WorkPager.name())) {
			return 18;
		}
		else if	(name.equals(phoneTypes.Assistant.name())) {
			return 19;
		}
		else if	(name.equals(phoneTypes.MMS.name())) {
			return 20;
		}
		else {
			return 0;
		}
	}
	
	public ArrayList<String> typeToArrayList() {
		ArrayList<String> list = new ArrayList<String>() ;
		list.add(phoneTypes.Home.name());
		list.add(phoneTypes.Mobile.name());
		list.add(phoneTypes.Work.name());
		list.add(phoneTypes.FaxHome.name());		
		list.add(phoneTypes.FaxWork.name());
		list.add(phoneTypes.Pager.name());
		list.add(phoneTypes.Other.name());
		list.add(phoneTypes.Callback.name());
		list.add(phoneTypes.Car.name());
		list.add(phoneTypes.CompanyMain.name());
		list.add(phoneTypes.ISDN.name());
		list.add(phoneTypes.Main.name());
		list.add(phoneTypes.OtherFax.name());
		list.add(phoneTypes.Radio.name());
		list.add(phoneTypes.Telex.name());
		list.add(phoneTypes.TTYTTD.name());
		list.add(phoneTypes.WorkMobile.name());
		list.add(phoneTypes.WorkPager.name());
		list.add(phoneTypes.Assistant.name());
		list.add(phoneTypes.MMS.name());
		return list;
	}
 }
