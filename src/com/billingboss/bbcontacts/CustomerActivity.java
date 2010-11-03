package com.billingboss.bbcontacts;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;

import com.billingboss.bbcontacts.R;

import android.app.ListActivity;
import android.content.Context;
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
	private Context ctx;	
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);	

		setContentView(R.layout.customer_list); 
		ctx = getApplicationContext();
		
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
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
        	return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void getCustomers() {
		RestClient client = new RestClient(CUSTOMER_URL);
    	client.AddParam("select", "name");
    	//client.AddHeader("Authorization", "Basic YmVlbWFuOnRlc3Q=");    	
    	client.AddHeader("Authorization", getBasicAuth());
    	
    	try {
    	    client.Execute(RequestMethod.GET);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}

    	String response = client.getResponse();
    	if (response == null || response.trim().equals("")) {
    		ErrorHandler.LogToastError(ctx, TAG, 
    			getString(R.string.customer_err_null_response));
    		return;
    	}

    	Log.d(TAG, response);

    	this.loadFeed(response, ParserType.DOM);
	}

	private String getBasicAuth() {
		// read settings to get user name and password
    	Cursor c = mDbHelper.fetchSetting(1);
    	
		if (c == null) {
			ErrorHandler.LogToastError(ctx, TAG,
					getString(R.string.settings_err_not_created));
			return "";
		}    	
    	
		String username = c.getString(c.getColumnIndex(BBContactsDBAdapter.SETTINGS_USERNAME));            	
		String password = c.getString(c.getColumnIndex(BBContactsDBAdapter.SETTINGS_PASSWORD));
    	
    	// Add basic authorization to header
    	return "Basic " + Base64.encodeBytes((username + ":" + password).getBytes());
	}
	
	private void loadFeed(String feed, ParserType type){
		try{
			FeedParser parser = FeedParserFactory.getParser(type);
			parser.setFeed(feed);
			customers = parser.parse();
		
			customerList = new ArrayList<CustomerRow>(customers.size());
			
			for (Customer cust : customers){
				customerList.add(new CustomerRow(cust.getName(), cust.getBBId()));
			}
			
			setScreenList();
			
		} catch (Throwable t){
			Log.e(TAG,t.getMessage(),t);
		}
	}

	private void setScreenList() {
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
		  Toast.makeText(ctx, customerList.get(position).bb_id,		    		  
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
	    		ErrorHandler.LogToastError(ctx, TAG, 
       				 String.format(getString(R.string.customer_err_not_saved), cust.getName()));	    				
			};
		}		
		return;
	}

	private void fillData() {
		// Get all of the Customers from the database and create the item list
		Cursor c = mDbHelper.fetchAllCustomers();
		
		if (c == null) {
			ErrorHandler.LogToastError(ctx, TAG,
					getString(R.string.customers_err_not_created));
			return;
		}
		
		startManagingCursor(c);
		customerList = new ArrayList<CustomerRow>();
		
		do {
			String name = c.getString(c.getColumnIndex(BBContactsDBAdapter.CUSTOMER_NAME));            	
			String bb_id = c.getString(c.getColumnIndex(BBContactsDBAdapter.CUSTOMER_BB_ID));
			customerList.add(new CustomerRow(name, bb_id));
		} while (c.moveToNext());
		c.close();
		
		setScreenList();        
		
	}	
	
}
