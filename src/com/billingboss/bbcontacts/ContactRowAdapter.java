package com.billingboss.bbcontacts;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
		  ContactRow row = contacts.get(pos);	
	    	final CharSequence[] items = {
	    			row.address.getStreet(), 
	    			row.address.getCity(), 
	    			row.address.getCountry()};

	    	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
	    	builder.setTitle(ctx.getString(R.string.address));
	    	
	    	builder.setItems(items, new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int item) {
	    	        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	    	    }
	    	});
	    	AlertDialog dialog = builder.create();
	    	dialog.show();
		}
	});

	  int colorPos = position % colors.length;
	  view.setBackgroundColor(colors[colorPos]);
	  return view;
	}

	}
