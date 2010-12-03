package com.billingboss.bbcontacts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ContactActivity extends ListActivity {
	
	private static final String TAG = "ContactActivity";
	private BBContactsDBAdapter mDbHelper;
	private ArrayList<ContactRow> contactList;
	private Context ctx;
	private static final String CSV_FILENAME = "contacts.csv";	
	
	private static final String CSV_HDG_NAME = "customer_name";
	private static final String CSV_HDG_ADD1 = "customer_address1";
	private static final String CSV_HDG_ADD2 = "customer_address2";
	private static final String CSV_HDG_CITY = "customer_city";
	private static final String CSV_HDG_PROV = "customer_province_state";	
	private static final String CSV_HDG_POST = "customer_postalcode_zip";
	private static final String CSV_HDG_COUNTRY = "customer_country";
	private static final String CSV_HDG_WEB = "customer_website";
	private static final String CSV_HDG_PHONE = "customer_phone";
	private static final String CSV_HDG_LANG = "customer_language";
	private static final String CSV_HDG_CONTACT_FIRST = "contact_first";
	private static final String CSV_HDG_CONTACT_LAST = "contact_last";
	private static final String CSV_HDG_CONTACT_EMAIL = "contact_email";	
	private static final String CSV_HDG_CONTACT_PHONE = "contact_phone";	
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);	
        
		setContentView(R.layout.contact_list);
		
		Button btnGetCust = (Button) findViewById(R.id.send_file_to_bb);
		btnGetCust.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                saveContacts();
            }
        });
		
		ctx = getApplicationContext();		
		
		mDbHelper = new BBContactsDBAdapter(this);
        mDbHelper.open();
        fillData();
	}
	
	// reads customers from the phone db on initial call and if screen is rotated
	private void fillData() {

		// contactList already has entries, don't retrieve again from databasae 
		if (contactList != null) {
			return;
		}
		
		// read through the phone contacts
		contactList = new ArrayList<ContactRow>();		

		// Get all of the Contacts from the database and create the item list
		Cursor c = mDbHelper.fetchAllContacts();
		
		if (c == null) {
			ErrorHandler.LogToastError(ctx, TAG,
					getString(R.string.customers_err_not_created));
			return;
		}
		
		try {
			startManagingCursor(c);
			
			do {
				String contactId = c.getString(c.getColumnIndex(BBContactsDBAdapter.CONTACT_ID));            	
				String customerId = c.getString(c.getColumnIndex(BBContactsDBAdapter.CONTACT_CUSTOMER_ID));
				
				// for each db contact read the google contact for first name, last name, email, phone				
				
				Uri mContacts = ContactsContract.Contacts.CONTENT_URI;

				Cursor mCur = managedQuery(mContacts, null, "_ID = ?", new String[] {contactId}, null);
				startManagingCursor(mCur); 

				if (mCur.moveToFirst()) {
						Log.d(TAG, String.format("found contact for %s", contactId));           	
						String name = mCur.getString(mCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						String phone = "";
						String email = "";
						// phone
						if (Integer.parseInt(mCur.getString(
								mCur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
							Cursor pCur = managedQuery(
									ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
									null, 
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
											new String[]{contactId}, null);
							if (pCur.moveToFirst()) {
								phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							} 
							pCur.close();

							//email
							Cursor emailCur = managedQuery( 
									ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
									null,
									ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
									new String[]{contactId}, null); 
							if (emailCur.moveToFirst()) { 
								// This would allow you get several email addresses
								// if the email addresses were stored in an array
								email = emailCur.getString(
										emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
							} 
							emailCur.close();

						}            	
						contactList.add(new ContactRow(contactId, customerId, name, phone, email));
				}
				mCur.close();

			} while (c.moveToNext());
		}
		finally {
			c.close();
		}
		
		// use contactList to display on the screen
		setScreenList();        
	}
	
	private void setScreenList() {
		ListAdapter adapter = new ContactRowAdapter(this, 
				(List<? extends Map<String, Contact>>) contactList,
				R.layout.contacts_row, 
				new String[] {ContactRow.KEY_CONTACT_ID, 
							  ContactRow.KEY_CUSTOMER_ID, 
							  ContactRow.KEY_DISPLAY_NAME,
							  ContactRow.KEY_EMAIL,
							  ContactRow.KEY_PHONE}, 
				new int[] {R.id.contact_id, R.id.customer_id, R.id.display_name, R.id.email, R.id.phone});     

		this.setListAdapter(adapter);        
		Toast.makeText(this, contactList.size() + "", Toast.LENGTH_LONG).show();
	}

	private void saveContacts() {
		 	try
		 	{
		 		File root = Environment.getExternalStorageDirectory();
		 		File gpxfile = new File(root, CSV_FILENAME);
		 		FileWriter writer = new FileWriter(gpxfile);

		 	    writer.append(CSV_HDG_NAME);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_ADD1);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_ADD2);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_CITY);
		 	    writer.append(',');		 	    
		 	    writer.append(CSV_HDG_PROV);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_POST);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_COUNTRY);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_WEB);
		 	    writer.append(',');	
		 	    writer.append(CSV_HDG_PHONE);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_LANG);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_CONTACT_FIRST);
		 	    writer.append(',');	
		 	    writer.append(CSV_HDG_CONTACT_LAST);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_CONTACT_EMAIL);
		 	    writer.append(',');
		 	    writer.append(CSV_HDG_CONTACT_PHONE);
		 	    writer.append('\n');

		 	    // go through the list selecting checked items and write to file

		 	    // get the checked rows in the listview 
	 	    	ListView list = (ListView) findViewById(android.R.id.list);
	 	    	//SparseBooleanArray positions = list.getCheckedItemPositions();
	 	    	
	 	    	for (int i = 0; i < contactList.size() - 1; i++) {
	 	    		// skip not checked rows
	 	    		View row = (View) list.getChildAt(i);
	 	    		CheckedTextView chkBox = (CheckedTextView) row.findViewById(R.id.row_checkbox);
	 	    		
	 	    		if (!chkBox.isChecked()) {
	 	    			continue;
	 	    		}
	 	    		
	 	    		ContactRow contactRow = contactList.get(i);
	 	    		
			 	    writer.append("CSV_HDG_NAME");
			 	    writer.append(',');
			 	    writer.append("CSV_HDG_ADD1");
			 	    writer.append(',');
			 	    writer.append("CSV_HDG_ADD2");
			 	    writer.append(',');
			 	    writer.append("CSV_HDG_CITY");
			 	    writer.append(',');		 	    
			 	    writer.append("CSV_HDG_PROV");
			 	    writer.append(',');
			 	    writer.append("CSV_HDG_POST");
			 	    writer.append(',');
			 	    writer.append("CSV_HDG_COUNTRY");
			 	    writer.append(',');
			 	    writer.append("CSV_HDG_WEB");
			 	    writer.append(',');	
			 	    writer.append("CSV_HDG_PHONE");
			 	    writer.append(',');
			 	    writer.append("CSV_HDG_LANG");
			 	    writer.append(',');
			 	    writer.append(contactRow.first_name);
			 	    writer.append(',');	
			 	    writer.append(contactRow.last_name);
			 	    writer.append(',');
			 	    writer.append(contactRow.email);
			 	    writer.append(',');
			 	    writer.append(contactRow.phone);
			 	    writer.append('\n');
	 	    	}

		 	    writer.flush();
		 	    writer.close();
		 	}
		 	catch(IOException e)
		 	{
		 	     e.printStackTrace();
		 	}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		 MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.contact_menu, menu);
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
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}





/*public ArrayList<Address> getContactAddresses(String id) {
		ArrayList<Address> addrList = new ArrayList<Address>();
		
		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
		String[] whereParameters = new String[]{id, 
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
		
		Cursor addrCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
		while(addrCur.moveToNext()) {
			String poBox = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
			String street = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
			String city = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
			String state = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
			String postalCode = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
			String country = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
			String type = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
			Address a = new Address(poBox, street, city, state, postalCode, country, type);
			addrList.add(a);
		} 
		addrCur.close();
		return(addrList);
	}
*/