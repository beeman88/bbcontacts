package com.billingboss.bbcontacts;
/*package com.billingboss.bbcontacts;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class CustomerList extends ListActivity {
	
	private static final String TAG = "CustomerList";
	
	private List<Customer> customers;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.customer_list);
        // loadFeed(ParserType.ANDROID_SAX);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, ParserType.ANDROID_SAX.ordinal(), 
				ParserType.ANDROID_SAX.ordinal(), R.string.android_sax);
		menu.add(Menu.NONE, ParserType.SAX.ordinal(), ParserType.SAX.ordinal(),
				R.string.sax);
		menu.add(Menu.NONE, ParserType.DOM.ordinal(), ParserType.DOM.ordinal(), 
				R.string.dom);
		menu.add(Menu.NONE, ParserType.XML_PULL.ordinal(), 
				ParserType.XML_PULL.ordinal(), R.string.pull);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		ParserType type = ParserType.values()[item.getItemId()];
		ArrayAdapter<String> adapter =
			(ArrayAdapter<String>) this.getListAdapter();
		if (adapter.getCount() > 0){
			adapter.clear();
		}
		this.loadFeed(feed, type);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent viewCustomer = new Intent(Intent.ACTION_VIEW, 
				Uri.parse(customers.get(position).getLink().toExternalForm()));
		this.startActivity(viewCustomer);
	}

	private void loadFeed(String feed, ParserType type){
    	try{
    		Log.i(TAG, "ParserType="+type.name());
	    	FeedParser parser = FeedParserFactory.getParser(type);
	    	long start = System.currentTimeMillis();
	    	parser.setFeed(feed);
	    	customers = parser.parse();
	    	long duration = System.currentTimeMillis() - start;
	    	Log.i(TAG, "Parser duration=" + duration);
	    	String xml = writeXml();
	    	Log.i(TAG, xml);
	    	List<String> names = new ArrayList<String>(customers.size());
	    	for (Customer cust : customers){
	    		names.add(cust.getName());
	    	}
	    	ArrayAdapter<String> adapter = 
	    		new ArrayAdapter<String>(this, R.layout.customers_row,names);
	    	this.setListAdapter(adapter);
    	} catch (Throwable t){
    		Log.e(TAG,t.getMessage(),t);
    	}
    }
    
	private String writeXml(){
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "customers");
			serializer.attribute("", "number", String.valueOf(customers.size()));
			for (Customer cust: customers){
				serializer.startTag("", "customer");
				serializer.startTag("", "name");
				serializer.text(cust.getName());
				serializer.endTag("", "name");
				serializer.startTag("", "bb_id");
				serializer.text(cust.getBBId().toString());
				serializer.endTag("", "bb_id");
				serializer.endTag("", "customer");
			}
			serializer.endTag("", "customers");
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}
*/