package com.gamesinjs.dune2;

import android.app.Activity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gamesinjs.dune2.game.GameMode;
import com.gamesinjs.dune2.game.GameModeChangeListener;

public class ControlBar extends LinearLayout implements GameModeChangeListener {

	private static ControlBar instance = null;

	public static void createFor(FrameLayout layout, Activity activity) {
		instance = new ControlBar(activity);
		layout.addView(instance);
	}

	private final Activity activity;
	private final BillingButton billingButton;
	private final LangButton langButton;
	private final OptionsButton optionsButton;
	
	private ControlBar(Activity activity) {
		super(activity);
		this.activity = activity;
		this.billingButton = new BillingButton(activity);
		this.langButton = new LangButton(activity);
		this.optionsButton = new OptionsButton(activity);
		
		LayoutParams layoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP);

		setLayoutParams(layoutParams);
		addView(billingButton);
		addView(langButton);
		addView(optionsButton);
		

		GameMode.setGameModeChangeListener(this);
	}

	public static boolean dispatch(MotionEvent ev) {
		if (instance == null) {
			return false;
		}

		if (instance.billingButton.dispatch(ev)) {
			return true;
		}
		
		if (instance.langButton.dispatch(ev)) {
			return true;
		}
		
		if (instance.optionsButton.dispatch(ev)) {
			return true;
		}

		return false;
	}

	@Override
	public void onGameModeChanged(final int gameMode) {
		billingButton.onGameModeChanged(gameMode);
		langButton.onGameModeChanged(gameMode);
		optionsButton.onGameModeChanged(gameMode);
		
		activity.runOnUiThread(new Runnable() {
		 @Override
			public void run() {
				if (gameMode == GameMode.GM_MENU) {
					setVisibility(View.VISIBLE);
				} else if (gameMode == GameMode.GM_MAP) {
					setVisibility(View.VISIBLE);
				} else {
					setVisibility(View.GONE);
				}						
			}
		});
	}

}
