package com.glitchtechscience.utility.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

public abstract class BaseDatabaseObject {

	/** Common field for all database objects */

	/** Schema for BaseDatabaseObject db fields */
	public final static String COMMON_FIELD_SCHEMA = "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lastSync INTEGER, pendingDeletion INTEGER NOT NULL DEFAULT 0";

	/** DatabaseColumn( INTEGER UNIQUE PRIMARY KEY ASC ) */
	private long id = -1;

	/** DatabaseColumn( TEXT ) */
	protected long lastSync;

	/** pendingDeletion INTEGER NOT NULL DEFAULT 0 */
	protected int pendingDeletion = 0;

	/* ----- ----- ----- */

	/**
	 * @return long
	 */
	public long getId() {

		return this.id;
	}

	/**
	 * @param id
	 * 		ID of object of type long
	 */
	public void setId( long id ) {

		this.id = id;
	}

	/**
	 * @return timestamp of last server sync
	 */
	public long getLastSync() {

		return this.lastSync;
	}

	/**
	 * @param lastSync
	 * 		timestamp of last server sync
	 */
	public void setLastSync( long lastSync ) {

		this.lastSync = lastSync;
	}

	/**
	 * @param date
	 * 		Calendar object of last server sync
	 */
	@SuppressWarnings("UnusedDeclaration")
	public void setLastSync( Calendar date ) {

		this.lastSync = date.getTimeInMillis();
	}

	/**
	 * Set the last sync timestamp to the current system time
	 */
	@SuppressWarnings("UnusedDeclaration")
	public void setLastSyncNow() {

		this.lastSync = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * Is object pending deletion?
	 *
	 * @return boolean
	 */
	public boolean getPendingDeletion() {

		return this.pendingDeletion == 1;
	}

	/**
	 * @param pendingDeletion
	 * 		Is object pending deletion
	 */
	public void setPendingDeletion( boolean pendingDeletion ) {

		this.pendingDeletion = pendingDeletion ? 1 : 0;
	}

	/* ----- ----- ----- */

	/**
	 * Constructor
	 */
	public BaseDatabaseObject() {

	}

	/* ----- ----- ----- */

	/**
	 * Return the content resolver. Requires the abstract method getContext()
	 *
	 * @return ContentResolver
	 */
	protected android.content.ContentResolver getContentResolver() {

		return getContext().getContentResolver();
	}

	/* ----- ----- ----- */

	/**
	 * Load the current object from the database.
	 */
	public void load() {

		load( getProjection(), getSelection(), getSelectionArgs(), getSortOrder(), "1" );
	}

	/**
	 * Load the current object from the database.
	 *
	 * @param projection
	 * 		String[]   parameters to retrieve
	 * @param selection
	 * 		String  Where string
	 * @param selectionArgs
	 * 		String[]    Where arguments in array
	 * @param sortOrder
	 * 		String  Sort pattern string
	 * @param limit
	 * 		String  Selection limits
	 */
	public void load( String[] projection, String selection, String[] selectionArgs, String sortOrder, String limit ) {

		Uri uri = getUri().buildUpon().appendQueryParameter( "limit", limit ).build();

		if( uri != null ) {

			Cursor c = getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );

			setData( c );
		}
	}

	/**
	 * Save the current object to the database
	 *
	 * @return number of rows touched
	 */
	public int save() {

		if( getId() < 0 ) {

			ContentValues data = getData();

			data.remove( getIdFieldName() );

			return insert( data );
		} else {

			return update();
		}
	}

	/**
	 * Insert current object into database
	 *
	 * @param data
	 * 		ContentValues
	 *
	 * @return number of rows touched
	 */
	public int insert( ContentValues data ) {

		Uri result = getContentResolver().insert( getUri(), data );

		if( result != null ) {

			setId( Long.parseLong( result.getLastPathSegment() ) );
		}

		return 1;
	}

	/**
	 * Updates current record of this object.
	 *
	 * @return number of rows touched
	 */
	public int update() {

		int numUpdated = getContentResolver().update( getUri(), getData(), getSelection(), getSelectionArgs() );
		Log.d( "Save", "Count: " + numUpdated );

		return numUpdated;
	}

