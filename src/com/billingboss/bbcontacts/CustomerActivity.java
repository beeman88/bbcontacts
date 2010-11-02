package com.billingboss.bbcontacts;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;

import com.billingboss.bbcontacts.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CustomerActivity extends ListActivity {
	
	private BBContactsDBAdapter mDbHelper;	
	
	private static final String CUSTOMER_URL = "https://sdata.billingboss.com/sdata/billingboss/crmErp/-/tradingAccounts";
	private static final String TAG = "CustomerActivity";

	private List<Customer> customers;	
	private ArrayList<CustomerRow> customerList;	
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);	

		setContentView(R.layout.customer_list);        
		mDbHelper = new BBContactsDBAdapter(this);
        mDbHelper.open();
        fillData();        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		 MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.customer_menu, menu);
		    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.get_customers:
			getCustomers();
        	return true;
		case R.id.set_customers:
			setCustomers();
        	return true;
		case R.id.contacts:
			startActivity(new Intent(this, ContactActivity.class));
        	return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void getCustomers() {
		RestClient client = new RestClient(CUSTOMER_URL);
    	client.AddParam("select", "name");
    	//client.AddHeader("Authorization", "Basic YmVlbWFuOnRlc3Q=");

    	// read settings to get user name and password
    	String username = "";
    	String password = "";
    	Cursor c = mDbHelper.fetchSetting(1);
    	if (c.getCount() > 0) {
			username = c.getString(c.getColumnIndex(BBContactsDBAdapter.SETTINGS_USERNAME));            	
			password = c.getString(c.getColumnIndex(BBContactsDBAdapter.SETTINGS_PASSWORD));
    	}
    	else {
    		Log.d(TAG, "settings username and password are not created");
    	}
    	
    	// Add basic authorization to header
    	String auth = "Basic " + Base64.encodeBytes((username + ":" + password).getBytes());
    	client.AddHeader("Authorization", auth);
    	
    	try {
    	    client.Execute(RequestMethod.GET);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}

    	String response = client.getResponse();
    	if (response == null || response.trim().equals("")) {
        	Log.i(TAG, "response is null");
    		return;
    	}

    	Log.d(TAG, response);

    	this.loadFeed(response, ParserType.DOM);
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
			
			//String xml = writeXml();
			//Log.i(TAG, xml);
			
			customerList = new ArrayList<CustomerRow>(customers.size());
			
			for (Customer cust : customers){
				customerList.add(new CustomerRow(cust.getName(), cust.getBBId()));
			}
			
			createList();
			
		} catch (Throwable t){
			Log.e(TAG,t.getMessage(),t);
		}
	}

	private void createList() {
		ListAdapter adapter = new CustomerRowAdapter(this, 
				(List<? extends Map<String, String>>) customerList,
				R.layout.customers_row, 
				Customer.getFrom(), 
				Customer.getTo());     

		this.setListAdapter(adapter);        
		
		// add listener for list item click
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		  // When clicked, show a toast with the TextView text
		  Toast.makeText(getApplicationContext(), customerList.get(position).bb_id,		    		  
		      Toast.LENGTH_SHORT).show();
		}
		});
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
				/*				serializer.startTag("", "bb_id");
				serializer.text(cust.getBBId().toString());
				serializer.endTag("", "bb_id");*/
				serializer.endTag("", "customer");
			}
			serializer.endTag("", "customers");
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	private void setCustomers() {
		mDbHelper.deleteCustomers();
		for (Customer cust : customers){
			if (mDbHelper.createCustomer(cust.getName(), cust.getBBId()) < 0) {
				Log.e(TAG, cust.getName() + " could not be created");
			};
		}		
		return;
	}

	private void fillData() {
		// Get all of the Customers from the database and create the item list
		Cursor c = mDbHelper.fetchAllCustomers();
		startManagingCursor(c);

/*		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter Customers =
			new SimpleCursorAdapter(this, R.layout.customers_row, c, from, to);
		setListAdapter(Customers);
*/		
		customerList = new ArrayList<CustomerRow>();
		
		if (c.moveToFirst()) {
			do {
				String name = c.getString(c.getColumnIndex(BBContactsDBAdapter.CUSTOMER_NAME));            	
				String bb_id = c.getString(c.getColumnIndex(BBContactsDBAdapter.CUSTOMER_BB_ID));
				customerList.add(new CustomerRow(name, bb_id));
			} while (c.moveToNext());
		}
		c.close();
		
		createList();        
		
	}	
	
}
