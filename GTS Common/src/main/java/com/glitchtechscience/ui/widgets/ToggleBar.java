package com.glitchtechscience.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glitchtechscience.LibraryCore.R;

public class ToggleBar extends RelativeLayout {

	private TextView label;
	private TextView desc;
	private Toggle toggle;

	public ToggleBar( Context context, AttributeSet attrs ) {

		super( context, attrs );

		LayoutInflater.from( context ).inflate( R.layout.ui_widget_toggle_bar, this, true );

		TypedArray array = context.obtainStyledAttributes( attrs, R.styleable.ToggleBar, 0, 0 );

		label = (TextView) findViewById( R.id.label );
		setLabel( array.getString( R.styleable.ToggleBar_label ) );

		desc = (TextView) findViewById( R.id.desc );
		setDesc( array.getString( R.styleable.ToggleBar_description ) );

		toggle = (Toggle) findViewById( R.id.toggle );
		setTextOn( array.getString( R.styleable.ToggleBar_android_textOn ) );
		setTextOff( array.getString( R.styleable.ToggleBar_android_textOff ) );
		setChecked( array.getBoolean( R.styleable.ToggleBar_android_checked, false ) );

		array.recycle();
	}

	public void setLabel( String text ) {

		if( text == null ) {

			text = "Label";
		}

		label.setText( text );
	}

	public void setDesc( String text ) {

		if( text == null ) {

			text = "";
		}

		desc.setText( text );
		desc.setVisibility( text.length() <= 0 ? View.GONE : View.VISIBLE );
	}

	public void setTextOn( String txt ) {

		toggle.setTextOn( txt );
	}

	public void setTextOff( String txt ) {

		toggle.setTextOff( txt );
	}

	public boolean isChecked() {

		return toggle.isChecked();
	}

	public void setChecked( boolean checked ) {

		toggle.setChecked( checked );
	}

	public void setOnClickListener( OnClickListener l ) {

		toggle.setOnClickListener( l );
	}

	public void setOnCheckedChangeListener( OnCheckedChangeListener l ) {

		toggle.setOnCheckedChangeListener( l );
	}
}
