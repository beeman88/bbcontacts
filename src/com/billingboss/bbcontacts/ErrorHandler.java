package com.billingboss.bbcontacts;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ErrorHandler extends Exception {

	public static void ToastError(Context ctx, String message) {
	  Toast.makeText(ctx, message,		    		  
		      Toast.LENGTH_LONG).show();
	}
	
	public static void LogError(String tag, String message) {
		Log.e(tag, message);
	}

	public static void LogToastError(Context ctx, String tag, String message) {
		ToastError(ctx, message);
		LogError(tag, message);
	}
	
	
}
