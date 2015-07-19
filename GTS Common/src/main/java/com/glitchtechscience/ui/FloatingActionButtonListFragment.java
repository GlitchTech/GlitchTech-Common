package com.glitchtechscience.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.glitchtechscience.LibraryCore.R;
import com.glitchtechscience.ui.widgets.UndoBar;

import java.util.ArrayList;

/**
 * Abstract class to assist with FloatingActionButton in the list fragment.
 */
public abstract class FloatingActionButtonListFragment extends ListFragment implements AdapterView.OnItemLongClickListener, ActionMode.Callback {

	private ActionMode mActionMode;
	private ListView mListView;

	protected FloatingActionButton floatingActionButton;
	protected UndoBar undobar;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

		View rootView = inflater.inflate( R.layout.ui_fab_list_fragment, container, false );

		if( rootView == null ) {

			throw new AssertionError( "rootView cannot be null" );
		}

		floatingActionButton = (FloatingActionButton) rootView.findViewById( R.id.floating_action_button );
		undobar = (UndoBar) rootView.findViewById( R.id.undobar );

		return rootView;
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {

		super.onActivityCreated( savedInstanceState );

		// bind mListView
		mListView = getListView();

		// bind long click for action mode
		mListView.setOnItemLongClickListener( this );
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

	/**
	 * List item clicked.
	 *
	 * @param l
	 * 		The ListView where the click happened
	 * @param view
	 * 		The view within the AbsListView that was clicked
	 * @param position
	 * 		The position of the view in the list
	 * @param id
	 * 		The row id of the item that was clicked
	 */
	@Override
	public void onListItemClick( ListView l, View view, int position, long id ) {

		if( mActionMode != null ) {
			// invalidate title and menus

			mActionMode.invalidate();
		} else {

			viewAction( position );
		}
	}

	/**
	 * List item clicked and held.
	 *
	 * @param parent
	 * 		The AbsListView where the click happened
	 * @param view
	 * 		The view within the AbsListView that was clicked
	 * @param position
	 * 		The position of the view in the list
	 * @param id
	 * 		The row id of the item that was clicked
	 *
	 * @return true if the callback consumed the long click, false otherwise
	 */
	@Override
	public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {

		// if actionmode is null "not started"
		if( mActionMode != null ) {

			return false;
		}

		// Set to multiple selection mode
		mListView.setChoiceMode( AbsListView.CHOICE_MODE_MULTIPLE );
		mListView.setItemChecked( position, true );

		// Start the CAB
		mActionMode = ( (AppCompatActivity) getActivity() ).startSupportActionMode( this );

		return true;
	}

	/**
	 * Get currently checked items in the List.
	 *
	 * @return int[]
	 */
	protected int[] getCheckedPositions() {

		SparseBooleanArray checked = mListView.getCheckedItemPositions();
		ArrayList<Integer> checkedPositionList = new ArrayList<>();

		for( int i = 0; i < checked.size(); i++ ) {

			final int index = checked.keyAt( i );

			if( checked.get( index ) ) {

				checkedPositionList.add( index );
			}
		}

		int[] checkedPositions = new int[checkedPositionList.size()];

		for( int i = 0; i < checkedPositionList.size(); i++ ) {

			checkedPositions[i] = checkedPositionList.get( i );
		}

		return checkedPositions;
	}

	/**
	 * Called when action mode is first created. The menu supplied will be used to
	 * generate action buttons for the action mode.
	 *
	 * @param mode
	 * 		ActionMode being created
	 * @param menu
	 * 		Menu used to populate action buttons
	 *
	 * @return true if the action mode should be created, false if entering this
	 * mode should be aborted.
	 */
	@Override
	public boolean onCreateActionMode( ActionMode mode, Menu menu ) {

		buildActionMenu( mode, menu );

		return true;
	}

	/**
	 * Called to refresh an action mode's action menu whenever it is invalidated.
	 *
	 * @param mode
	 * 		ActionMode being prepared
	 * @param menu
	 * 		Menu used to populate action buttons
	 *
	 * @return true if the menu or action mode was updated, false otherwise.
	 */
	@Override
	public boolean onPrepareActionMode( ActionMode mode, Menu menu ) {

		buildActionMenu( mode, menu );

		return true;
	}

	/**
	 * Build the Action Menu
	 *
	 * @param mode
	 * 		ActionMode
	 * @param menu
	 * 		Menu
	 */
	private void buildActionMenu( ActionMode mode, Menu menu ) {

		// Get checked count
		final int checked = mListView.getCheckedItemCount();

		// Empty menu
		menu.clear();

		// Fetch inflater
		MenuInflater inflater = mode.getMenuInflater();

		// update title with number of checked items
		mode.setTitle( Integer.toString( checked ) );

		// update menu based on number of items selected
		switch( checked ) {
			case 0:
				// nothing checked - exit action mode
				mode.finish();
				break;
			case 1:
				// single item
				inflater.inflate( getSingleActionMenu(), menu );
				break;
			default:
				// multiple items
				inflater.inflate( getMultipleActionMenu(), menu );
				break;
		}
	}

	/**
	 * Return menu resource for single selected item.
	 *
	 * @return menu resource
	 */
	public int getSingleActionMenu() {

		return R.menu.general_action_mode_single;
	}

	/**
	 * Return menu resource for multiple selected items.
	 *
	 * @return menu resource
	 */
	public int getMultipleActionMenu() {

		return R.menu.general_action_mode_multiple;
	}

	/**
	 * Called to report a user click on an action button.
	 *
	 * @param mode
	 * 		The current ActionMode
	 * @param item
	 * 		The item that was clicked
	 *
	 * @return true if this callback handled the event, false if the standard MenuItem
	 * invocation should continue.
	 */
	@Override
	public boolean onActionItemClicked( ActionMode mode, MenuItem item ) {

		if( item.getItemId() == R.id.action_delete ) {

			if( deleteAction( getCheckedPositions() ) ) {

				mode.finish();
			}

			return true;
		} else if( item.getItemId() == R.id.action_modify ) {

			int[] checked = getCheckedPositions();

			if( modifyAction( checked.length > 0 ? checked[0] : -1 ) ) {

				mode.finish();
			}

			return true;
		} else {

			return false;
		}
	}

	/**
	 * Called when an action mode is about to be exited and destroyed.
	 *
	 * @param mode
	 * 		The current ActionMode being destroyed
	 */
	@Override
	public void onDestroyActionMode( ActionMode mode ) {

		mListView.clearChoices();

		for( int i = 0; i < mListView.getCount(); i++ ) {

			mListView.setItemChecked( i, false );
		}

		mListView.post( new Runnable() {

			@Override
			public void run() {

				mListView.setChoiceMode( ListView.CHOICE_MODE_NONE );
			}
		} );

		mActionMode = null;
	}

	/**
	 * Terminate Action Mode
	 */
	public void finishActionMode() {

		if( mActionMode != null ) {

			mActionMode.finish();
		}
	}

	/**
	 * Perform view action on checked item.
	 *
	 * @param position
	 * 		int
	 *
	 * @return True if action completed
	 */
	protected abstract boolean viewAction( int position );

	/**
	 * Perform delete action on checked item(s).
	 *
	 * @param checkedPositions
	 * 		int[]
	 *
	 * @return True if action completed
	 */
	protected abstract boolean deleteAction( int[] checkedPositions );

	/**
	 * Perform modify action on checked item.
	 *
	 * @param position
	 * 		int
	 *
	 * @return True if action completed
	 */
	protected abstract boolean modifyAction( int position );
}
