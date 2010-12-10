package com.billingboss.bbcontacts;

public class Website {

 	private String url;
 	private String type;
 	
 	public Website(String url, String type) {
 		this.url = url; 		
 		this.type = type;
 	}
 	
 	public String getUrl() {
 		return url;
 	}
 
 	public void setUrl(String url) {
 		this.url = url;
 	}
 
 	public Website(String url) {
 		this.url = url;
 	}
 	
 	public String getType() {
 		return type;
 	}
 
 	public void setType(String type) {
 		this.type = type;
 	}
 	
 }
