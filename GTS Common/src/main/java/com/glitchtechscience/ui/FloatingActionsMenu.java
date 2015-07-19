package com.glitchtechscience.ui;

import android.content.Context;
import android.util.AttributeSet;

public class FloatingActionsMenu extends com.getbase.floatingactionbutton.FloatingActionsMenu {

	private OnFloatingActionMenuUpdateListener onFloatingActionMenuUpdateListener;

	public FloatingActionsMenu( Context context ) {

		this( context, null );
	}

	public FloatingActionsMenu( Context context, AttributeSet attrs ) {

		super( context, attrs );
	}

	public FloatingActionsMenu( Context context, AttributeSet attrs, int defStyle ) {

		super( context, attrs, defStyle );
	}

	public void setOnFloatingActionMenuUpdateListener( OnFloatingActionMenuUpdateListener listener ) {

		onFloatingActionMenuUpdateListener = listener;
	}

	@Override
	public void toggle() {

		super.toggle();

		if( onFloatingActionMenuUpdateListener != null ) {

			onFloatingActionMenuUpdateListener.actionMenuToggled();
		}
	}

	@Override
	public void expand() {

		super.expand();

		if( onFloatingActionMenuUpdateListener != null ) {

			onFloatingActionMenuUpdateListener.actionMenuExpanded();
		}
	}

	@Override
	public void collapse() {

		super.collapse();

		if( onFloatingActionMenuUpdateListener != null ) {

			onFloatingActionMenuUpdateListener.actionMenuCollapsed();
		}
	}

	public interface OnFloatingActionMenuUpdateListener {

		public void actionMenuExpanded();

		public void actionMenuCollapsed();

		public void actionMenuToggled();
	}
}
