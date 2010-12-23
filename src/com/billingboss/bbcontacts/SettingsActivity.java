package com.billingboss.bbcontacts;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private static final String TAG = "SettingActivity";	
	private BBContactsDBAdapter mDbHelper;
	private String username;
	private String password; 
	private Context ctx;
	private static OnSettingsUpdatedListener onSettingsUpdatedListener = null;

	private static ArrayList<String> emailOrder;
	private static ArrayList<String> emailSelection;	
	private static ArrayList<String> addressOrder;
	private static ArrayList<String> addressSelection;	
	
	private enum ContactAddress {
		Email,
		Address
	}

	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);	

		setContentView(R.layout.settings);
		ctx = getApplicationContext();
		
		// define button listener to save settings
		Button save = (Button) findViewById(R.id.save);		
		save.setOnClickListener(new OnClickListener() {		
			public void onClick(View view) {
				// all setting update, pop up message
				if (setBBCredentials() &&
					setSelections(emailOrder, 
							BBContactsDBAdapter.FIELD_EMAIL,
							ContactAddress.Email)) {
						ErrorHandler.LogToastError(ctx, TAG, 
								getString(R.string.settings_saved));    				 
					}
					onSaveClicked();
				}
		});

		mDbHelper = new BBContactsDBAdapter(this);
		mDbHelper.open();
		// populate screen fields
		fillData();
		
		setUIBBCredentials();
		// set UI Email lists
		setUISelections(R.id.list_1,
						R.id.list_2,
						emailOrder,
						emailSelection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.customers:
			startActivity(new Intent(this, CustomerActivity.class));
			return true;
		case R.id.database:
			startActivity(new Intent(this, DatabaseActivity.class));
			return true;
		case R.id.contacts:
			startActivity(new Intent(this, ContactActivity.class));			
        	return true;        	
		}

		return super.onOptionsItemSelected(item);
	}
		
	// Allows the user to set an Listener and react to the event
	static public void setOnSettingsUpdatedListener(OnSettingsUpdatedListener listener) {
		onSettingsUpdatedListener = listener;
	}
	
	// This function is called after the check was complete
	private void onSaveClicked(){
		// Check if the Listener was set, otherwise we'll get an Exception when we try to call it
		if(onSettingsUpdatedListener!=null) {
			// Only trigger the event
				onSettingsUpdatedListener.onSettingsUpdated();
			}
		}
		
	private void fillData() {
		
		if (username == null && password == null) {
			getBBCredentials();
		}
		
		// arraylists defined, called because of rotate
		if (emailOrder == null && emailSelection == null) {
			emailOrder = new ArrayList<String>();
			emailSelection = new ArrayList<String>();
			
			// populate email listviews
			getSelections(emailOrder, 
					emailSelection, 
					Email.typeToArrayList(), 
					BBContactsDBAdapter.FIELD_EMAIL,
					Email.emailTypes.Work.name(),
					ContactAddress.Email);
		}
		
/*		// arraylists defined, called because of rotate
		if (addressOrder == null && addressSelection == null) {
			addressOrder = new ArrayList<String>();
			addressSelection = new ArrayList<String>();
			getAddressSelections();
		}*/
		
		return;
	}

	private void resetSelectionList(ArrayList<String> selections, ArrayList<String> types) {
		selections.clear();
		for (String type : types) {
			selections.add(type);
		}
	}

	private boolean setSelections(ArrayList<String> order, 	// backing arraylist for left side list
			int preferenceField, 							// what kind of field, email, address, ..
			ContactAddress className						// class
		) {
		// remove current selections
		mDbHelper.deletePreferencesByField(preferenceField);
		// create new preferences
		for (int i = 0; i < order.size(); i++) {
			// use the emailOrder array list value to get the index 
			if (mDbHelper.createPreference(
					preferenceField,
					getIndexByName(className, order.get(i)),
					i) < 0) {
				ErrorHandler.LogToastError(ctx, TAG, 
						String.format(getString(R.string.settings_err_email_not_saved), className));
				return false;
			}
		}
		return true;
	}	
	
	// generalized method to populate list from cursor for address, email, phone, website, org type
	private void getSelections(ArrayList<String> order, // backing arraylist for left side list 
			ArrayList<String> selection,                // backing arraylist for right side list
			ArrayList<String> list,						// arraylist of all choices
			int preferenceField,						// what kind of field, email, address, ..
			String defaultType,							// initial type if cursor empty
			ContactAddress className					// class 
			) {
		// reset the arraylists backing the listview
		// order cleared, selection has all choices
		order.clear();
		resetSelectionList(selection, list);

		// contact field selections, get cursor of types ordered by sequence
		Cursor cur = mDbHelper.fetchPreferencesByField(preferenceField);

		if (cur == null) {
			// add default of work to emailOrder and remove from emailSelection
			order.add(defaultType);
			selection.remove(defaultType);
		}
		else {
			try {
				startManagingCursor(cur); 
				if (cur.moveToFirst()) {
					do {
						int inx = cur.getInt(cur.getColumnIndex(BBContactsDBAdapter.PREFERENCES_TYPE));
						String typeName = getNameByIndex(className, inx);
						order.add(typeName);
						selection.remove(typeName);
					} while (cur.moveToNext());
				}
			}
			finally {
				cur.close();
			}
		}
	}
	
	private String getNameByIndex(ContactAddress className, int inx) {
		switch (className) {
		case Email: 
			return Email.getNameByIndex(inx);
		case Address: 
			return Address.getNameByIndex(inx);
		default:
			return "";
		}
	}
	
	private int getIndexByName(ContactAddress className, String name) {
		switch (className) {
		case Email: 
			return Email.getIndexByName(name);
		case Address: 
			return Address.getIndexByName(name);
		default:
			return 0;
		}
	}

	private boolean setBBCredentials() {
		// BillingBoss data
		EditText txtUsername = (EditText) findViewById(R.id.username);
		username = txtUsername.getText().toString();
		EditText txtPassword = (EditText) findViewById(R.id.password);
		password = txtPassword.getText().toString();

		// if update return false create setting
		if (!mDbHelper.updateSetting(1, username, password)) {
			if (mDbHelper.createSetting(username, password) < 0) {
				ErrorHandler.LogToastError(ctx, TAG, 
						getString(R.string.settings_err_not_saved));
				return false;
			}
		}

		return true;
	}	

	private void getBBCredentials() {
		// Get settings cursor for the first row
		Cursor c = mDbHelper.fetchSetting(1);

		if (c == null) {
			ErrorHandler.LogToastError(ctx, TAG,
					getString(R.string.settings_err_not_created));
			return;
		}

		try {
			// if cursor has rows, already moved to first
			startManagingCursor(c);
			username = c.getString(c.getColumnIndex(BBContactsDBAdapter.SETTINGS_USERNAME));            	
			password = c.getString(c.getColumnIndex(BBContactsDBAdapter.SETTINGS_PASSWORD));
		}
		finally {
			c.close();
		}
	}

	private void setUIBBCredentials() {
		// BillingBoss data
		EditText txtUsername = (EditText) findViewById(R.id.username);
		txtUsername.setText(username);
		EditText txtPassword = (EditText) findViewById(R.id.password);
		txtPassword.setText(password);
	}
	
	private void setUISelections(int leftId, 
								 int rightId,
								 final ArrayList<String> order,
								 final ArrayList<String> selection) {
		final ListView list1 = (ListView) findViewById(leftId);
		list1.setAdapter(new ArrayAdapter<String>(this,
				R.layout.settings_list, order));

		final ListView list2 = (ListView) findViewById(rightId);
		list2.setAdapter(new ArrayAdapter<String>(this,
				R.layout.settings_list, selection));

		list1.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// remove list item from emailOrder list and add to emailSelection list
				moveListItem(arg1, list1, list2, order, selection);
			}
		});

		list2.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// remove list item from emailSelection list and add to emailOrder list				
				moveListItem(arg1, list2, list1, selection, order);
			}
		});
	}	

	private void moveListItem (View arg1, 
			ListView lvFrom, 
			ListView lvTo, 
			ArrayList<String> aRemove, 
			ArrayList<String> aAdd) {
		TextView textview = (TextView) arg1;

		aRemove.remove(textview.getText());
		lvFrom.setAdapter(new ArrayAdapter<String>(ctx,
				R.layout.settings_list, aRemove));

		aAdd.add(textview.getText().toString());
		lvTo.setAdapter(new ArrayAdapter<String>(ctx,
				R.layout.settings_list, aAdd));
	}
}
