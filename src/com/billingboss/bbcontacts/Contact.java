package com.billingboss.bbcontacts;

public class Contact implements Comparable<Contact>{
	private Integer customerId;
	private String displayName;
	//private String firstName;
	//private String lastName;
	private String email;
	private String phone;
	
	public Integer getCustomerId() {
		return this.customerId;
	}
	
	public void setCustomerId(Integer id) {
		this.customerId = id;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName.trim();
	}
	
/*	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName.trim();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName.trim();
	}
*/
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email.trim();
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone.trim();
	}
	
	public Contact copy(){
		Contact copy = new Contact();
		copy.customerId = this.customerId;
		copy.displayName = this.displayName;
		//copy.firstName = this.firstName;
		//copy.lastName = this.lastName;
		copy.email = this.email;
		copy.phone = this.phone;
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.displayName);
/*		sb.append(firstName);
		sb.append(" ");
		sb.append(lastName);
*/		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.toString() == null) ? 0 : this.toString().hashCode());
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
		Contact other = (Contact) obj;

		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;		
		
/*		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;*/

		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;

		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		
		return true;
	}

	public int compareTo(Contact another) {
		if (another == null) return 1;
		// sort descending, most recent first
		return another.toString().compareTo(this.toString());
	}
}

