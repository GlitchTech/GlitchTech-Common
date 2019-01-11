package com.glitchtechscience.ui;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

	protected Toolbar toolbar;
	protected ActionBar actionbar;

	private int toolbarLeft = 0;
	private int toolbarRight = 0;

	protected abstract int getLayoutResource();
	protected abstract int getToolbarId();

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );
		setContentView( getLayoutResource() );

		toolbar = (Toolbar) findViewById( getToolbarId() );

		if( toolbar != null ) {

			setSupportActionBar( toolbar );

			actionbar = getSupportActionBar();

			toolbarLeft = toolbar.getContentInsetLeft();
			toolbarRight = toolbar.getContentInsetRight();

			showActionBar();
		}
	}

	protected void setActionBarIcon( int iconRes ) {

		toolbar.setNavigationIcon( iconRes );
	}

	protected void hideActionBar() {

		if( actionbar != null ) {

			actionbar.setDisplayHomeAsUpEnabled( false );
			toolbar.setContentInsetsAbsolute( 0, 0 );
		}
	}

	protected void showActionBar() {

		if( actionbar != null ) {

			actionbar.setDisplayHomeAsUpEnabled( true );
			toolbar.setContentInsetsAbsolute( toolbarLeft, toolbarRight );
		}
	}
}
