package com.billingboss.bbcontacts;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class CustomerActivity extends ListActivity {
	
	private BBContactsDBAdapter mDbHelper;	
	
	private static final String CUSTOMER_URL = "https://sdata.billingboss.com/sdata/billingboss/crmErp/-/tradingAccounts";
	private static final String TAG = "CustomerActivity";

	private ArrayList<Customer> customers;	
	private ArrayList<CustomerRow> customerList;
	private Context ctx;
	private String company;
	private Integer customer_id;
	private static RestClient client;
	
	private static final int CONTACT_PICKER_RESULT = 1001;	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Request for the progress bar to be shown in the title
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
		setContentView(R.layout.customer_list); 
		
		Button btnGetCust = (Button) findViewById(R.id.button_get_cust);
		btnGetCust.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                getCustomers();
            }
        });
		
		Button btnSetCust = (Button) findViewById(R.id.button_set_cust);
		btnSetCust.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setCustomers();
            }
        });	
		
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
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
        	return true;
		case R.id.contacts:
			startActivity(new Intent(this, ContactActivity.class));			
        	return true;        	
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void getCustomers() {
		RestClient client;
		customers = new ArrayList<Customer>();
		
		// use DOM to parse
		DomFeedParser parser = new DomFeedParser();
		
		int currentIndex = 1;
		
    	// customers are retrieved in batches
    	do {
    		// getRestClient creates rest client
    		client = getRestClient(currentIndex); 
    		
    		// check if username, password entered
    		if (client.getUsername().equals("") || 
   				client.getPassword().equals("")) {
	    		ErrorHandler.LogToastError(ctx, TAG, 
		    			getString(R.string.settings_err_not_created));
    			return;
    		}
    		
    		// make the request 
    		String response = client.next();
	    	if (response == null || response.trim().equals("")) {
	    		ErrorHandler.LogToastError(ctx, TAG, 
	    			getString(R.string.customer_err_null_response));
	    		return;
	    	}
	     	Log.d(TAG, response);
	     	
	    	// parse the xml and add customs in a list
			parser.setFeed(response);
	    	customers.addAll(parser.parse());

	    	// get the totals number of customers, number of customers retrieved in a request and 
	    	// the current index
	    	try {
	    		client.setTotalResults(Integer.parseInt(parser.getTotalResults().trim()));
	    		client.setItemsPerPage(Integer.parseInt(parser.getItemsPerPage().trim()));
	    		client.setStartIndex(Integer.parseInt(parser.getStartIndex().trim()));
	    	}
	    	catch (Exception e) {
	    	}
	    	
	    	currentIndex = currentIndex + client.getItemsPerPage();
	    	
	    // another request is made based on the total, index and items per page	
    	} while (client.hasNext());

		// adapt the customer list to the UI customer list rows
    	customerList = new ArrayList<CustomerRow>(customers.size());
		for (Customer cust : customers){
			customerList.add(new CustomerRow(cust.getName(), cust.getBBId()));
		}
    	
		// take the customers from CustomerList and display them
		setScreenList();
	}

	private RestClient getRestClient(int startIndex) {
		client = new RestClient(CUSTOMER_URL);
    	client.AddParam("select", "name");
    	client.AddParam("startIndex", Integer.toString(startIndex));    	
    	//client.AddHeader("Authorization", "Basic YmVlbWFuOnRlc3Q=");    	
    	client.AddHeader("Authorization", getBasicAuth());
		return client;
	}

	private String getBasicAuth() {
		// if username and password are null, retrieve from settings table
		if (client.getUsername().compareTo("") == 0 &&
			client.getPassword().compareTo("") == 0)
		{
			// read settings to get user name and password
	    	Cursor c = mDbHelper.fetchSetting(1);
	    	
			if (c == null) {
				ErrorHandler.LogToastError(ctx, TAG,
						getString(R.string.settings_err_not_created));
				return "";
			}    	
	    	
			try {
				client.setUsername(c.getString(c.getColumnIndex(BBContactsDBAdapter.SETTINGS_USERNAME)));            	
				client.setPassword(c.getString(c.getColumnIndex(BBContactsDBAdapter.SETTINGS_PASSWORD)));
			}
			finally {
				c.close();
			}
		}
    	// Add basic authorization to header
    	return Settings.getBasicAuth(client.getUsername(), client.getPassword());
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
			
			    //TODO using customerList.getPosition forced me to use a global list
			    // save company name and bb_id for adding in database
        		company = customerList.get(position).name;
        		// convert billing boss id from string to Integer
        		try {
        			customer_id = new Integer (customerList.get(position).bb_id);
        		}
        		catch (Exception e) {
        			ErrorHandler.LogToastError(ctx, TAG, e.getLocalizedMessage());
        		}
        		// bring up the Contact Picker
			    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
			            Contacts.CONTENT_URI);
			    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
		}
		});
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case CONTACT_PICKER_RESULT:
                // handle contact results
            	Uri result = data.getData();
            	// get the contact id from the Uri
            	String id = result.getLastPathSegment();
            	
            	Toast.makeText(ctx, id,		    		  
          		      Toast.LENGTH_SHORT).show();
            	
        		Long contact_id = convertIdToLong(id);
        		if (contact_id == null) {
        			return;
        		}
        		      
        		// insert into phone database contact, customer
            	insertContact(contact_id.longValue());

        		// TODO update company in contact with billingboss customer name            	
            	// updateContactCompany(id);
                break;
            }

        } else {
            // gracefully handle failure
            Log.w(TAG, getString(R.string.customers_warn_activity_result));
        }
    }

	private void updateContactCompany(String id) {
		// look for organization with BillingBoss as it's custom label
		ContentResolver cr = getContentResolver();            	
		String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + 
						  ContactsContract.Data.MIMETYPE + " = ? AND " + 
						  ContactsContract.CommonDataKinds.Organization.TYPE + " = ? AND " + 
						  ContactsContract.CommonDataKinds.Organization.LABEL + " = ?"; 
		String[] orgWhereParams = new String[]{id, 
			ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
			Integer.toString(ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM),
			getString(R.string.BillingBoss)
			}; 

		// assign organization record values
		Uri mOrgs = ContactsContract.Data.CONTENT_URI;
		ContentValues args = new ContentValues();
		args.put(ContactsContract.CommonDataKinds.Organization.COMPANY, company);        
		args.put(ContactsContract.CommonDataKinds.Organization.RAW_CONTACT_ID, id);
		args.put(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM);
		args.put(ContactsContract.CommonDataKinds.Organization.LABEL, getString(R.string.BillingBoss));
		args.put(ContactsContract.CommonDataKinds.Organization.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);                
		
		Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
	            null, orgWhere, orgWhereParams, null);
		
		// organization not found, insert 
		if (orgCur == null || orgCur.getCount() == 0) {
			try {
				cr.insert(mOrgs, args);
			}
			catch (Exception e) {
				ErrorHandler.LogToastError(ctx, TAG, e.getLocalizedMessage());
			}
		}
		else // update	
		{
			try {
				cr.update(mOrgs, args, orgWhere, orgWhereParams);
			}
			catch (Exception e) {
				ErrorHandler.LogToastError(ctx, TAG, e.getLocalizedMessage());
			}
			finally {
				orgCur.close();				
			}
		}
	}

	private Long convertIdToLong(String id) {
		
		try {
			return Long.parseLong(id.trim());
		} catch (NumberFormatException nfe) {
			ErrorHandler.LogToastError(ctx, TAG, "NumberFormatException: " + nfe.getLocalizedMessage());
			return null;
		}            	
	}

	private void insertContact(long contact_id) {
		//TODO check if the customer has been saved in the phone db
		
		// insert into phone database
		Cursor c = mDbHelper.fetchContactByContactId(contact_id);
		if (c == null) {
			mDbHelper.createContact(new Integer((int) contact_id), customer_id);
			return;
		}

		try {
			long row_id = c.getLong(c.getColumnIndex(BBContactsDBAdapter.CONTACT_ROWID));
			mDbHelper.updateContact(row_id, contact_id, customer_id);
		}
		finally {
			c.close();
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

	// reads customers from the phone db on initial call and if screen is rotated
	private void fillData() {
		
		// customerList already has entries, don't retrieve again from databasae 
		if (customerList != null) {
			return;
		}
		
		customerList = new ArrayList<CustomerRow>();
		
		// Get all of the Customers from the database and create the item list
		Cursor c = mDbHelper.fetchAllCustomers();
		
		if (c == null) {
			ErrorHandler.LogToastError(ctx, TAG,
					getString(R.string.customers_err_not_created));
			return;
		}
		
		try {
			startManagingCursor(c);
			
			do {
				String name = c.getString(c.getColumnIndex(BBContactsDBAdapter.CUSTOMER_NAME));            	
				String bb_id = c.getString(c.getColumnIndex(BBContactsDBAdapter.CUSTOMER_BB_ID));
				customerList.add(new CustomerRow(name, bb_id));
			} while (c.moveToNext());
		}
		finally {
			c.close();
		}
		
		// use customerList to display on the screen
		setScreenList();        
	}	
	
}
