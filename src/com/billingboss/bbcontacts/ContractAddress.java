package com.billingboss.bbcontacts;

import java.util.ArrayList;

public abstract class ContractAddress {
	public abstract String getNameByIndex(int index);
	public abstract int getIndexByName(String name);
	public abstract ArrayList<String> typeToArrayList();
}
