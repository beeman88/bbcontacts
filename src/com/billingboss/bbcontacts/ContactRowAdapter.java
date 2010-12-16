package com.billingboss.bbcontacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
/*		TextView orgText = (TextView) view.findViewById(R.id.org);
		if (orgText.getText().equals("")) {
			// if organization is empty, disable the checkbox
			chkBox.setChecked(false);
			chkBox.setEnabled(false);
		}
*/		
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
					addToItems(row.address.getStreet(), items);
					addToItems(row.address.getPoBox(), items);
					addToItems(row.address.getCity(), items);
					addToItems(row.address.getState(), items);
					addToItems(row.address.getCountry(), items);
					addToItems(row.address.getPostalCode(), items);
				}
				CharSequence[] csItems = new CharSequence[items.size()];
				for (int i=0; i < items.size(); i++) {
					csItems[i] = items.get(i);
				}
				return csItems;
			}

			private void addToItems(String field, ArrayList<String> items) {
				if (!field.trim().equals("")) {
					items.add(field);
				}
			}
		});

		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);
		return view;
	}

}
