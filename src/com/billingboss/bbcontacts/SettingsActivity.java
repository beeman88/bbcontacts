package com.billingboss.bbcontacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	
	private static final String TAG = "SettingActivity";	
	private BBContactsDBAdapter mDbHelper;
	private String username;
	private String password; 
	private Context ctx;

	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);	

		setContentView(R.layout.settings);
		ctx = getApplicationContext();
		
		
		mDbHelper = new BBContactsDBAdapter(this);
        mDbHelper.open();
        // populate screen fields
        fillData();
		
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
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void fillData() {
		// Get settings cursor for the first row
		Cursor c = mDbHelper.fetchSetting(1);;

		
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
		EditText txtUsername = (EditText) findViewById(R.id.username);
		username = txtUsername.getText().toString();
		EditText txtPassword = (EditText) findViewById(R.id.password);
		password = txtPassword.getText().toString();
	}	
	
	private void setScreenData() {
		EditText txtUsername = (EditText) findViewById(R.id.username);
		txtUsername.setText(username);
		EditText txtPassword = (EditText) findViewById(R.id.password);
		txtPassword.setText(password);
	}
	
	
}
