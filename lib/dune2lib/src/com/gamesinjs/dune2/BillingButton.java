package com.gamesinjs.dune2;

import android.app.Activity;
import android.view.View;

import com.gamesinjs.dune2.game.GameMode;

public class BillingButton extends ControlButton {

	private final ReinforcementClickListener reinforcementClickListener;

	BillingButton(final Activity activity) {
		super(activity);

		this.reinforcementClickListener = new ReinforcementClickListener(
				activity);

		setOnClickListener(reinforcementClickListener);
		setVisibility(View.GONE);
	}

	@Override
	public void onGameModeChanged(final int gameMode) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (gameMode == GameMode.GM_MAP) {
					switch (GameMode.playerHouse()) {
					case GameMode.HOUSE_ATREIDES:
						setImageResource(R.drawable.a_sonic_tank);
						break;

					case GameMode.HOUSE_ORDOS:
						setImageResource(R.drawable.o_deviator);
						break;

					case GameMode.HOUSE_HARKONNEN:
						setImageResource(R.drawable.h_devastator);
						break;

					default:
						setImageResource(R.drawable.h_siege_tank);
					}

					setVisibility(View.VISIBLE);
				} else {
					setVisibility(View.GONE);
				}
			}
		});
	}

}
