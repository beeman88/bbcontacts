package com.billingboss.bbcontacts;

public class Settings implements Comparable<Settings>{
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Settings copy(){
		Settings copy = new Settings();
		copy.username = username;
		copy.password = password;
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Username: ");
		sb.append(username);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		Settings other = (Settings) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	public int compareTo(Settings another) {
		if (another == null) return 1;
		// sort descending, most recent first
		return another.username.compareTo(username);
	}

	// class method 
	public static String getBasicAuth(String username, String password) {
		// Add basic authorization to header
		return "Basic " + Base64.encodeBytes((username + ":" + password).getBytes());
	}	
}

