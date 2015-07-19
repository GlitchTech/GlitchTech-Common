package com.glitchtechscience.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.glitchtechscience.LibraryCore.R;

/**
 * UI Widget for Undobar
 *
 * This activity is used to display different layout resources for a tutorial on user interface design.
 *
 * @author Matt Schott [GlitchTechScience@gmail.com]
 * @version 2014.12.16
 * @since 1.0
 */
public class UndoBar extends LinearLayout implements View.OnClickListener {

	// Animation holder
	YoYo.YoYoString lastAnimation;

	// Local storage of animation timer
	private int mDuration;
	private int mDelay;

	// Bound view for the message
	private TextView messageView;

	// Objects to handle delay action
	private Handler mHideHandler = new Handler();
	private Runnable mHideRunnable = new Runnable() {

		@Override
		public void run() {

			hide( false );
		}
	};

	// Handler for finishing actions
	private onUndoBarStateChanged stateChangedListener;
	private boolean listenerFired;

	// State objects from Controller
	private Bundle actionCompleteToken;

	public UndoBar( Context context ) {

		this( context, null );
	}

	public UndoBar( Context context, AttributeSet attrs ) {

		super( context, attrs );

		LayoutInflater.from( context )
				.inflate( R.layout.ui_widget_undobar, this, true );

		// Bind message view
		messageView = (TextView) findViewById( R.id.ui_widget_undobar_message );

		// Parse attributes
		TypedArray array = context.obtainStyledAttributes( attrs, R.styleable.UndoBar, 0, 0 );

		setDelay( array.getInteger( R.styleable.UndoBar_delay, ( 5 * 1000 ) ) );//5 sec default in millis
		setDuration( array.getInteger( R.styleable.UndoBar_duration, getResources().getInteger( android.R.integer.config_shortAnimTime ) ) );
		setMessage( array.getString( R.styleable.UndoBar_label ) );
		setStateChangedListener( null );

		lastAnimation = null;

		array.recycle();

		// Bind click listener
		findViewById( R.id.ui_widget_undobar ).setOnClickListener( this );

		// Hide self from view
		setVisibility( View.GONE );
	}

	/**
	 * Set the delay before the undobar hides itself.
	 *
	 * @param delay
	 * 		How long before bar hides itself.
	 */
	public void setDelay( int delay ) {

		mDelay = delay;
	}

	/**
	 * Set the duration of the animation.
	 *
	 * @param duration
	 * 		How long does the animation run.
	 */
	public void setDuration( int duration ) {

		mDuration = duration;
	}

	/**
	 * Set the display message on the bar.
	 *
	 * @param message
	 * 		Text to display.
	 */
	public void setMessage( String message ) {

		if( message == null || message.length() <= 0 ) {

			message = getResources().getString( R.string.undo );
		}

		messageView.setText( message );
	}

	/**
	 * Set the state change listener to handle when action is Undone or Released
	 *
	 * @param listener
	 * 		onUndoBarStateChanged Listener
	 */
	public void setStateChangedListener( onUndoBarStateChanged listener ) {

		stateChangedListener = listener;
	}

	/**
	 * Handle click event on bar.
	 *
	 * @param view
	 * 		View which was clicked
	 */
	@Override
	public void onClick( View view ) {

		if( !listenerFired && stateChangedListener != null ) {

			stateChangedListener.onUndo( actionCompleteToken );
		}

		actionCompleteToken = null;
		listenerFired = true;

		hide();
	}

	/**
	 * Show the undobar. Returns bundle in onUndo or onRelease.
	 *
	 * @param actionToken
	 * 		Bundle of information.
	 */
	public void show( Bundle actionToken ) {

		show( actionToken, false );
	}

	/**
	 * Show the undobar. Returns bundle in onUndo or onRelease.
	 *
	 * @param actionToken
	 * 		Bundle of information.
	 * @param message
	 * 		Message to display on the undobar
	 */
	public void show( Bundle actionToken, String message ) {

		setMessage( message );

		show( actionToken, false );
	}

	/**
	 * Show the undobar. Returns bundle in onUndo or onRelease.
	 *
	 * @param actionToken
	 * 		Bundle of information.
	 * @param message
	 * 		Message to display on the undobar
	 * @param immediate
	 * 		Set true to skip show animation
	 */
	public void show( Bundle actionToken, String message, boolean immediate ) {

		setMessage( message );

		show( actionToken, immediate );
	}

	/**
	 * Show the undobar. Returns bundle in onUndo or onRelease.
	 *
	 * @param actionToken
	 * 		Bundle of information.
	 * @param immediate
	 * 		Set true to skip show animation
	 */
	public void show( Bundle actionToken, boolean immediate ) {

		listenerFired = false;
		actionCompleteToken = actionToken;

		mHideHandler.removeCallbacks( mHideRunnable );
		mHideHandler.postDelayed( mHideRunnable, mDelay );

		setVisibility( View.VISIBLE );

		// Cancel all active or pending animations
		if( lastAnimation != null ) {

			lastAnimation.stop( false );
		}

		// Animate view in
		YoYo.with( Techniques.SlideInUp )
				.duration( immediate ? 0 : mDuration )
				.interpolate( new AccelerateDecelerateInterpolator() )
				.playOn( this );
	}

	/**
	 * Hides undo bar.
	 */
	public void hide() {

		hide( false );
	}

	/**
	 * Hides the undobar.
	 *
	 * @param immediate
	 * 		Set true to skip hide animation
	 */
	public void hide( boolean immediate ) {

		mHideHandler.removeCallbacks( mHideRunnable );

		// Cancel all active or pending animations
		if( lastAnimation != null ) {

			lastAnimation.stop( false );
		}

		// Animate view in
		YoYo.with( Techniques.SlideOutDown )
				.duration( immediate ? 0 : mDuration )
				.interpolate( new AccelerateDecelerateInterpolator() )
				.withListener( new com.nineoldandroids.animation.Animator.AnimatorListener() {

					@Override
					public void onAnimationStart( com.nineoldandroids.animation.Animator animation ) {

					}

					@Override
					public void onAnimationEnd( com.nineoldandroids.animation.Animator animation ) {

						setVisibility( View.GONE );

						if( !listenerFired && stateChangedListener != null ) {

							stateChangedListener.onReleased( actionCompleteToken );
						}

						listenerFired = true;
						actionCompleteToken = null;
					}

					@Override
					public void onAnimationCancel( com.nineoldandroids.animation.Animator animation ) {

					}

					@Override
					public void onAnimationRepeat( com.nineoldandroids.animation.Animator animation ) {

					}
				} )
				.playOn( this );
	}

	public interface onUndoBarStateChanged {

		void onUndo( Bundle token );

		void onReleased( Bundle token );
	}
}
