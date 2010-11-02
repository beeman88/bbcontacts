package com.billingboss.bbcontacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.Toast;

public class ContactActivity extends ListActivity {
	
	private static final String TAG = "ContactActivity";	
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);	
        
		setContentView(R.layout.contact_list);        
   
		ArrayList<ContactRow> contactList = new ArrayList<ContactRow>();

		Uri mContacts = ContactsContract.Contacts.CONTENT_URI;

		Cursor mCur = managedQuery(mContacts, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

		startManagingCursor(mCur); 
		Log.d(TAG, "cursor.getCount()=" + mCur.getCount());        

		if (mCur.moveToFirst()) {
			do {
				String id = mCur.getString(mCur.getColumnIndex(ContactsContract.Contacts._ID));            	
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
									new String[]{id}, null);
					if (pCur.moveToFirst()) {
						phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					} 
					pCur.close();

					//email
					Cursor emailCur = managedQuery( 
							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
							new String[]{id}, null); 
					if (emailCur.moveToFirst()) { 
						// This would allow you get several email addresses
						// if the email addresses were stored in an array
						email = emailCur.getString(
								emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					} 
					emailCur.close();

				}            	
				contactList.add(new ContactRow(name, phone, email));
			} while (mCur.moveToNext());
		}
		mCur.close();

		ListAdapter adapter = new ContactRowAdapter(this, 
				(List<? extends Map<String, String>>) contactList,
				R.layout.contacts_row, 
				new String[] {ContactRow.KEY_NAME, ContactRow.KEY_NUMBER, ContactRow.KEY_EMAIL }, 
				new int[] {R.id.name, R.id.number, R.id.email});     

		this.setListAdapter(adapter);        
		Toast.makeText(this, contactList.size() + "", Toast.LENGTH_LONG).show();
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
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}
