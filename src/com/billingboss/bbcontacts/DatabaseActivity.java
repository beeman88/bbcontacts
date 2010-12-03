package com.billingboss.bbcontacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DatabaseActivity extends ListActivity {
	
	private static final String TAG = "DatabaseActivity";	
	private BBContactsDBAdapter mDbHelper;
	private Context ctx;

	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.database_list);        

		ctx = getApplicationContext();
		
		mDbHelper = new BBContactsDBAdapter(this);
        mDbHelper.open();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		 MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.database_menu, menu);
		    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.customers:
			startActivity(new Intent(this, CustomerActivity.class));
        	return true;
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
        	return true;
		case R.id.menu_read_customers:
			getCustomers();
	    	return true;
	    	// TODO add this back in 
/*		case R.id.menu_read_contacts:
			getContacts();
		    return true;*/
		case R.id.menu_reset_database:
			resetDatabase();
			return true;
		
	}
		return super.onOptionsItemSelected(item);
	}
	
	private void getCustomers() {
		ArrayList<CustomerRow>customerList = new ArrayList<CustomerRow>();
		
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
		setCustomerList(customerList);        
		
	}
	
/*	private void getContacts() {
		ArrayList<ContactRow>contactList = new ArrayList<ContactRow>();
		
		// Get all of the Contacts from the database and create the item list
		Cursor c = mDbHelper.fetchAllContacts();
		
		if (c == null) {
			ErrorHandler.LogToastError(ctx, TAG,
					getString(R.string.contacts_err_not_created));
			return;
		}
		
		try {
			startManagingCursor(c);
			
			do {
				String contactId = c.getString(c.getColumnIndex(BBContactsDBAdapter.CONTACT_ID));				
				String customerId = c.getString(c.getColumnIndex(BBContactsDBAdapter.CONTACT_CUSTOMER_ID));            	
				contactList.add(new ContactRow(contactId, customerId));
			} while (c.moveToNext());
		}
		finally {
			c.close();
		}
		
		// use customerList to display on the screen
		setContactList(contactList);        
		
	}*/

	private void setCustomerList(ArrayList<CustomerRow> customerList) {
		
		ListAdapter adapter = new CustomerRowAdapter(this, 
				(List<? extends Map<String, String>>) customerList,
				R.layout.db_customer_row, 
				new String[] { CustomerRow.KEY_NAME, CustomerRow.KEY_BB_ID }, 
				new int[] { R.id.db_cust_name, R.id.db_bb_id });     

		this.setListAdapter(adapter);        
	}
	
/*	private void setContactList(ArrayList<ContactRow> contactList) {
		
		ListAdapter adapter = new ContactRowAdapter(this, 
				(List<? extends Map<String, String>>) contactList,
				R.layout.db_contact_row, 
				new String[] { ContactRow.KEY_CONTACT_ID, ContactRow.KEY_CUSTOMER_ID }, 
				new int[] { R.id.db_contact_id, R.id.db_customer_id });     

		this.setListAdapter(adapter);        
	}*/
	
	private void resetDatabase() {
		mDbHelper.deleteCustomers();
		mDbHelper.deleteContacts();
	}
	
	
}
