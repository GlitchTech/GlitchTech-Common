package com.glitchtechscience.ui.behavior;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class FloatingActionButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

	public FloatingActionButtonBehavior( Context context, AttributeSet attrs ) {

		super( context, attrs );
	}

	@Override
	public boolean layoutDependsOn( CoordinatorLayout parent, FloatingActionButton child, View dependency ) {

		return dependency instanceof Snackbar.SnackbarLayout;
	}

	@Override
	public boolean onDependentViewChanged( CoordinatorLayout parent, FloatingActionButton child, View dependency ) {

		child.setTranslationY( Math.min( 0, dependency.getTranslationY() - dependency.getHeight() ) );

		return true;
	}
}
