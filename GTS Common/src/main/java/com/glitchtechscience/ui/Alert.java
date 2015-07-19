package com.glitchtechscience.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Alert Dialog helper class.
 */
public class Alert {

	private static final int MESSAGE_ALERT = 1;
	private static final int CONFIRM_ALERT = 2;

	public static void messageAlert( Context ctx, String title, String message ) {

		showAlertDialog( MESSAGE_ALERT, ctx, title, message, null, "OK" );
	}

	public static void messageAlert( Context ctx, String title, String message, int icon ) {

		showAlertDialog( MESSAGE_ALERT, ctx, title, message, null, icon, "OK" );
	}

	public static void messageAlert( Context ctx, String title, String message, int icon, boolean cancelable ) {

		showAlertDialog( MESSAGE_ALERT, ctx, title, message, null, icon, cancelable, "OK" );
	}

	public static void confirmationAlert( Context ctx, String title, String message, DialogInterface.OnClickListener callBack ) {

		showAlertDialog( CONFIRM_ALERT, ctx, title, message, callBack, "OK" );
	}

	public static void confirmationAlert( Context ctx, String title, String message, DialogInterface.OnClickListener callBack, String... buttonNames ) {

		showAlertDialog( CONFIRM_ALERT, ctx, title, message, callBack, buttonNames );
	}

	public static void confirmationAlert( Context ctx, String title, String message, DialogInterface.OnClickListener callBack, int icon, String... buttonNames ) {

		showAlertDialog( CONFIRM_ALERT, ctx, title, message, callBack, icon, buttonNames );
	}

	private static void showAlertDialog( int alertType, Context ctx, String title, String message, DialogInterface.OnClickListener posCallback, String... buttonNames ) {

		showAlertDialog( alertType, ctx, title, message, posCallback, android.R.drawable.ic_dialog_alert, buttonNames );
	}

	private static void showAlertDialog( int alertType, Context ctx, String title, String message, DialogInterface.OnClickListener posCallback, int icon, String... buttonNames ) {

		showAlertDialog( alertType, ctx, title, message, posCallback, icon, false, buttonNames );
	}

	private static void showAlertDialog( int alertType, Context ctx, String title, String message, DialogInterface.OnClickListener posCallback, int icon, boolean cancelable, String... buttonNames ) {

		final AlertDialog.Builder builder = new AlertDialog.Builder( ctx );

		if( title != null ) {

			builder.setTitle( title );
		}

		builder.setMessage( ( message == null ) ? "" : message );

		// false = pressing back button won't dismiss this alert
		builder.setCancelable( cancelable );

		// icon on the left of title
		builder.setIcon( icon );

		switch( alertType ) {
			case MESSAGE_ALERT:
				break;
			case CONFIRM_ALERT:
				builder.setPositiveButton( buttonNames[0], posCallback );
				break;
		}

		builder.setNegativeButton( buttonNames[buttonNames.length - 1], new DialogInterface.OnClickListener() {

			@Override
			public void onClick( DialogInterface dialogInterface, int i ) {

				dialogInterface.dismiss();
			}
		} ).create().show();
	}
}
