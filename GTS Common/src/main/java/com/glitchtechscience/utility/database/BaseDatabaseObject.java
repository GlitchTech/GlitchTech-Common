package com.glitchtechscience.utility.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;

import java.util.Calendar;

public abstract class BaseDatabaseObject {

	/** Common field for all database objects */

	/** Schema for BaseDatabaseObject db fields */
	public final static String COMMON_FIELD_SCHEMA = "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lastSync INTEGER, pendingDeletion INTEGER NOT NULL DEFAULT 0";
	public final static String ID_FIELD = "id";
	public final static String LAST_SYNC_FIELD = "lastSync";
	public final static String PENDING_DELETION_FIELD = "pendingDeletion";

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
	 * @see BaseDatabaseObject#load(ContentResolver)
	 */
	public void load() {

		load( getContentResolver() );
	}

	/**
	 * @param cr
	 * 		ContentResolver
	 *
	 * @see BaseDatabaseObject#load(ContentResolver, String[], String, String[], String, String)
	 */
	public void load( ContentResolver cr ) {

		load( cr, getProjection(), getSelection(), getSelectionArgs(), getSortOrder(), "1" );
	}

	/**
	 * Load the current object from the database.
	 *
	 * @param cr
	 * 		ContentResolver
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
	public void load( ContentResolver cr, String[] projection, String selection, String[] selectionArgs, String sortOrder, String limit ) {

		Uri uri = getUri().buildUpon().appendQueryParameter( "limit", limit ).build();

		if( uri != null ) {

			Cursor c = cr.query( uri, projection, selection, selectionArgs, sortOrder );

			setData( c );
		}
	}

	/**
	 * @return number of rows touched
	 *
	 * @see BaseDatabaseObject#save(ContentResolver)
	 */
	public int save() {

		return save( getContentResolver() );
	}

	/**
	 * Save the current object to the database
	 *
	 * @param cr
	 * 		ContentResolver
	 *
	 * @return number of rows touched
	 */
	public int save( ContentResolver cr ) {

		if( getId() < 0 ) {

			ContentValues data = getData();

			data.remove( ID_FIELD );

			return insert( cr, data );
		} else {

			return update( cr );
		}
	}

	/**
	 * @param data
	 * 		ContentValues
	 *
	 * @return number of rows touched
	 *
	 * @see BaseDatabaseObject#update(ContentResolver)
	 */
	public int insert( ContentValues data ) {

		return insert( getContentResolver(), data );
	}

	/**
	 * Insert current object into database
	 *
	 * @param cr
	 * 		ContentResolver
	 * @param data
	 * 		ContentValues
	 *
	 * @return number of rows touched
	 */
	public int insert( ContentResolver cr, ContentValues data ) {

		Uri result = getContentResolver().insert( getUri(), data );

		if( result != null ) {

			setId( Long.parseLong( result.getLastPathSegment() ) );
		}

		return 1;
	}

	/**
	 * @return number of rows touched
	 *
	 * @see BaseDatabaseObject#update(ContentResolver)
	 */
	public int update() {

		return update( getContentResolver() );
	}

	/**
	 * Updates current record of this object.
	 *
	 * @param cr
	 * 		ContentResolver
	 *
	 * @return number of rows touched
	 */
	public int update( ContentResolver cr ) {

		return cr.update( getUri(), getData(), getSelection(), getSelectionArgs() );
	}

	/**
	 * @return number of rows touched
	 *
	 * @see BaseDatabaseObject#delete(ContentResolver)
	 */
	public int delete() {

		return delete( getContentResolver() );
	}

	/**
	 * Removes current object from database
	 *
	 * @param cr
	 * 		ContentResolver
	 *
	 * @return number of rows touched
	 */
	public int delete( ContentResolver cr ) {

		return cr.delete( getUri(), getSelection(), getSelectionArgs() );
	}

	/* ----- ----- ----- */

	/**
	 * Returns ContentValues version of object.
	 *
	 * @return ContentValues
	 */
	public ContentValues getData() {

		ContentValues data = new ContentValues();

		data.put( ID_FIELD, this.getId() );
		data.put( LAST_SYNC_FIELD, this.getLastSync() );
		data.put( PENDING_DELETION_FIELD, this.getPendingDeletion() ? 1 : 0 );

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

		if( data.containsKey( ID_FIELD ) ) {

			this.setId( data.getAsLong( ID_FIELD ) );
		}

		if( this.isCVValueLong( data, LAST_SYNC_FIELD ) ) {

			this.setLastSync( data.getAsLong( LAST_SYNC_FIELD ) );
		}

		if( data.containsKey( PENDING_DELETION_FIELD ) ) {

			this.setPendingDeletion( 1 == data.getAsInteger( PENDING_DELETION_FIELD ) );
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
	 * Returns basic find string
	 *
	 * @return String
	 */
	public String getSelection() {

		return ID_FIELD + " = ?";
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

	/**
	 * Return current context
	 *
	 * @return Context
	 */
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

		String[] superProjection = new String[]{ ID_FIELD + " AS _id", ID_FIELD, PENDING_DELETION_FIELD, LAST_SYNC_FIELD };

		int superLength = superProjection.length;
		int childLength = childProjection.length;

		String[] mergedProjection = new String[superLength + childLength];

		System.arraycopy( superProjection, 0, mergedProjection, 0, superLength );
		System.arraycopy( childProjection, 0, mergedProjection, superLength, childLength );

		return mergedProjection;
	}
}
