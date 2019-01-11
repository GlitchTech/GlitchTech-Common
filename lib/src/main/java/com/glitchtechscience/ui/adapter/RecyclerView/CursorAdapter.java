package com.glitchtechscience.ui.adapter.RecyclerView;

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

/**
 * Cursor adapter for RecyclerView. Based off of old Cursor Adapter.
 *
 * @param <VH>
 */
public abstract class CursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements Filterable, CursorFilter.CursorFilterClient {

	private Cursor mCursor;
	private boolean mDataValid;
	private int mRowIdColumn;

	private ChangeObserver mChangeObserver;
	private DataSetObserver mDataSetObserver;

	private CursorFilter mCursorFilter;
	private FilterQueryProvider mFilterQueryProvider;

	public CursorAdapter( Cursor cursor ) {

		init( cursor );
	}

	void init( Cursor c ) {

		mCursor = c;

		boolean cursorPresent = c != null;

		mDataValid = cursorPresent;
		mRowIdColumn = cursorPresent ? c.getColumnIndexOrThrow( "_id" ) : -1;

		mChangeObserver = new ChangeObserver();
		mDataSetObserver = new NotifyingDataSetObserver();

		if( cursorPresent ) {

			if( mChangeObserver != null ) {

				c.registerContentObserver( mChangeObserver );
			}

			if( mDataSetObserver != null ) {

				c.registerDataSetObserver( mDataSetObserver );
			}
		}
	}

	/**
	 * Returns the cursor.
	 *
	 * @return Cursor
	 */
	public Cursor getCursor() {

		return mCursor;
	}

	/**
	 * @see android.widget.ListAdapter#getCount()
	 */
	@Override
	public int getItemCount() {

		if( mDataValid && mCursor != null ) {

			return mCursor.getCount();
		}

		return 0;
	}

	/**
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Object getItem( int position ) {

		if( mDataValid && mCursor != null ) {

			mCursor.moveToPosition( position );

			return mCursor;
		}

		return null;
	}

	/**
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	@Override
	public long getItemId( int position ) {

		if( mDataValid && mCursor != null ) {

			if( mCursor.moveToPosition( position ) ) {

				return mCursor.getLong( mRowIdColumn );
			}
		}

		return 0;
	}

	/**
	 * @param hasStableIds
	 * 		boolean
	 */
	@Override
	public void setHasStableIds( boolean hasStableIds ) {

		super.setHasStableIds( true );
	}

	/**
	 * Called by RecyclerView to display the data at the specified position. This method
	 * should update the contents of the ViewHolder#itemView to reflect the item at
	 * the given position.
	 *
	 * @param viewHolder
	 * 		{@inheritDoc}
	 * @param position
	 * 		{@inheritDoc}
	 */
	@Override
	public void onBindViewHolder( VH viewHolder, int position ) {

		if( !mDataValid ) {

			throw new IllegalStateException( "Cursor is not valid." );
		}

		if( !mCursor.moveToPosition( position ) ) {

			throw new IllegalStateException( "Couldn't move cursor to position " + position );
		}

		onBindViewHolder( viewHolder, mCursor );
	}

	/**
	 * Called to display the data in the cursor. This method should update the contents
	 * of the ViewHolder#itemView to reflect the item contained in the cursor.
	 *
	 * @param viewHolder
	 * 		The ViewHolder which should be updated to represent the contents of the
	 * 		item at the given position in the data set.
	 * @param cursor
	 * 		Cursor
	 */
	public abstract void onBindViewHolder( VH viewHolder, Cursor cursor );

	/**
	 * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
	 * closed.
	 *
	 * @param cursor
	 * 		The new cursor to be used
	 */
	public void changeCursor( Cursor cursor ) {

		Cursor old = swapCursor( cursor );

		if( old != null ) {

			old.close();
		}
	}

