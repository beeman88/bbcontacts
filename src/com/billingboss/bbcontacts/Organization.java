package com.billingboss.bbcontacts;

public class Organization {
 	private String organization = "";
 	private String title = "";
 	private String type;
 	public String getOrganization() {
 		return organization;
 	}
 	public void setOrganization(String organization) {
 		this.organization = organization;
 	}
 	public String getTitle() {
 		return title;
 	}
 	public void setTitle(String title) {
 		this.title = title;
 	}
 	
 	public String getType() {
 		return type;
 	}
 
 	public void setType(String type) {
 		this.type = type;
 	}
 	
 	public Organization() {
 		
 	}
 	public Organization(String org, String title, String type) {
 		this.organization = org;
 		this.title = title;
 		this.type = type;
 	}
 }
