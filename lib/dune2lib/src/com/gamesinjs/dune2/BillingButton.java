package com.gamesinjs.dune2;

import android.app.Activity;
import android.view.View;

import com.gamesinjs.dune2.game.GameMode;

public class BillingButton extends ControlButton {

	private final DonateClickListener donateClickListener;
	private final ReinforcementClickListener reinforcementClickListener;

	BillingButton(final Activity activity, final BillingThread billingThread) {
		super(activity);

		this.donateClickListener = new DonateClickListener(activity,
				billingThread);
		this.reinforcementClickListener = new ReinforcementClickListener(
				activity, billingThread);

		setImageResource(R.drawable.billing);
		setOnClickListener(donateClickListener);
	}

	@Override
	public void onGameModeChanged(final int gameMode) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (gameMode == GameMode.GM_MENU) {
					setImageResource(R.drawable.billing);
					setOnClickListener(donateClickListener);
					setVisibility(View.VISIBLE);
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
					setVisibility(View.VISIBLE);
				} else {
					setVisibility(View.GONE);
				}
			}
		});
	}

}