	/**
	 * Swap in a new Cursor, returning the old Cursor.  Unlike
	 * {@link #changeCursor(Cursor)}, the returned old Cursor is not
	 * closed.
	 *
	 * @param newCursor
	 * 		The new cursor to be used.
	 *
	 * @return Returns the previously set Cursor, or null if there wasa not one.
	 * If the given new Cursor is the same instance is the previously set
	 * Cursor, null is also returned.
	 */
	public Cursor swapCursor( Cursor newCursor ) {

		if( newCursor == mCursor ) {

			return null;
		}

		final Cursor oldCursor = mCursor;
		final int itemCount = getItemCount();

		if( oldCursor != null ) {

			if( mChangeObserver != null ) {

				oldCursor.unregisterContentObserver( mChangeObserver );
			}

			if( mDataSetObserver != null ) {

				oldCursor.unregisterDataSetObserver( mDataSetObserver );
			}
		}

		mCursor = newCursor;

		if( mCursor != null ) {

			if( mChangeObserver != null ) {

				mCursor.registerContentObserver( mChangeObserver );
			}

			if( mDataSetObserver != null ) {

				mCursor.registerDataSetObserver( mDataSetObserver );
			}

			mRowIdColumn = mCursor.getColumnIndexOrThrow( "_id" );
			mDataValid = true;

			// notify the observers about the new cursor
			notifyDataSetChanged();
		} else {

			mRowIdColumn = -1;
			mDataValid = false;

			// notify the observers about the lack of a data set
			// notifyDataSetInvalidated();
			notifyItemRangeRemoved( 0, itemCount );
		}

		return oldCursor;
	}

	/**
	 * Converts the cursor into a CharSequence. Subclasses should override this
	 * method to convert their results. The default implementation returns an
	 * empty String for null values or the default String representation of
	 * the value.
	 *
	 * @param cursor
	 * 		the cursor to convert to a CharSequence
	 *
	 * @return a CharSequence representing the value
	 */
	public CharSequence convertToString( Cursor cursor ) {

		return cursor == null ? "" : cursor.toString();
	}

	/**
	 * Runs a query with the specified constraint. This query is requested
	 * by the filter attached to this adapter.
	 *
	 * The query is provided by a
	 * {@link android.widget.FilterQueryProvider}.
	 * If no provider is specified, the current cursor is not filtered and returned.
	 *
	 * After this method returns the resulting cursor is passed to {@link #changeCursor(Cursor)}
	 * and the previous cursor is closed.
	 *
	 * This method is always executed on a background thread, not on the
	 * application's main thread (or UI thread.)
	 *
	 * Contract: when constraint is null or empty, the original results,
	 * prior to any filtering, must be returned.
	 *
	 * @param constraint
	 * 		the constraint with which the query must be filtered
	 *
	 * @return a Cursor representing the results of the new query
	 *
	 * @see #getFilter()
	 * @see #getFilterQueryProvider()
	 * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
	 */
	public Cursor runQueryOnBackgroundThread( CharSequence constraint ) {

		if( mFilterQueryProvider != null ) {

			return mFilterQueryProvider.runQuery( constraint );
		}

		return mCursor;
	}

	public Filter getFilter() {

		if( mCursorFilter == null ) {

			mCursorFilter = new CursorFilter( this );
		}

		return mCursorFilter;
	}

	/**
	 * Returns the query filter provider used for filtering. When the
	 * provider is null, no filtering occurs.
	 *
	 * @return the current filter query provider or null if it does not exist
	 *
	 * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
	 * @see #runQueryOnBackgroundThread(CharSequence)
	 */
	public FilterQueryProvider getFilterQueryProvider() {

		return mFilterQueryProvider;
	}

	/**
	 * Sets the query filter provider used to filter the current Cursor.
	 * The provider's
	 * {@link android.widget.FilterQueryProvider#runQuery(CharSequence)}
	 * method is invoked when filtering is requested by a client of
	 * this adapter.
	 *
	 * @param filterQueryProvider
	 * 		the filter query provider or null to remove it
	 *
	 * @see #getFilterQueryProvider()
	 * @see #runQueryOnBackgroundThread(CharSequence)
	 */
	public void setFilterQueryProvider( FilterQueryProvider filterQueryProvider ) {

		mFilterQueryProvider = filterQueryProvider;
	}

	/**
	 * Called when the {@link ContentObserver} on the cursor receives a change notification.
	 * Can be implemented by sub-class.
	 *
	 * @see ContentObserver#onChange(boolean)
	 */
	protected void onContentChanged() {

	}

	private class ChangeObserver extends ContentObserver {

		public ChangeObserver() {

			super( new Handler() );
		}

		@Override
		public boolean deliverSelfNotifications() {

			return true;
		}

		@Override
		public void onChange( boolean selfChange ) {

			onContentChanged();
		}
	}

	private class NotifyingDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {

			mDataValid = true;

			notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {

			mDataValid = false;

			notifyItemRangeRemoved( 0, getItemCount() );
		}
	}
}
