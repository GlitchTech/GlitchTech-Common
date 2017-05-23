package com.glitchtechscience.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.glitchtechscience.LibraryCore.R;

public class SwitchBar extends RelativeLayout {

	private TextView label;
	private TextView desc;
	private Switch toggle;

	public SwitchBar( Context context, AttributeSet attrs ) {

		super( context, attrs );

		LayoutInflater.from( context ).inflate( R.layout.gtscommon__ui_widget_switch_bar, this, true );

		TypedArray array = context.obtainStyledAttributes( attrs, R.styleable.SwitchBar, 0, 0 );

		label = (TextView) findViewById( R.id.label );
		setLabel( array.getString( R.styleable.SwitchBar_label ) );

		desc = (TextView) findViewById( R.id.desc );
		setDesc( array.getString( R.styleable.SwitchBar_description ) );

		toggle = (Switch) findViewById( R.id.toggle );
		setTextOn( array.getString( R.styleable.SwitchBar_android_textOn ) );
		setTextOff( array.getString( R.styleable.SwitchBar_android_textOff ) );
		setChecked( array.getBoolean( R.styleable.SwitchBar_android_checked, false ) );

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

	public boolean getChecked() {

		return isChecked();
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
