/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.billingboss.bbcontacts;

/* import java.io.File; */
import java.io.IOException;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Based on Simple notes database tutorial. 
 */
public class BBContactsDBAdapter {
	
    private static final String DATABASE_NAME = "data";
    
    private static final String TABLE_CUSTOMERS = "customers";
    public static final String CUSTOMER_ROWID = "_id";
    public static final String CUSTOMER_NAME = "name";
    public static final String CUSTOMER_BB_ID = "bb_id";
    public static final String CUSTOMER_UPDATED_AT = "updated_at"; 
    public static final int CUSTOMER_COL_ROWID = 0;
    public static final int CUSTOMER_COL_NAME = 1;
    public static final int CUSTOMER_COL_BB_ID = 2;
    public static final int CUSTOMER_COL_UPDATED_AT = 3; 
    
    private static final String TABLE_CONTACTS = "contacts";
    public static final String CONTACT_ROWID = "_id";
    public static final String CONTACT_ID = "contact_id";    
    public static final String CONTACT_CUSTOMER_ID = "customer_id";    
    public static final String CONTACT_UPDATED_AT = "updated_at";    
    
    private static final String TABLE_SETTINGS = "settings";
    public static final String SETTINGS_ROWID = "_id";
    public static final String SETTINGS_USERNAME = "username";
    public static final String SETTINGS_PASSWORD = "password";    
    public static final String SETTINGS_UPDATED_AT = "updated_at";
    
    private static final String TABLE_PREFERENCES = "preferences";
    public static final String PREFERENCES_ROWID = "_id";
    public static final String PREFERENCES_FIELD = "field";
    public static final String PREFERENCES_TYPE = "type";    
    public static final String PREFERENCES_SEQUENCE = "sequence"; 
    
    public static final int FIELD_EMAIL = 1;
    public static final int FIELD_ADDRESS = 2;
    public static final int FIELD_ORGANIZATION = 3;
    public static final int FIELD_PHONE = 4;    
    
    private static final int DATABASE_VERSION = 1;	

    private static final String TAG = "BBContactsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_CUSTOMERS =
        "create table customers (_id integer primary key autoincrement, " +
                "name text not null unique, " +
                "bb_id text not null unique, " +
        		"updated_at bigint);";
    private static final String DATABASE_CREATE_CONTACTS =
        "create table contacts (_id integer primary key autoincrement, " +
        		"contact_id integer unique, " +
        		"customer_id integer, " +
        		"updated_at bigint);";
    private static final String DATABASE_CREATE_SETTINGS =
        "create table settings (_id integer primary key autoincrement, " +
                "username text, " +
                "password text, " +                
        		"updated_at bigint);";
    private static final String DATABASE_CREATE_PREFERENCES =
        "create table preferences (_id integer primary key autoincrement, " +
                "field int, " +
                "type int, " +                
        		"sequence int);";    

