package com.billingboss.bbcontacts;
import java.util.List;


public interface FeedParser {
	List<Customer> parse();
	void setFeed(String feed);
}
