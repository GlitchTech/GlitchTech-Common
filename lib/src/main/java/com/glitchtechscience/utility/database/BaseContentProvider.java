package com.glitchtechscience.utility.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

public abstract class BaseContentProvider extends ContentProvider {

	public static final String GROUP_BY_KEY = "groupBy";
	public static final String HAVING_KEY = "having";
	public static final String LIMIT_KEY = "limit";

	/* ----- ----- ----- */

	protected abstract SQLiteDatabase getWritableDatabase();

	protected abstract SQLiteDatabase getReadableDatabase();

	/* ----- ----- ----- */

	/**
	 * Insert set of values into designated table. Action performed inside of a transaction.
	 *
	 * @param uri
	 * 		Uri
	 * @param values
	 * 		ContentValues[]
	 *
	 * @return Number of inserts performed
	 */
	@SuppressWarnings("NullableProblems") //Outside of my control
	@Override
	public int bulkInsert( Uri uri, ContentValues[] values ) {

		// Opens the database object in "write" mode.
		SQLiteDatabase db = getWritableDatabase();

		int numOperations = 0;

		// Begin a transaction
		if( db != null ) {

			db.beginTransaction();

			try {

				for( ContentValues cv : values ) {

					if( db.insert( getType( uri ), null, cv ) >= 0 ) {

						numOperations++;
					}
				}

				db.setTransactionSuccessful();

				if( getContext() != null && uri != null ) {

					getContext().getContentResolver().notifyChange( uri, null );
				}
			} finally {

				db.endTransaction();
			}
		}

		return numOperations;
	}

	/**
	 * Performs the work provided in a single transaction
	 *
	 * @param operations
	 * 		ArrayList of ContentProviderOperation to perform
	 *
	 * @return ContentProviderResult[]
	 */
	@SuppressWarnings("NullableProblems") //Outside of my control
	@Override
	public ContentProviderResult[] applyBatch( ArrayList<ContentProviderOperation> operations ) {

		final int numOperations = operations.size();

		ContentProviderResult[] result = new ContentProviderResult[numOperations];

		// Opens the database object in "write" mode.
		SQLiteDatabase db = getWritableDatabase();

		// Begin a transaction
		if( db != null ) {

			db.beginTransaction();

			try {

				for( int i = 0; i < numOperations; i++ ) {

					result[i] = operations.get( i ).apply( this, result, i );
				}

				db.setTransactionSuccessful();
			} catch( OperationApplicationException e ) {

				Log.d( "BaseContentProvider", "batch failed: " + e.getLocalizedMessage() );
			} finally {

				db.endTransaction();
			}
		}

		return result;
	}

	/**
	 * Handle query request to SQLiteDatabase
	 *
	 * @param uri
	 * 		The URI to query. This will be the full URI sent by the client;
	 * 		if the client is requesting a specific record, the URI will end in a record number
	 * 		that the implementation should parse and add to a WHERE or HAVING clause, specifying
	 * 		that _id value.
	 * @param columns
	 * 		The list of columns to put into the cursor. If
	 * 		{@code null} all columns are included.
	 * @param selection
	 * 		A selection criteria to apply when filtering rows.
	 * 		If {@code null} then all rows are included.
	 * @param selectionArgs
	 * 		You may include ?s in selection, which will be replaced by
	 * 		the values from selectionArgs, in order that they appear in the selection.
	 * 		The values will be bound as Strings.
	 * @param sortOrder
	 * 		How the rows in the cursor should be sorted.
	 * 		If {@code null} then the provider is free to define the sort order.
	 *
	 * @return a Cursor or {@code null}.
	 */
	@Override
	public Cursor query( @NonNull Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder ) {

		SQLiteDatabase db = getReadableDatabase();

		if( db != null ) {

			//Default functionality
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

			queryBuilder.setTables( getType( uri ) );

			Cursor generalCursor = queryBuilder.query( db, columns, selection, selectionArgs, uri.getQueryParameter( GROUP_BY_KEY ), uri.getQueryParameter( HAVING_KEY ), sortOrder, uri.getQueryParameter( LIMIT_KEY ) );

			if( generalCursor != null && getContext() != null ) {

				generalCursor.setNotificationUri( getContext().getContentResolver(), uri );
			}

			return generalCursor;
		}

		return null;
	}

	/**
	 * Insert a row into the SQLiteDatabase.
	 *
	 * @param uri
	 * 		The content:// URI of the insertion request. This must not be null.
	 * @param values
	 * 		A set of column_name/value pairs to add to the database.
	 * 		This must not be {@code null}.
	 *
	 * @return The URI for the newly inserted item.
	 */
	@Override
	public Uri insert( @NonNull Uri uri, ContentValues values ) {

		SQLiteDatabase db = getWritableDatabase();

		if( db != null ) {

			long id = db.insert( getType( uri ), null, values );

			if( id >= 0 ) {

				Uri resultUri = uri.buildUpon().appendPath( Long.toString( id ) ).build();

				if( getContext() != null && resultUri != null ) {

					getContext().getContentResolver().notifyChange( resultUri, null );
				}

				return resultUri;
			}
		}

		return Uri.parse( uri.toString() );
	}

	/**
	 * Update a SQLiteDatabase table.
	 *
	 * @param uri
	 * 		The URI to query. This can potentially have a record ID if this
	 * 		is an update request for a specific record.
	 * @param values
	 * 		A set of column_name/value pairs to update in the database.
	 * 		This must not be {@code null}.
	 * @param selection
	 * 		An optional filter to match rows to update.
	 * @param selectionArgs
	 * 		You may include ?s in selection, which will be replaced by
	 * 		the values from selectionArgs, in order that they appear in the selection.
	 * 		The values will be bound as Strings.
	 *
	 * @return the number of rows affected.
	 */
	@Override
	public int update( @NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs ) {

		SQLiteDatabase db = getWritableDatabase();

		if( db != null ) {

			int rows = db.update( getType( uri ), values, selection, selectionArgs );

			if( rows > 0 && getContext() != null ) {

				getContext().getContentResolver().notifyChange( uri, null );
			}

			return rows;
		}

		return 0;
	}

	/**
	 * Delete from a SQLiteDatabase table.
	 *
	 * @param uri
	 * 		The full URI to query, including a row ID (if a specific record is requested).
	 * @param selection
	 * 		An optional restriction to apply to rows when deleting.
	 * @param selectionArgs
	 * 		You may include ?s in selection, which will be replaced by
	 * 		the values from selectionArgs, in order that they appear in the selection.
	 * 		The values will be bound as Strings.
	 *
	 * @return The number of rows affected.
	 */
	@Override
	public int delete( @NonNull Uri uri, String selection, String[] selectionArgs ) {

		SQLiteDatabase db = getWritableDatabase();

		if( db != null ) {

			int rows = db.delete( getType( uri ), selection, selectionArgs );

			if( rows > 0 && getContext() != null ) {

				getContext().getContentResolver().notifyChange( uri, null );
			}

			return rows;
		}

		return 0;
	}
}
