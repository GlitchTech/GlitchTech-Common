package com.glitchtechscience.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.glitchtechscience.LibraryCore.R;

/**
 * Abstract class to assist with FloatingActionButton in the list fragment.
 */
public abstract class FloatingActionButtonListFragment extends ListFragment {

	protected FloatingActionButton floatingActionButton;

	@Override
	protected int getLayout() {

		return R.layout.ui_fab_list_fragment;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

		View rootView = super.onCreateView( inflater, container, savedInstanceState );

		if( rootView == null ) {

			throw new AssertionError( "rootView cannot be null" );
		}

		floatingActionButton = (FloatingActionButton) rootView.findViewById( R.id.floating_action_button );

		return rootView;
	}

	/**
	 * Modify the color scheme of the Floating Action Button
	 *
	 * @param normal
	 * 		Default resource color
	 * @param pressed
	 * 		Pressed resource color
	 */
	protected void setButtonColorResource( int normal, int pressed ) {

		floatingActionButton.setColorNormalResId( normal );
		floatingActionButton.setColorPressedResId( pressed );
	}
}
