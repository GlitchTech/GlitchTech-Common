package com.glitchtechscience.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;

public abstract class DrawerActivity extends BaseActivity {

	protected DrawerLayout drawer;
	protected ActionBarDrawerToggle drawerToggle;

	protected abstract int getDrawerResource();

	protected abstract int getDrawerOpenResource();

	protected abstract int getDrawerClosedResource();

	@Override
	protected void onCreate( Bundle savedInstanceState ) {

		super.onCreate( savedInstanceState );

		// Construct the drawer
		drawer = (DrawerLayout) findViewById( getDrawerResource() );
		drawerToggle = new ActionBarDrawerToggle( this, drawer, toolbar, getDrawerOpenResource(), getDrawerClosedResource() );
		drawer.setDrawerListener( drawerToggle );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		if( drawerToggle.onOptionsItemSelected( item ) ) {

			return true;
		}

		switch( item.getItemId() ) {
			case android.R.id.home:
				drawer.openDrawer( Gravity.START );
				return true;
		}

		return super.onOptionsItemSelected( item );
	}

	@Override
	protected void onPostCreate( Bundle savedInstanceState ) {

		super.onPostCreate( savedInstanceState );
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig ) {

		super.onConfigurationChanged( newConfig );
		drawerToggle.onConfigurationChanged( newConfig );
	}

	@Override
	public void onBackPressed() {

		if( drawer.isDrawerOpen( Gravity.START ) ) {

			drawer.closeDrawers();
			return;
		}

		super.onBackPressed();
	}
}
