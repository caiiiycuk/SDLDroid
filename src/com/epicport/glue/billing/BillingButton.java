package com.epicport.glue.billing;

import android.app.Activity;

import com.epicport.R;
import com.epicport.glue.ui.ControlButton;

public class BillingButton extends ControlButton {

	private final SimpleBuyListener reinforcementClickListener;

	public BillingButton(final Activity activity, final BillingThread billingThread) {
		super(activity);

		this.reinforcementClickListener = new SimpleBuyListener(
				activity, billingThread);

		setImageResource(R.drawable.billing);
		setOnClickListener(reinforcementClickListener);
	}

	@Override
	public void onGameModeChanged(final int gameMode) {
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (gameMode == GameMode.GM_MENU) {
//					setImageResource(R.drawable.billing);
//					setOnClickListener(donateClickListener);
//					setVisibility(View.VISIBLE);
//				} else if (gameMode == GameMode.GM_MAP) {
//					switch (GameMode.playerHouse()) {
//					case GameMode.HOUSE_ATREIDES:
//						setImageResource(R.drawable.sonic_tank);
//						break;
//
//					case GameMode.HOUSE_ORDOS:
//						setImageResource(R.drawable.deviator);
//						break;
//
//					case GameMode.HOUSE_HARKONNEN:
//						setImageResource(R.drawable.devastator);
//						break;
//
//					default:
//						setImageResource(R.drawable.siege_tank);
//					}
//
//					setOnClickListener(reinforcementClickListener);
//					setVisibility(View.VISIBLE);
//				} else {
//					setVisibility(View.GONE);
//				}
//			}
//		});
	}

}
