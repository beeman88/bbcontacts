package com.billingboss.bbcontacts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ContactActivity extends ListActivity implements OnSettingsUpdatedListener{

	private static final String TAG = "ContactActivity";
	private BBContactsDBAdapter mDbHelper;
	private static ArrayList<ContactRow> contactList;
	private static ArrayList<Integer> emailTypes;
	private static ArrayList<Integer> addressTypes;	
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
	private static final String CSV_HDG_FAX = "customer_fax";
	private static final String CSV_HDG_CONTACT_FIRST = "contact_first_name";
	private static final String CSV_HDG_CONTACT_LAST = "contact_last_name";
	private static final String CSV_HDG_CONTACT_EMAIL = "contact_email";	
	private static final String CSV_HDG_CONTACT_PHONE = "contact_phone";

	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);	

		setContentView(R.layout.contact_list);

		Button btnGetCust = (Button) findViewById(R.id.send_file_to_bb);
		btnGetCust.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				saveContacts();
				//sendContacts();
			}
		});

		ctx = getApplicationContext();		

		mDbHelper = new BBContactsDBAdapter(this);
		mDbHelper.open();
		
		SettingsActivity.setOnSettingsUpdatedListener(this);
		
		fillData();
		// use contactList to display on the screen
		setScreenList();        
	}

	// reads customers from the phone db on initial call and if screen is rotated
	private void fillData() {

		// contactList already has entries, don't retrieve again from databasae 
		if (contactList != null) {
			return;
		}

		// read through the phone contacts
		contactList = new ArrayList<ContactRow>();		

		// for each db contact read the google contact for first name, last name, email, phone				

		Uri mContacts = ContactsContract.Contacts.CONTENT_URI;

		Cursor mCur = managedQuery(mContacts, null, null, null, null);
		startManagingCursor(mCur); 

		while (mCur.moveToNext()) {
			String id = mCur.getString(mCur.getColumnIndex(ContactsContract.Contacts._ID));
			String name = mCur.getString(mCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String phoneContact = "";
			String phoneCompany = "";
			String email = "";
			String org = "";
			String website = "";
			
			// phone
			if (Integer.parseInt(mCur.getString(
					mCur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				phoneContact = getPhone(id, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
				phoneCompany = getPhone(id, ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN);
			}
			
			// email
			email = getEmail(id);
			
			// address ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
			Address address = getAddress(id);
			
			// organization
			org = getOrg(id, ContactsContract.CommonDataKinds.Organization.TYPE_WORK);
			
			// website
			website = getWebsite(id, ContactsContract.CommonDataKinds.Website.TYPE_WORK);
			
			// add contact row to list
			contactList.add(new ContactRow(id, org, name, address, website, phoneCompany, email, phoneContact));
		}
		mCur.close();
	}

	private void setScreenList() {
		ListAdapter adapter = new ContactRowAdapter(this, 
				(List<? extends Map<String, Contact>>) contactList,
				R.layout.contacts_row, 
				new String[] {ContactRow.KEY_ORGANIZATION, 
			ContactRow.KEY_DISPLAY_NAME,
			ContactRow.KEY_EMAIL
			}, 
			new int[] {R.id.org, R.id.display_name, R.id.email});     

		this.setListAdapter(adapter);        
		//Toast.makeText(this, contactList.size() + "", Toast.LENGTH_LONG).show();
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
			writer.append(CSV_HDG_FAX);
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

			for (int i = 0; i < contactList.size(); i++) {
				// skip not checked rows
				View row = (View) list.getChildAt(i);
				CheckedTextView chkBox = (CheckedTextView) row.findViewById(R.id.row_checkbox);

				if (!chkBox.isChecked()) {
					continue;
				}

				ContactRow contactRow = contactList.get(i);
				writer.append(contactRow.organization.trim());
				writer.append(',');
				writer.append(contactRow.address.getStreet().trim());
				writer.append(',');
				writer.append(contactRow.address.getPoBox().trim());
				writer.append(',');
				writer.append(contactRow.address.getCity().trim());
				writer.append(',');		 	    
				writer.append(contactRow.address.getState().trim());
				writer.append(',');
				writer.append(contactRow.address.getPostalCode().trim());
				writer.append(',');
				writer.append(contactRow.address.getCountry().trim());
				writer.append(',');
				writer.append(contactRow.website.trim());
				writer.append(',');	
				writer.append(contactRow.company_phone.trim());
				writer.append(',');
				// TODO have a language default in settings
				writer.append(Locale.getDefault().getLanguage().trim());
				writer.append(',');
				writer.append("(604) 207-FAXX");
				writer.append(',');
				writer.append(contactRow.first_name.trim());
 				writer.append(',');	
				writer.append(contactRow.last_name.trim());
				writer.append(',');
				writer.append(contactRow.email.trim());
				writer.append(',');
				writer.append(contactRow.contact_phone.trim());
				writer.append('\n');
			}

			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
	
	/*private void sendContacts() {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

		File root = Environment.getExternalStorageDirectory();
		
		String pathToOurFile = root.getAbsolutePath() + '/' + CSV_FILENAME;
		String urlServer = "www.billingboss.com/customers/import";
		String lineEnd = "\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;

		try
		{
		FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

		URL url = new URL(urlServer);
		connection = (HttpURLConnection) url.openConnection();

		// Allow Inputs & Outputs
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);

		// Enable POST method
		connection.setRequestMethod("POST");

		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

		outputStream = new DataOutputStream( connection.getOutputStream() );
		outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
		outputStream.writeBytes(lineEnd);

		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		buffer = new byte[bufferSize];

		// Read file
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		while (bytesRead > 0)
		{
		outputStream.write(buffer, 0, bufferSize);
		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}

		outputStream.writeBytes(lineEnd);
		outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		// Responses from the server (code and message)
		int serverResponseCode = connection.getResponseCode();
		String serverResponseMessage = connection.getResponseMessage();

		fileInputStream.close();
		outputStream.flush();
		outputStream.close();
		}
		catch (Exception ex)
		{
		//Exception handling
		}		
	}*/
	
	private String getPhone(String id, int type) {
		ArrayList<Phone> phones = getPhones(id, type);
		
		// if no phones return empty
		if (phones.size() == 0) {
			return "";
		}
		
		return phones.get(0).getNumber();
	}
	
	private ArrayList<Phone> getPhones(String id, int type) {
		ArrayList<Phone> phones = new ArrayList<Phone>();
		
		// TODO this cursor will only grab work phones
		// settings needs to specify priority sequence of phones to use
		Cursor pCur = managedQuery(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
				null, 
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " + 
				ContactsContract.CommonDataKinds.Phone.TYPE + " = ?", 
				new String[]{id, Integer.toString(type)}, 
				null);
		
		if (pCur.moveToFirst()) {
			do {
				phones.add(new Phone(
	 					pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
	 					pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))));
			} while (pCur.moveToNext());
		}
		
		pCur.close();
		
		return phones;
	}
	
	private String getEmail(String id) {
		
		if (emailTypes == null) {
			emailTypes = getTypes(BBContactsDBAdapter.FIELD_EMAIL);
			if (emailTypes == null) {
				return "";
			}
		}
		
		ArrayList<Email> emails = getEmails(id, emailTypes);
		
		// if no phones return empty
		if (emails.size() == 0) {
			return "";
		}
		
		return emails.get(0).getAddress();		
	}

	private ArrayList<Integer> getTypes(int fieldType) {
		// contact field selections, get cursor of email types ordered by sequence
		Cursor eCur = mDbHelper.fetchPreferencesByField(fieldType);
		if (eCur == null) {
			return null;
		}

		ArrayList<Integer> types = new ArrayList<Integer>();
		try {
			startManagingCursor(eCur); 
			if (eCur.moveToFirst()) {
				do {
					types.add(new Integer(
							eCur.getInt(eCur.getColumnIndex(BBContactsDBAdapter.PREFERENCES_TYPE))));
				} while (eCur.moveToNext());
			}
			return types;			
		}
		finally {
			eCur.close();
		}
	}
	
	private ArrayList<Email> getEmails(String id, ArrayList<Integer> types) {
		ArrayList<Email> emails = new ArrayList<Email>();
		
		String inStr = arrayListToString(types);
		
		Cursor emailCur = managedQuery( 
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
				null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ? AND " +
				ContactsContract.CommonDataKinds.Email.TYPE + " IN " + inStr, 
				new String[]{id}, 
				null); 
		
 		if (emailCur.moveToFirst()) {
			do {
				String address = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
	 			emails.add(new Email(address, emailType));
			} while (emailCur.moveToNext());
		} 		
		emailCur.close();
		return emails;
	}
	
	private Address getAddress(String id) {
		
		if (addressTypes == null) {
			addressTypes = getTypes(BBContactsDBAdapter.FIELD_ADDRESS);
			if (addressTypes == null) {
				return new Address("", "", "", "", "", "", "");
			}
		}		
		
		ArrayList<Address> addresses = getAddresses(id, addressTypes);
		
		// if no addresses return blank address
		if (addresses.size() == 0) {
			return new Address("", "", "", "", "", "", "");
		}
		
		return addresses.get(0);		
	}
	
	private ArrayList<Address> getAddresses(String id, ArrayList<Integer> types) {
	ArrayList<Address> addrList = new ArrayList<Address>();
	
	String inStr = arrayListToString(types);

	Cursor addrCur = managedQuery(ContactsContract.Data.CONTENT_URI, 
			null, 
			ContactsContract.Data.CONTACT_ID + " = ? AND " + 
			ContactsContract.Data.MIMETYPE + " = ? AND " +
			ContactsContract.CommonDataKinds.StructuredPostal.TYPE  + " IN " + inStr, 
			new String[]{id,
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}, 
			null);
	
		if (addrCur.moveToFirst()) {
			do {
				String poBox = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
				String street = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
				String city = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
				String state = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
				String postalCode = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
				String country = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
				String type = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				addrList.add(new Address(poBox, street, city, state, postalCode, country, type));
			} while (addrCur.moveToNext());
		} 		
	addrCur.close();
	return(addrList);
	}

	private String arrayListToString(ArrayList<Integer> types) {
		// create (1, 3) clause
		String inStr = types.toString();
		// now replace the square brackets with round ones
		inStr = inStr.replace('[', '(').replace(']', ')');
		return inStr;
	}
	
	private String getOrg(String id, int type) {
		ArrayList<Organization> orgs = getOrgs(id, type);
		
		// if no orgs return ""
		if (orgs.size() == 0) {
			return "";
		}
		
		return orgs.get(0).getOrganization();		
	}

	private ArrayList<Organization> getOrgs(String id, int type) {
 		ArrayList<Organization> orgs = new ArrayList<Organization>();
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + 
 					   ContactsContract.Data.MIMETYPE + " = ? AND " +
 					   ContactsContract.CommonDataKinds.Organization.TYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, 
 				ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE,
 				Integer.toString(type)}; 
 		
 		Cursor orgCur = managedQuery(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
 
 		if (orgCur.moveToFirst()) {
			do {
	 			String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
	 			String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
	 			//String type = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
	 			orgs.add(new Organization(orgName, title, Integer.toString(type)));
			} while (orgCur.moveToNext());
		} 		
 		orgCur.close();
 		return(orgs);
 	}
	
	private String getWebsite(String id, int type) {
		ArrayList<Website> websites = getWebsites(id, type);
		
		// if no websites return ""
		if (websites.size() == 0) {
			return "";
		}
		
		return websites.get(0).getUrl();		
	}

	private ArrayList<Website> getWebsites(String id, int type) {
		ArrayList<Website> websites = new ArrayList<Website>();
		
 		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + 
		   ContactsContract.Data.MIMETYPE + " = ? AND " +
		   ContactsContract.CommonDataKinds.Organization.TYPE + " = ?"; 
 		String[] whereParameters = new String[]{id, 
 				ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
 				Integer.toString(ContactsContract.CommonDataKinds.Website.TYPE_WORK)}; 
		
		Cursor websiteCur = managedQuery( 
				ContactsContract.Data.CONTENT_URI, 
				null,
				where,
				whereParameters,
				null);
		
 		if (websiteCur.moveToFirst()) {
			do {
				String url = websiteCur.getString(websiteCur.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
				//String type = websiteCur.getString(websiteCur.getColumnIndex(ContactsContract.CommonDataKinds.Website.TYPE));
	 			websites.add(new Website(url, Integer.toString(type)));
			} while (websiteCur.moveToNext());
		} 		
		websiteCur.close();
		return websites;
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

	@Override
	public void onSettingsUpdated() {
		// reset the class variables that store email types and the contact data
		contactList = null;
		emailTypes = null;
		addressTypes = null;
	}
}





