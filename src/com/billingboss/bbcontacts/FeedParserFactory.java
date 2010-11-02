package com.billingboss.bbcontacts;
public abstract class FeedParserFactory {
	static String feedUrl = "http://www.androidster.com/android_news.rss";
	
	public static FeedParser getParser(){
		return getParser(ParserType.ANDROID_SAX);
	}
	
	public static FeedParser getParser(ParserType type){
		switch (type){
/*			case SAX:
				return new SaxFeedParser(feedUrl); */
			case DOM:
				return new DomFeedParser();
/*			case ANDROID_SAX:
				return new AndroidSaxFeedParser();
			case XML_PULL:
				return new XmlPullFeedParser(feedUrl);*/
			default: return null;
		}
	}
}
