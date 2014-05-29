package com.epicport.glue.ui;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.epicport.glue.GameModeChangeListener;

public abstract class ControlButton extends ImageButton implements
		GameModeChangeListener {

	protected Activity activity;

	protected ControlButton(final Activity activity) {
		super(activity);
		this.activity = activity;

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		int size = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 34, dm));
		int padding = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 8, dm));

		setPadding(padding, padding, padding, padding);
		setScaleType(ImageView.ScaleType.FIT_CENTER);

		layoutParams.width = size;
		layoutParams.height = size;

		setLayoutParams(layoutParams);

		setFocusable(false);
		setClickable(true);
	}

	public boolean dispatch(MotionEvent ev) {
		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		boolean clickAction = action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_UP;

		if (clickAction && getLeft() <= (int) ev.getX()
				&& getRight() > (int) ev.getX() && getTop() <= (int) ev.getY()
				&& getBottom() > (int) ev.getY()) {
			return true;
		}

		return false;
	}

}
