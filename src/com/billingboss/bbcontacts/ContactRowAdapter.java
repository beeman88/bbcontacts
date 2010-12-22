package com.billingboss.bbcontacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ContactRowAdapter extends SimpleAdapter { 

	private List<ContactRow> contacts;
	/*
	 * Alternating color list -- you could initialize this from anywhere.
	 * Note that the colors make use of the alpha here, otherwise they would be
	 * opaque and wouldn't give good results!
	 */
	//private int[] colors = new int[] { 0x30ffffff, 0x30808080 };
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
		
		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
		  		TextView orgText = (TextView) v.findViewById(R.id.org);		
				if (!orgText.getText().equals("")) {				
					CheckedTextView chkBox = (CheckedTextView) v.findViewById(R.id.row_checkbox);				
					chkBox.toggle();
				}
			}
		});

		// wow put the checkbox click listener here - toggle
		CheckedTextView chkBox = (CheckedTextView) view.findViewById(R.id.row_checkbox);
		chkBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				((CheckedTextView) v).toggle();
			}
		});

  		TextView orgText = (TextView) view.findViewById(R.id.org);		
		if (orgText.getText().equals("")) {
			// if organization is empty, disable the checkbox
			setCheckBox(chkBox, false);
		}
		else {
			setCheckBox(chkBox, true);		}

		ImageView more = (ImageView) view.findViewById(R.id.more);
		more.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {	
					AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
					builder.setTitle(ctx.getString(R.string.address));
					
					ContactRow row = contacts.get(pos);					
					final CharSequence[] items = getAddressItems(row);
					
					// if there is no address fields, don't bother displaying pop up
					if (items.length == 0) {
						return;
					}
					
					builder.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							//Toast.makeText(getApplicationContext(), arrayItems[item], Toast.LENGTH_SHORT).show();
						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
			}

			private CharSequence[] getAddressItems(ContactRow row) {
				// if address is null show an empty dialog
				ArrayList<String> items = new ArrayList<String>();
				if (row.address != null) {
					// add address fields in pairs
					addToItems(row.address.getPoBox(), 
							   row.address.getStreet(), items);
					addToItems(row.address.getCity(), 
							   row.address.getState(), items);
					addToItems(row.address.getCountry(), 
							   row.address.getPostalCode(), items);
				}
				CharSequence[] csItems = new CharSequence[items.size()];
				for (int i=0; i < items.size(); i++) {
					csItems[i] = items.get(i);
				}
				return csItems;
			}

			private void addToItems(String field1,
					String field2,
					ArrayList<String> items) {
				String fields = field1 + " " + field2;
				if (!fields.trim().equals("")) {
					items.add(fields.trim());
				}
			}
		});

/*		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);*/
		return view;
	}

	private void setCheckBox(CheckedTextView chkBox, boolean setting) {
		chkBox.setChecked(setting);
		chkBox.setEnabled(setting);
		chkBox.setClickable(setting);
		chkBox.setFocusable(setting);
	}

}
