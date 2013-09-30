package com.gamesinjs.dune2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.gamesinjs.dune2.game.GameMode;
import com.gamesinjs.dune2.game.GameModeChangeListener;

public class ControlButton extends ImageButton implements
		GameModeChangeListener {

	private static ControlButton instance = null;

	private final static String DONATE_1 = "android.test.purchased";
	//private final static String DONATE_1 	= "donate_1";
	private final static String DONATE_5 	= "donate_5";
	private final static String DONATE_15 	= "donate_15";
	//private final static String DONATE_25 = "donate_25";

	private final Activity activity;

	public static void createFor(FrameLayout layout, Activity activity,
			BillingThread billingThread) {
		instance = new ControlButton(activity, billingThread);
		layout.addView(instance);
	}

	private ControlButton(final Activity activity,
			final BillingThread billingThread) {
		super(activity);
		this.activity = activity;

		setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
						| Gravity.TOP));
		setImageResource(R.drawable.billing);

		setFocusable(false);
		setId(1);
		setClickable(true);

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CharSequence[] items = {
						getResources().getString(R.string.donate_1),
						getResources().getString(R.string.donate_5),
						getResources().getString(R.string.donate_15) };

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle(R.string.donate);
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, final int item) {
						switch (item) {
						case 0:
							billingThread.purchase(DONATE_1);
							break;
						case 1:
							billingThread.purchase(DONATE_5);
							break;
						case 2:
							billingThread.purchase(DONATE_15);
							break;
						}
					}
				});
				builder.setNegativeButton(R.string.donate_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

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
					ControlButton.this.setVisibility(View.VISIBLE);
				} else {
					ControlButton.this.setVisibility(View.GONE);
				}
			}
		});
	}

}
