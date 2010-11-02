package com.billingboss.bbcontacts;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

public abstract class BaseFeedParser implements FeedParser {

	// names of the XML tags
	static final String FEED = "http://www.w3.org/2005/Atom:feed";
	static final String ID = "id";
	static final  String TITLE = "title";
	static final  String UPDATED = "updated";
	static final  String LINK = "link";
	static final  String AUTHOR = "author";
	static final String CATEGORY = "category";
	static final String ENTRY = "entry";
	static final  String CONTENT = "content";
	static final  String PAYLOAD = "sdata:payload";
	static final String TRADING_ACCOUNT = "crmErp:tradingAccount";
	static final String NAME = "crmErp:name";
	static final  String START_INDEX = "opensearch:startIndex";
	static final String ITEMS_PER_PAGE = "opensearch:itemsPerPage";
	static final String TOTAL_RESULTS = "opensearch:totalResults";
	static final String KEY = "sdata:key";
	
	private String feed;

	protected BaseFeedParser(){
	}
	
	public void setFeed(String feed) {
		this.feed = feed;
	}
	
	public String getFeed() {
		return this.feed;
	}

	protected InputStream getInputStream() {
		return StringToStream(feed);
	}

    public static InputStream StringToStream(String text) {
        
        /*
         * Convert String to InputStream using ByteArrayInputStream 
         * class. This class constructor takes the string byte array 
         * which can be done by calling the getBytes() method.
         */
        try {
            InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
            return is;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
	
}