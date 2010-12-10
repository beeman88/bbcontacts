package com.billingboss.bbcontacts;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class ContactRowAdapter extends SimpleAdapter { 

	private List<ContactRow> contacts;
	/*
	* Alternating color list -- you could initialize this from anywhere.
	* Note that the colors make use of the alpha here, otherwise they would be
	* opaque and wouldn't give good results!
	*/
	private int[] colors = new int[] { 0x30ffffff, 0x30808080 };
	private final String TAG = "ContactRowAdapter";

	@SuppressWarnings("unchecked")
	public ContactRowAdapter(Context context, 
	        List<? extends Map<String, Contact>> list, 
	        int resource, 
	        String[] from, 
	        int[] to) {
	  super(context, list, resource, from, to);
	  this.contacts = (List<ContactRow>) list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  View view = super.getView(position, convertView, parent);
	  
	  final int pos = position;
	  final Context ctx = view.getContext();
	  
	  // wow put the checkbox click listener here - toggle
      CheckedTextView chkBox = (CheckedTextView) view.findViewById(R.id.row_checkbox);
	  chkBox.setOnClickListener(new View.OnClickListener() {
	      public void onClick(View v)
	      {
	          ((CheckedTextView) v).toggle();
	      }
	    });
	  
	  ImageView more = (ImageView) view.findViewById(R.id.more);
	  more.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
		  try {	
		  ContactRow row = contacts.get(pos);
		  
      	  AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
	      builder.setTitle(ctx.getString(R.string.address));

	      // if address is null show an empty dialog
	      String street = "";
	      String poBox = "";
	      String city = "";
	      String state = "";
	      String country = "";
	      String postal = "";
	      
	      if (row.address != null) {
	    	  street = row.address.getStreet();
	    	  poBox = row.address.getPoBox();
	    	  city = row.address.getCity();
	    	  state = row.address.getState();
	    	  country = row.address.getCountry();
	    	  postal = row.address.getPostalCode();
	      }

    	  final CharSequence[] items = {
    			  street,
    			  poBox,
    			  city, 
    			  state,
    			  postal,
    			  country};

    	  builder.setItems(items, new DialogInterface.OnClickListener() {
    		  public void onClick(DialogInterface dialog, int item) {
    			  //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
    		  }
    	  });
	      AlertDialog dialog = builder.create();
	      dialog.show();
		  }
		  catch (Exception e) {
			  Log.e(TAG, e.getMessage());
		  }
		}
	});

	  int colorPos = position % colors.length;
	  view.setBackgroundColor(colors[colorPos]);
	  return view;
	}

	}
