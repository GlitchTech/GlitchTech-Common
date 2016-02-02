package com.glitchtechscience.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class FloatingActionMenuBehavior extends CoordinatorLayout.Behavior<FloatingActionsMenu> {

	public FloatingActionMenuBehavior( Context context, AttributeSet attrs ) {

		super( context, attrs );
	}

	@Override
	public boolean layoutDependsOn( CoordinatorLayout parent, FloatingActionsMenu child, View dependency ) {

		return dependency instanceof Snackbar.SnackbarLayout;
	}

	@Override
	public boolean onDependentViewChanged( CoordinatorLayout parent, FloatingActionsMenu child, View dependency ) {

		child.setTranslationY( Math.min( 0, dependency.getTranslationY() - dependency.getHeight() ) );

		return true;
	}
}