	/**
	 * Removes current object from database
	 *
	 * @return number of rows touched
	 */
	public int delete() {

		int numDeleted = getContentResolver().delete( getUri(), getSelection(), getSelectionArgs() );
		Log.d( "Delete", "Count: " + numDeleted );

		return numDeleted;
	}

	/* ----- ----- ----- */

	/**
	 * Returns ContentValues version of object.
	 *
	 * @return ContentValues
	 */
	public ContentValues getData() {

		ContentValues data = new ContentValues();

		data.put( getIdFieldName(), this.getId() );
		data.put( "lastSync", this.getLastSync() );
		data.put( "pendingDeletion", this.getPendingDeletion() ? 1 : 0 );

		return data;
	}

	/**
	 * Loads data from Cursor into object
	 *
	 * @param c
	 * 		Cursor
	 */
	public void setData( Cursor c ) {

		ContentValues data = new ContentValues();

		if( c.isBeforeFirst() ) {

			c.moveToFirst();
		}

		if( !c.isAfterLast() && !c.isBeforeFirst() ) {

			DatabaseUtils.cursorRowToContentValues( c, data );
		}

		setData( data );
	}

	/**
	 * Loads data from ContentValues into object
	 *
	 * @param data
	 * 		ContentValues
	 */
	public void setData( ContentValues data ) {

		if( data.containsKey( getIdFieldName() ) ) {

			this.setId( data.getAsLong( getIdFieldName() ) );
		}

		if( this.isCVValueLong( data, "lastSync" ) ) {

			this.setLastSync( data.getAsLong( "lastSync" ) );
		}

		if( data.containsKey( "pendingDeletion" ) ) {

			this.setPendingDeletion( 1 == data.getAsInteger( "pendingDeletion" ) );
		}
	}

	/**
	 * Helper method to determine if key is in ContentValues and key contains a long
	 *
	 * @param data
	 * 		ContentValue
	 * @param key
	 * 		String
	 *
	 * @return True if valid long
	 */
	protected boolean isCVValueLong( ContentValues data, String key ) {

		return ( data.containsKey( key ) && data.getAsLong( key ) != null );
	}

	/* ----- ----- ----- */

	/**
	 * Returns database field name of ID variable
	 *
	 * @return String
	 */
	public String getIdFieldName() {

		return "id";
	}

	/**
	 * Returns basic find string
	 *
	 * @return String
	 */
	public String getSelection() {

		return getIdFieldName() + " = ?";
	}

	/**
	 * Returns argument list for the basic find string
	 *
	 * @return String[]
	 */
	public String[] getSelectionArgs() {

		return new String[]{ this.getId() + "" };
	}

	/* ----- ----- ----- */

	/**
	 * (Mostly) Abstract methods; must be overridden
	 */

	// TODO add abstract method descriptions
	public abstract Context getContext();

	/**
	 * Return ContentProvider Uri for object
	 *
	 * @return Uri
	 */
	public abstract Uri getUri();

	/**
	 * Return a string array containing all fields to be retrieved from the database
	 *
	 * @return String[]
	 */
	public abstract String[] getProjection();

	/**
	 * Return a string containing the ORDER BY argument.
	 *
	 * @return String
	 */
	public abstract String getSortOrder();

	/* ----- ----- ----- */

	/**
	 * Adds base projection fields to the array
	 *
	 * @param childProjection
	 * 		String[]
	 *
	 * @return String[]
	 */
	protected String[] addBaseProjection( String[] childProjection ) {

		String[] superProjection = new String[]{ getIdFieldName() + " AS _id", getIdFieldName(), "pendingDeletion", "lastSync" };

		int superLength = superProjection.length;
		int childLength = childProjection.length;

		String[] mergedProjection = new String[superLength + childLength];

		System.arraycopy( superProjection, 0, mergedProjection, 0, superLength );
		System.arraycopy( childProjection, 0, mergedProjection, superLength, childLength );

		return mergedProjection;
	}
}
