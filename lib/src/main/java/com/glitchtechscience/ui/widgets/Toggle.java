package com.glitchtechscience.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.glitchtechscience.LibraryCore.R;

public class Toggle extends LinearLayout {

	// TODO https://stackoverflow.com/questions/34799622/android-data-binding-with-a-custom-view

	private CompoundButton compoundButton;

	public Toggle( Context context, AttributeSet attrs ) {

		super( context, attrs );

		TypedArray array = context.obtainStyledAttributes( attrs, R.styleable.Toggle, 0, 0 );

		if( android.os.Build.VERSION.SDK_INT >= 14 ) {

			compoundButton = generateUINew( context );
		} else {

			compoundButton = generateUIOld( context );
		}

		setTextOn( array.getString( R.styleable.Toggle_android_textOn ) );
		setTextOff( array.getString( R.styleable.Toggle_android_textOff ) );

		array.recycle();
	}

	private ToggleButton generateUIOld( Context context ) {

		ToggleButton item = new ToggleButton( context );

		addView( item );

		return item;
	}

	@SuppressLint("NewApi")
	private Switch generateUINew( Context context ) {

		Switch item = new Switch( context );

		addView( item );

		return item;
	}

	@SuppressLint("NewApi")
	public void setTextOn( String txt ) {

		if( txt == null ) {

			txt = "On";
		}

		if( android.os.Build.VERSION.SDK_INT >= 14 ) {

			( (Switch) compoundButton ).setTextOn( txt );
		} else {

			( (ToggleButton) compoundButton ).setTextOn( txt );
		}
	}

	@SuppressLint("NewApi")
	public void setTextOff( String txt ) {

		if( txt == null ) {

			txt = "Off";
		}

		if( android.os.Build.VERSION.SDK_INT >= 14 ) {

			( (Switch) compoundButton ).setTextOff( txt );
		} else {

			( (ToggleButton) compoundButton ).setTextOff( txt );
		}
	}

	public boolean isChecked() {

		return compoundButton.isChecked();
	}

	public void setChecked( boolean checked ) {

		compoundButton.setChecked( checked );
	}

	public void setOnClickListener( OnClickListener l ) {

		compoundButton.setOnClickListener( l );
	}

	public void setOnCheckedChangeListener( OnCheckedChangeListener l ) {

		compoundButton.setOnCheckedChangeListener( l );
	}
}
