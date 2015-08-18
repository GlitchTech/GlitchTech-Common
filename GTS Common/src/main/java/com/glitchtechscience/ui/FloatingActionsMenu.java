package com.glitchtechscience.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class FloatingActionsMenu extends com.getbase.floatingactionbutton.FloatingActionsMenu {

	// Masking scrim
	private View scrim;

	// Local storage of animation timer
	private int mShortAnimationDuration;

	// Action callback
	private OnFloatingActionMenuUpdateListener onFloatingActionMenuUpdateListener;

	public FloatingActionsMenu( Context context ) {

		this( context, null );
	}

	public FloatingActionsMenu( Context context, AttributeSet attrs ) {

		super( context, attrs );

		init();
	}

	public FloatingActionsMenu( Context context, AttributeSet attrs, int defStyle ) {

		super( context, attrs, defStyle );

		init();
	}

	private void init() {

		// Retrieve and cache the system's default "short" animation time.
		setAnimationDuration( getResources().getInteger( android.R.integer.config_shortAnimTime ) );
	}

	public void setOnFloatingActionMenuUpdateListener( OnFloatingActionMenuUpdateListener listener ) {

		onFloatingActionMenuUpdateListener = listener;
	}

	public void setScrim( View view ) {

		scrim = view;

		scrim.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick( View v ) {

				collapse();
			}
		} );
	}

	public void setAnimationDuration( int duration ) {

		mShortAnimationDuration = duration;
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

		fadeInScrim();

		if( onFloatingActionMenuUpdateListener != null ) {

			onFloatingActionMenuUpdateListener.actionMenuExpanded();
		}
	}

	@Override
	public void collapse() {

		super.collapse();

		fadeOutScrim();

		if( onFloatingActionMenuUpdateListener != null ) {

			onFloatingActionMenuUpdateListener.actionMenuCollapsed();
		}
	}

	private void fadeInScrim() {

		if( scrim != null ) {

			// Set content transparent
			scrim.setAlpha( 0f );
			// Set content visible
			scrim.setVisibility( View.VISIBLE );

			// Animate to full solid
			scrim.animate().alpha( 1f ).setInterpolator( new AccelerateDecelerateInterpolator() ).setDuration( mShortAnimationDuration ).setListener( null );
		}
	}

	private void fadeOutScrim() {

		if( scrim != null ) {

			// Animate to transparent, set to GONE when done
			scrim.animate().alpha( 0f ).setInterpolator( new AccelerateDecelerateInterpolator() ).setDuration( mShortAnimationDuration ).setListener( new AnimatorListenerAdapter() {

				@Override
				public void onAnimationEnd( Animator animation ) {

					scrim.setVisibility( View.GONE );
				}
			} );
		}
	}

	public interface OnFloatingActionMenuUpdateListener {

		public void actionMenuExpanded();

		public void actionMenuCollapsed();

		public void actionMenuToggled();
	}
}
