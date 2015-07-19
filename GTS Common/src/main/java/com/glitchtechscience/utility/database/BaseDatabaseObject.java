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

	/** DatabaseColumn( TEXT ) */
	protected long lastSync;

	/** pendingDeletion INTEGER NOT NULL DEFAULT 0 */
	protected int pendingDeletion = 0;

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

	//////////////////////////////////

	public BaseDatabaseObject() {

	}

	public abstract Context getContext();

	protected android.content.ContentResolver getContentResolver() {

		return getContext().getContentResolver();
	}

	public void load() {

		load( getProjection(), getSelection(), getSelectionArgs(), getSortOrder(), "1" );
	}

	public void load( String[] projection, String selection, String[] selectionArgs, String sortOrder, String limit ) {

		Uri uri = getUri().buildUpon().appendQueryParameter( "limit", limit ).build();

		if( uri != null ) {

			Cursor c = getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );

			setData( c );
		}
	}

	public int save() {

		if( getId() < 0 ) {

			ContentValues data = getData();

			data.remove( getFieldId() );

			return insert( data );
		} else {

			return update();
		}
	}

	public int insert( ContentValues data ) {

		Uri result = getContentResolver().insert( getUri(), data );

		if( result != null ) {

			setId( Long.parseLong( result.getLastPathSegment() ) );
		}

		return 1;
	}

	public int update() {

		int numUpdated = getContentResolver().update( getUri(), getData(), getSelection(), getSelectionArgs() );
		Log.d( "Save", "Count: " + numUpdated );

		return numUpdated;
	}

	public int delete() {

		int numDeleted = getContentResolver().delete( getUri(), getSelection(), getSelectionArgs() );
		Log.d( "Delete", "Count: " + numDeleted );

		return numDeleted;
	}

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

	protected boolean isCVValueLong( ContentValues data, String key ) {

		return ( data.containsKey( key ) && data.getAsLong( key ) != null );
	}

	/**
	 * (Mostly) Abstract methods; must be overridden
	 */

	// TODO add abstract method descriptions
	public abstract long getId();

	protected abstract void setId( long id );

	protected abstract String getFieldId();

	public abstract Uri getUri();

	public ContentValues getData() {

		ContentValues data = new ContentValues();

		data.put( "lastSync", this.getLastSync() );
		data.put( "pendingDeletion", this.getPendingDeletion() ? 1 : 0 );

		return data;
	}

	public void setData( ContentValues data ) {

		if( this.isCVValueLong( data, "lastSync" ) ) {

			this.setLastSync( data.getAsLong( "lastSync" ) );
		}

		if( data.containsKey( "pendingDeletion" ) ) {

			this.setPendingDeletion( 1 == data.getAsInteger( "pendingDeletion" ) );
		}
	}

	public abstract String[] getProjection();

	public abstract String getSelection();

	public abstract String[] getSelectionArgs();

	public abstract String getSortOrder();
}