    private final Context mCtx;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_CUSTOMERS);
            db.execSQL(DATABASE_CREATE_CONTACTS);
            db.execSQL(DATABASE_CREATE_SETTINGS);
            db.execSQL(DATABASE_CREATE_PREFERENCES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS customers");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            db.execSQL("DROP TABLE IF EXISTS settings");
            db.execSQL("DROP TABLE IF EXISTS preferences");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public BBContactsDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the bbcontacts database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public BBContactsDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new customer using the name provided. If the customer is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name the name of the customer
     * @param updated_at the last updated timestamp of the customer
     * @return rowId or -1 if failed
     * @throws IOException 
     */
    public long createCustomer(String name, String bb_id){
        ContentValues initialValues = new ContentValues();
        initialValues.put(CUSTOMER_NAME, name);
        initialValues.put(CUSTOMER_BB_ID, bb_id);
        initialValues.put(CUSTOMER_UPDATED_AT, getCurrentTime());

        return mDb.insert(TABLE_CUSTOMERS, null, initialValues);
    }
    
    /**
     * Create a new contact using the customer_id, first, last, email, phone. If the contact is
     * successfully created return the new rowId for that contact, otherwise return
     * a -1 to indicate failure.
     * 
     * @param customer_id the customer_id of the contact
     * @param contact_id the id of the contact
     * @param updated_at the last updated timestamp of the contact
     * @return rowId or -1 if failed
     * @throws IOException 
     */
    public long createContact(Integer contact_id, Integer customer_id ){
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(CONTACT_ID, contact_id);        
        initialValues.put(CONTACT_CUSTOMER_ID, customer_id);
        initialValues.put(CUSTOMER_UPDATED_AT, getCurrentTime());

        return mDb.insert(TABLE_CONTACTS, null, initialValues);
    }
    
    /**
     * Create a new setting using the username, password. If the setting is
     * successfully created return the new rowId for that setting, otherwise return
     * a -1 to indicate failure.
     * 
     * @param username for the settings billing boss username
     * @param password for the settings billing boss password
     * @param updated_at the last updated timestamp of the contact
     * @return rowId or -1 if failed
     * @throws IOException 
     */
    public long createSetting(String username, String password){
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(SETTINGS_USERNAME, username);        
        initialValues.put(SETTINGS_PASSWORD, password);
        initialValues.put(SETTINGS_UPDATED_AT, getCurrentTime());

        return mDb.insert(TABLE_SETTINGS, null, initialValues);
    }    

    /**
     * Create a new preference using field, type, sequence. If the preference is
     * successfully created return the new rowId for that preference, otherwise return
     * a -1 to indicate failure.
     * 
     * @param field for the contact field (EMAIL, PHONE, ADDRESS, ORGANIZATION, WEBSITE) 
     * @param type for the type of field (WORK, MOBILE, HOME .... )
     * @param sequence for the priority
     * @return rowId or -1 if failed
     * @throws IOException 
     */
    public long createPreference(int field, int type, int sequence){
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(PREFERENCES_FIELD, field);        
        initialValues.put(PREFERENCES_TYPE, type);
        initialValues.put(PREFERENCES_SEQUENCE, sequence);

        return mDb.insert(TABLE_PREFERENCES, null, initialValues);
    }    
    
    /**
     * Delete the customer with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCustomer(long rowId) {

        return mDb.delete(TABLE_CUSTOMERS, CUSTOMER_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Delete all customers
     * 
     * @return true if deleted, false otherwise
     */
    public boolean deleteCustomers() {

        return mDb.delete(TABLE_CUSTOMERS, null, null) > 0;
    }    
    
    /**
     * Delete the contact with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteContact(long rowId) {

        return mDb.delete(TABLE_CONTACTS, CONTACT_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Delete all contacts
     * 
     * @return true if deleted, false otherwise
     */
    public boolean deleteContacts() {

        return mDb.delete(TABLE_CONTACTS, null, null) > 0;
    }
    
    /**
     * Delete the preference with the given rowId
     * 
     * @param rowId id of contact to delete
     * @return true if deleted, false otherwise
     */
    public boolean deletePreference(long rowId) {

        return mDb.delete(TABLE_PREFERENCES, PREFERENCES_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Delete the preference with the given rowId
     * 
     * @param field of preferences to delete
     * @return true if deleted, false otherwise
     */
    public boolean deletePreferencesByField(int field) {

        return mDb.delete(TABLE_PREFERENCES, 
        		PREFERENCES_FIELD + "=" + Integer.toString(field), 
        		null) > 0;
    }
    
    /**
     * Delete all preferences
     * 
     * @return true if deleted, false otherwise
     */
    public boolean deletePreferences() {

        return mDb.delete(TABLE_PREFERENCES, null, null) > 0;
    }
    
    /**
     * Return a Cursor positioned at the customer that matches the given rowId
     * 
     * @param rowId id of customer to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchCustomer(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_CUSTOMERS, new String[] {CUSTOMER_ROWID,
                    CUSTOMER_NAME, CUSTOMER_BB_ID, CUSTOMER_UPDATED_AT}, CUSTOMER_ROWID + "=" + rowId, null,
                    null, null, null, null);
        return checkCursor(mCursor);
    }

    /**
     * Return a Cursor over the list of all customers in the database
     * 
     * @return Cursor over all customers
     */
    public Cursor fetchAllCustomers() {

    	Cursor mCursor =
    		mDb.query(TABLE_CUSTOMERS, new String[] {CUSTOMER_ROWID, CUSTOMER_NAME, CUSTOMER_BB_ID,
                CUSTOMER_UPDATED_AT}, null, null, null, null, null);
    	
        return checkCursor(mCursor);
    }
    
    /**
     * Return a Cursor positioned at the customer that matches the given rowId
     * 
     * @param rowId id of customer to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchContact(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_CONTACTS, new String[] {CONTACT_ROWID,
            		CONTACT_ID, CONTACT_CUSTOMER_ID, CUSTOMER_UPDATED_AT}, CONTACT_ROWID + "=" + rowId, null,
                    null, null, null, null);
        return checkCursor(mCursor);

    }

    /**
     * Return a Cursor over the list of all contacts in the database
     * 
     * @return Cursor over all contacts
     */
    public Cursor fetchAllContacts() {

    	Cursor mCursor =
    		mDb.query(TABLE_CONTACTS, new String[] {CONTACT_ROWID, CONTACT_ID,
                CONTACT_CUSTOMER_ID, CONTACT_UPDATED_AT}, null, null, null, null, null);
        return checkCursor(mCursor);    	
    }
    
    /**
     * Return a Cursor positioned at the customer that matches the given rowId
     * 
     * @param rowId id of customer to retrieve
     * @return Cursor positioned to matching setting, if found
     * @throws SQLException if settomg could not be found/retrieved
     */
    public Cursor fetchContactByContactId(long contactId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_CONTACTS, new String[] {CONTACT_ROWID,
            		CONTACT_ID, CONTACT_CUSTOMER_ID, CUSTOMER_UPDATED_AT}, CONTACT_ID + "=" + contactId, null,
                    null, null, null, null);
        return checkCursor(mCursor);
    }
    
    /**
     * Return a Cursor positioned at the setting that matches the given rowId
     * 
     * @param rowId id of customer to retrieve
     * @return Cursor positioned to matching setting, if found
     * @throws SQLException if setting could not be found/retrieved
     */
    public Cursor fetchSetting(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_SETTINGS, new String[] {SETTINGS_ROWID,
                    SETTINGS_USERNAME, SETTINGS_PASSWORD, SETTINGS_UPDATED_AT}, SETTINGS_ROWID + "=" + rowId, null,
                    null, null, null, null);
        return checkCursor(mCursor);
    }

	private Cursor checkCursor(Cursor mCursor) {
		// if cursor null or has 0 rows
        if (mCursor == null || mCursor.getCount() == 0) {
        	return null;
        }
        
        mCursor.moveToFirst();        
        return mCursor;
	}
	
    /**
     * Return a Cursor positioned at the preference that matches the given rowId
     * 
     * @param rowId id of preference to retrieve
     * @return Cursor positioned to matching preference, if found
     * @throws SQLException if preference could not be found/retrieved
     */
    public Cursor fetchPreference(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, TABLE_PREFERENCES, new String[] {PREFERENCES_ROWID,
                    PREFERENCES_FIELD, PREFERENCES_TYPE, PREFERENCES_SEQUENCE}, PREFERENCES_ROWID + "=" + rowId, null,
                    null, null, null, null);
        return checkCursor(mCursor);
    }

    /**
     * Return a Cursor over the list of all preferences in the database
     * 
     * @return Cursor over all preferences
     */
    public Cursor fetchAllPreferences() {

    	Cursor mCursor =
    		mDb.query(TABLE_PREFERENCES, new String[] {PREFERENCES_ROWID,
                    PREFERENCES_FIELD, PREFERENCES_TYPE, PREFERENCES_SEQUENCE}, null, null, null, null, null);
    	
        return checkCursor(mCursor);
    }
    
    /**
     * Return a Cursor over the list of preferences by field (email, phone ...)
     * 
     * @return Cursor over preferences by field 
     */
    public Cursor fetchPreferencesByField(int field) {

    	Cursor mCursor =
    		mDb.query(TABLE_PREFERENCES, 
    				new String[] {PREFERENCES_ROWID, PREFERENCES_FIELD, PREFERENCES_TYPE, PREFERENCES_SEQUENCE}, 
                    PREFERENCES_FIELD + "=" + Integer.toString(field), 
                    null, 
                    null, 
                    null, 
                    "SEQUENCE ASC");
    	
        return checkCursor(mCursor);
    }

    /**
     * Update the customer using the details provided. The customer to be updated is
     * specified using the rowId, and it is altered to use the name and updated_at
     * values passed in
     * 
     * @param rowId id of note to update
     * @param name value to set customer name to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCustomer(long rowId, String name, String bb_id ) {
        ContentValues args = new ContentValues();
        args.put(CUSTOMER_NAME, name);
        args.put(CUSTOMER_BB_ID, name);
        args.put(CUSTOMER_UPDATED_AT, getCurrentTime());

        return mDb.update(TABLE_CUSTOMERS, args, CUSTOMER_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Update the customer using the details provided. The customer to be updated is
     * specified using the rowId, and it is altered to use the name and updated_at
     * values passed in
     * 
     * @param rowId id of contact to update
     * @param customer_id value to set contact's customer id 
     * @param first value to set contact's first name
     * @param last value to set contact's last name
     * @param email value to set contact's email 
     * @param phone value to set contact's phone 
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateContact(
    		long rowId, long contact_id, long customer_id ) {
        ContentValues args = new ContentValues();
        args.put(CONTACT_ID, contact_id);        
        args.put(CONTACT_CUSTOMER_ID, customer_id);        
        args.put(CONTACT_UPDATED_AT, getCurrentTime());

        return mDb.update(TABLE_CONTACTS, args, CONTACT_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Update the setting using the details provided. The setting to be updated is
     * specified using the rowId, and it is altered to use the language, username, password and updated_at
     * values passed in
     * 
     * @param rowId id of setting to update (1)
     * @param username to set setting's username
     * @param password to set setting's password
     * @return true if the setting was successfully updated, false otherwise
     */
    public boolean updateSetting(
    		long rowId, String username, String password ) {
        ContentValues args = new ContentValues();
        args.put(SETTINGS_USERNAME, username);
        args.put(SETTINGS_PASSWORD, password);
        args.put(SETTINGS_UPDATED_AT, getCurrentTime());

        return mDb.update(TABLE_SETTINGS, args, SETTINGS_ROWID + "=" + rowId, null) > 0;
    }
    
    
    private Long getCurrentTime() {
    	return new Long(new Date().getTime());
    }
    
}
