package com.billingboss.bbcontacts;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlSerializer;
import android.app.Activity;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;


public class CustomerActivity extends ListActivity {
	
	private BBContactsDBAdapter mDbHelper;	
	
	private static final String CUSTOMER_URL = "https://sdata.billingboss.com/sdata/billingboss/crmErp/-/tradingAccounts";
	private static final String TAG = "CustomerActivity";

	private List<Customer> customers;	
	private ArrayList<CustomerRow> customerList;
	private Context ctx;
	private String company;
	
	private static final int CONTACT_PICKER_RESULT = 1001;	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Request for the progress bar to be shown in the title
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
		setContentView(R.layout.customer_list); 
		
		Button btnGetCust = (Button) findViewById(R.id.button_get_cust);
		btnGetCust.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //setProgressBarIndeterminateVisibility(true);
                getCustomers();
                //setProgressBarIndeterminateVisibility(false);                
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
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void getCustomers() {
		setProgressBarIndeterminateVisibility(true);
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
		setProgressBarIndeterminateVisibility(false);    	
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
			
        		company = customerList.get(position).name;
			
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
            	
        		ContentResolver cr = getContentResolver();            	
                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + 
                				  ContactsContract.Data.MIMETYPE + " = ? AND " + 
                				  ContactsContract.CommonDataKinds.Organization.TYPE + " = ? AND " + 
                				  ContactsContract.CommonDataKinds.Organization.LABEL + " = ?"; 
             	String[] orgWhereParams = new String[]{id, 
             		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
             		Integer.toString(ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM),
             		"BillingBoss"
             		}; 
             	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
                            null, orgWhere, orgWhereParams, null);

                Uri mOrgs = ContactsContract.Data.CONTENT_URI;
                ContentValues args = new ContentValues();
                args.put(ContactsContract.CommonDataKinds.Organization.COMPANY, company);        
                args.put(ContactsContract.CommonDataKinds.Organization.RAW_CONTACT_ID, id);
                args.put(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM);
                args.put(ContactsContract.CommonDataKinds.Organization.LABEL, "BillingBoss");
                args.put(ContactsContract.CommonDataKinds.Organization.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);                
                
             	// organization not found, insert 
             	if (orgCur == null || orgCur.getCount() == 0) {
             		try {
             			cr.insert(mOrgs, args);
             		}
             		catch (Exception e) {
             			ErrorHandler.LogToastError(ctx, TAG, e.getLocalizedMessage());
             		}
             	}
             	else	
             	{
	        		try {
	        			cr.update(mOrgs, args, orgWhere, orgWhereParams);
	        		}
	        		catch (Exception e) {
	        			ErrorHandler.LogToastError(ctx, TAG, e.getLocalizedMessage());
	        		}
             	}
                break;
            }

        } else {
            // gracefully handle failure
            Log.w(TAG, "Warning: activity result not ok");
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
