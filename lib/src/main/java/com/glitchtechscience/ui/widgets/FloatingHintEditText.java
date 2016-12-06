package com.glitchtechscience.ui.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.glitchtechscience.LibraryCore.R;

/**
 * @deprecated Use android.support.design.widget.TextInputLayout
 */
public class FloatingHintEditText extends EditText {

	private final Paint hintPaint = new Paint();
	private ColorStateList hintColors;

	// Animation specs
	private float hintScale;
	private int animationSteps;

	// Animation holders
	private boolean valueWasEmpty;
	private int animationFrame;
	private Animation animation = Animation.NONE;

	public FloatingHintEditText( Context context ) {

		super( context );

		init( context, null );
	}

	public FloatingHintEditText( Context context, AttributeSet attrs ) {

		super( context, attrs );

		setPadding( getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom() );

		init( context, attrs );
	}

	public FloatingHintEditText( Context context, AttributeSet attrs, int defStyle ) {

		super( context, attrs, defStyle );

		setPadding( getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom() );

		init( context, attrs );
	}

	private void init( Context context, AttributeSet attrs ) {

		// Get animation parameters
		TypedArray array = context.obtainStyledAttributes( attrs, R.styleable.floatingHintEditText, 0, 0 );

		hintScale = array.getFloat( R.styleable.floatingHintEditText_scale, (float) 0.6 );
		animationSteps = array.getInt( R.styleable.floatingHintEditText_steps, 6 );

		// Recycle array
		array.recycle();

		hintColors = getHintTextColors();
		valueWasEmpty = TextUtils.isEmpty( getText() );
	}

	@Override
	public int getCompoundPaddingTop() {

		final FontMetricsInt metrics = getPaint().getFontMetricsInt();
		final int floatingHintHeight = (int) ( ( metrics.bottom - metrics.top ) * hintScale );
		return super.getCompoundPaddingTop() + floatingHintHeight;
	}

	@Override
	protected void onTextChanged( CharSequence text, int start, int lengthBefore, int lengthAfter ) {

		super.onTextChanged( text, start, lengthBefore, lengthAfter );

		final boolean valueIsEmpty = TextUtils.isEmpty( getText() );

		// The empty state hasn't changed, so the hint stays the same.
		if( valueWasEmpty == valueIsEmpty ) {

			return;
		}

		valueWasEmpty = valueIsEmpty;

		// Don't animate if we aren't visible.
		if( !isShown() ) {

			return;
		}

		if( valueIsEmpty ) {

			animation = Animation.GROW;
			setHintTextColor( Color.TRANSPARENT );
		} else {

			animation = Animation.SHRINK;
		}
	}

	@Override
	protected void onDraw( @NonNull Canvas canvas ) {

		super.onDraw( canvas );

		if( TextUtils.isEmpty( getHint() ) ) {

			return;
		}

		final boolean isAnimating = ( animation != Animation.NONE );

		// The large hint is drawn by Android, so do nothing.
		if( !isAnimating && TextUtils.isEmpty( getText() ) ) {

			return;
		}

		hintPaint.set( getPaint() );
		hintPaint.setColor( hintColors.getColorForState( getDrawableState(), hintColors.getDefaultColor() ) );

		final float hintPosX = getCompoundPaddingLeft() + getScrollX();
		final float normalHintPosY = getBaseline();
		final float floatingHintPosY = normalHintPosY + getPaint().getFontMetricsInt().top + getScrollY();
		final float normalHintSize = getTextSize();
		final float floatingHintSize = normalHintSize * hintScale;

		// If we're not animating, we're showing the floating hint, so draw it and bail.
		if( !isAnimating ) {

			hintPaint.setTextSize( floatingHintSize );
			canvas.drawText( getHint().toString(), hintPosX, floatingHintPosY, hintPaint );
			return;
		}

		if( animation == Animation.SHRINK ) {

			drawKeyFrame( canvas, normalHintSize, floatingHintSize, hintPosX, normalHintPosY, floatingHintPosY );
		} else {

			drawKeyFrame( canvas, floatingHintSize, normalHintSize, hintPosX, floatingHintPosY, normalHintPosY );
		}

		animationFrame++;

		if( animationFrame == animationSteps ) {

			if( animation == Animation.GROW ) {

				setHintTextColor( hintColors );
			}

			animation = Animation.NONE;
			animationFrame = 0;
		}

		invalidate();
	}

	private void drawKeyFrame( Canvas canvas, float fromSize, float toSize, float hintPosX, float fromY, float toY ) {

		final float textSize = calculateShift( fromSize, toSize );
		final float hintPosY = calculateShift( fromY, toY );

		hintPaint.setTextSize( textSize );

		canvas.drawText( getHint().toString(), hintPosX, hintPosY, hintPaint );
	}

	private float calculateShift( float from, float to ) {

		final float alpha = (float) animationFrame / ( animationSteps - 1 );
		return from * ( 1 - alpha ) + to * alpha;
	}

	private static enum Animation {NONE, SHRINK, GROW}
}
