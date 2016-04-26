package com.glitchtechscience.utility;

import android.text.Html;

public class StringTools {

	public static String formatter( String in ) {

		return Html.fromHtml( in ).toString();
	}

	private static String[] dirtyItem = new String[] { "&", "\"", "<", ">", "`", "\n" };

	private static String[] cleanItem = new String[] { "&amp;", "&quot;", "$lt;", "&gt;", "'", " " };

	public static String cleanString( String dirtyString ) {

		if( dirtyString.length() <= 0 ) {

			return "";
		}

		for( int i = 0; i < dirtyItem.length; i++ ) {

			dirtyString = dirtyString.replaceAll( dirtyItem[i], cleanItem[i] );
		}

		return( dirtyString );
	}

	public static String dirtyString( String cleanString ) {

		if( cleanString.length() <= 0 ) {

			return "";
		}

		for( int i = 0; i < dirtyItem.length; i++ ) {

			cleanString = cleanString.replaceAll( cleanItem[i], dirtyItem[i] );
		}

		return( cleanString );
	}
}
