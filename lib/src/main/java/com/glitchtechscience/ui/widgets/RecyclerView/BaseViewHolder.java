package com.glitchtechscience.ui.widgets.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

	private ViewHolderListener mListener;

	public BaseViewHolder( View view ) {

		this( view, null );
	}

	public BaseViewHolder( View view, ViewHolderListener listener ) {

		super( view );

		this.setListener( listener );

		view.setOnClickListener( this );
		view.setOnLongClickListener( this );
	}

	/**
	 * Set ViewHolderListener
	 *
	 * @param vhl
	 * 		ViewHolderListener
	 */
	public void setListener( ViewHolderListener vhl ) {

		mListener = vhl;
	}

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v
	 * 		The view that was clicked.
	 */
	@Override
	public void onClick( View v ) {

		if( mListener != null ) {

			mListener.onClick( v, this.getAdapterPosition() );
		}
	}

	/**
	 * Called when a view has been clicked and held.
	 *
	 * @param v
	 * 		The view that was clicked and held.
	 *
	 * @return true if the callback consumed the long click, false otherwise.
	 */
	@Override
	public boolean onLongClick( View v ) {

		if( mListener != null ) {

			mListener.onLongClick( v, this.getAdapterPosition() );
			return true;
		}

		return false;
	}

	public interface ViewHolderListener {

		void onClick( View view, int position );

		void onLongClick( View view, int position );
	}
}
