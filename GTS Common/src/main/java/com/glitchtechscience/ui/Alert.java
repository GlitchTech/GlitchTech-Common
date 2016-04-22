package com.glitchtechscience.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Alert Dialog helper class.
 */
public class Alert {

	private final static int DEFAULT_ICON = android.R.drawable.ic_dialog_alert;

	public static void Message( Context ctx, String title, String message ) {

		Message( ctx, title, message, DEFAULT_ICON );
	}

	public static void Message( Context ctx, String title, String message, int icon ) {

		Confirmation( ctx, title, message, null, icon, "OK", null );
	}

	public static void Confirmation( Context ctx, String title, String message, DialogInterface.OnClickListener callBack, String positive ) {

		Confirmation( ctx, title, message, callBack, positive, null );
	}

	public static void Confirmation( Context ctx, String title, String message, DialogInterface.OnClickListener callBack, String positive, String negative ) {

		Confirmation( ctx, title, message, callBack, DEFAULT_ICON, positive, negative );
	}

	public static void Confirmation( Context ctx, String title, String message, DialogInterface.OnClickListener callBack, int icon, String positive, String negative ) {

		final AlertDialog.Builder builder = new AlertDialog.Builder( ctx );

		// Set Text
		if( title != null ) {

			builder.setTitle( title );
		}

		builder.setMessage( ( message == null ) ? "" : message );

		// Disabled back button exit
		builder.setCancelable( false );

		// Set icon
		builder.setIcon( icon );

		// Bind buttons
		if( negative != null ) {

			builder.setNegativeButton( negative, new DialogInterface.OnClickListener() {

				@Override
				public void onClick( DialogInterface dialogInterface, int i ) {

					dialogInterface.dismiss();
				}
			} );
		}

		builder.setPositiveButton( positive, callBack );

		builder.create().show();
	}
}
