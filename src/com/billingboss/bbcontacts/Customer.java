package com.billingboss.bbcontacts;

import com.billingboss.bbcontacts.R;
import com.billingboss.bbcontacts.R.id;

public class Customer implements Comparable<Customer>{
	private String name;
	private Integer bb_id;
	private static String[] from = new String[] { CustomerRow.KEY_NAME };
	private static int[] to = new int[] { R.id.cust_name };	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getBBId() {
		return this.bb_id.toString();
	}
	
	public void setBBId(String id) {
		this.bb_id = new Integer(id);
	}
	
	public static String[] getFrom() {
		return from;
	}
	
	public static int[] getTo() {
		return to;
	}
	
	public Customer copy(){
		Customer copy = new Customer();
		copy.name = name;
		copy.bb_id = bb_id;
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ");
		sb.append(name);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (bb_id == null) {
			if (other.bb_id != null)
				return false;
		} else if (!bb_id.equals(other.bb_id))
			return false;
		return true;
	}

	public int compareTo(Customer another) {
		if (another == null) return 1;
		// sort descending, most recent first
		return another.name.compareTo(name);
	}
}

