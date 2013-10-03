package com.gamesinjs.dune2;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gamesinjs.dune2.game.GameMode;
import com.gamesinjs.dune2.game.GameModeChangeListener;

public class ControlButton extends ImageButton implements
		GameModeChangeListener {

	private static ControlButton instance = null;

	private final Activity activity;

	public static void createFor(FrameLayout layout, Activity activity,
			BillingThread billingThread) {
		instance = new ControlButton(activity, billingThread);
		layout.addView(instance);
	}

	private final DonateClickListener donateClickListener;
	private final ReinforcementClickListener reinforcementClickListener;

	private ControlButton(final Activity activity,
			final BillingThread billingThread) {
		super(activity);
		this.activity = activity;
		this.donateClickListener = new DonateClickListener(activity,
				billingThread);
		this.reinforcementClickListener = new ReinforcementClickListener(
				activity, billingThread);

		LayoutParams layoutParams = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
						| Gravity.TOP);

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

		setId(1);
		setFocusable(false);
		setImageResource(R.drawable.billing);
		setClickable(true);
		setOnClickListener(donateClickListener);

		GameMode.setGameModeChangeListener(this);
	}

	public static boolean dispatch(MotionEvent ev) {
		if (instance == null) {
			return false;
		}

		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		boolean clickAction = action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_UP;

		if (clickAction && instance.getLeft() <= (int) ev.getX()
				&& instance.getRight() > (int) ev.getX()
				&& instance.getTop() <= (int) ev.getY()
				&& instance.getBottom() > (int) ev.getY()) {
			return true;
		}

		return false;
	}

	@Override
	public void onGameModeChanged(final int gameMode) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (gameMode == GameMode.GM_MENU) {
					setImageResource(R.drawable.billing);
					setOnClickListener(donateClickListener);
					ControlButton.this.setVisibility(View.VISIBLE);
				} else if (gameMode == GameMode.GM_MAP) {
					switch (GameMode.playerHouse()) {
						case GameMode.HOUSE_ATREIDES:
							setImageResource(R.drawable.sonic_tank);
							break;
	
						case GameMode.HOUSE_ORDOS:
							setImageResource(R.drawable.deviator);
							break;
	
						case GameMode.HOUSE_HARKONNEN:
							setImageResource(R.drawable.devastator);
							break;
							
						default:
							setImageResource(R.drawable.siege_tank);
					}

					setOnClickListener(reinforcementClickListener);
					ControlButton.this.setVisibility(View.VISIBLE);
				} else {
					ControlButton.this.setVisibility(View.GONE);
				}
			}
		});
	}

}
