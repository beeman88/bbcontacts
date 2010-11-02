package com.billingboss.bbcontacts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SettingsActivity extends Activity {
	
	private static final String TAG = "SettingActivity";	
	
	private BBContactsDBAdapter mDbHelper;	

	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);	

		setContentView(R.layout.settings);
		mDbHelper = new BBContactsDBAdapter(this);		
		
		final Button save = (Button) findViewById(R.id.save);		
		save.setOnClickListener(new OnClickListener() {		
		public void onClick(View view) {
			 EditText txtUsername = (EditText) findViewById(R.id.username);
			 String username = txtUsername.getText().toString();
			 EditText txtPassword = (EditText) findViewById(R.id.password);
			 String password = txtPassword.getText().toString();

	         mDbHelper.open();
	         if (!mDbHelper.updateSetting(1, "en-US", username, password)) {
	        	 if (mDbHelper.createSetting("en-US", username, password) < 0) {
	        		 Log.e(TAG, username + " not created in settings");
	        	 }
	         }
	         
	         return;
			}
			});		
	}	
}
