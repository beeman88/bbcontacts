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
	
	// 1 Home, 2 Work, 3 Other, 4 Mobile	
    enum emailTypes {
		HOME,
		WORK,
		OTHER,
		MOBILE
    }
	
	private ArrayList<String> order = new ArrayList<String>();
    private ArrayList<String> selection = new ArrayList<String>();	

	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);	

		setContentView(R.layout.settings);
		ctx = getApplicationContext();
		
		mDbHelper = new BBContactsDBAdapter(this);
        mDbHelper.open();
        // populate screen fields
        fillData();
        
        final ListView list1 = (ListView) findViewById(R.id.list_1);
        list1.setAdapter(new ArrayAdapter<String>(this,
                R.layout.settings_list, order));
        
        final ListView list2 = (ListView) findViewById(R.id.list_2);
        list2.setAdapter(new ArrayAdapter<String>(this,
                R.layout.settings_list, selection));
        
        list1.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// remove list item from order list and add to selection list
				moveListItem(arg1, list1, list2, order, selection);
			}
        });
        
        list2.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// remove list item from selection list and add to order list				
				moveListItem(arg1, list2, list1, selection, order);
			}
        });
		
        // define button listener to save settings
		final Button save = (Button) findViewById(R.id.save);		
		save.setOnClickListener(new OnClickListener() {		
		public void onClick(View view) {
			 getScreenData();

			 // if update return false create setting
	         if (!mDbHelper.updateSetting(1, username, password)) {
	        	 if (mDbHelper.createSetting(username, password) < 0) {
	        		 ErrorHandler.LogToastError(ctx, TAG, 
	        				 String.format(getString(R.string.settings_err_not_saved), username));
	        		 return;
	        	 }
	         }
	         
    		 ErrorHandler.LogToastError(ctx, TAG, 
    				 String.format(getString(R.string.settings_saved), username));    				 
	         return;
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
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void fillData() {
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
		
		setScreenData();
		
		
		return;
	}

	private void getScreenData() {
		// BillingBoss data
		EditText txtUsername = (EditText) findViewById(R.id.username);
		username = txtUsername.getText().toString();
		EditText txtPassword = (EditText) findViewById(R.id.password);
		password = txtPassword.getText().toString();
		
		ListView lvEmailOrder = (ListView) findViewById(R.id.list_1);
		//TODO convert ListView value to int and keep in array, 
		// then save to Preferences onClickListener 
	}	
	
	private void setScreenData() {
		
		// BillingBoss data
		EditText txtUsername = (EditText) findViewById(R.id.username);
		txtUsername.setText(username);
		EditText txtPassword = (EditText) findViewById(R.id.password);
		txtPassword.setText(password);
		
		// contact field selections
        Cursor eCur = mDbHelper.fetchPreferencesByField(BBContactsDBAdapter.FIELD_EMAIL);
        if (eCur == null) {
        	order.add(emailTypes.WORK.name());
        
	        selection.add(emailTypes.HOME.name());
	        selection.add(emailTypes.OTHER.name());
	        selection.add(emailTypes.MOBILE.name());
        }
        //TODO else read email selection from cursor
	}
	
	
}
