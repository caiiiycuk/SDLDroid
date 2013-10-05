package com.gamesinjs.dune2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;

import com.gamesinjs.dune2.game.GameMode;
import com.gamesinjs.dune2.language.LanguageSelector;

public class OptionsButton extends ControlButton implements OnClickListener {

	private static final String OPTIONS = "options";
	private static final String OFFSET = "offset";

	OptionsButton(final Activity activity) {
		super(activity);


		setImageResource(R.drawable.wrench);
		setOnClickListener(this);
		
		GameMode.offsetMode(isOffsetEnabled());
	}

	@Override
	public void onGameModeChanged(final int gameMode) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (gameMode == GameMode.GM_MENU) {
					setVisibility(View.VISIBLE);
				} else {
					setVisibility(View.GONE);
				}
			}
		});
	}
	
	public boolean isOffsetEnabled() {
		SharedPreferences preferences = activity.getSharedPreferences(
				OPTIONS, Activity.MODE_PRIVATE);

		return preferences.getBoolean(OFFSET, true);
	}
	
	public void setOffsetEnabled(boolean enabled) {
		SharedPreferences preferences = activity.getSharedPreferences(
				OPTIONS, Activity.MODE_PRIVATE);

		preferences.edit().putBoolean(OFFSET, enabled).commit();
		GameMode.offsetMode(enabled);
	}

	@Override
	public void onClick(View v) {
		CharSequence[] items;
		
		if (isOffsetEnabled()) {
			items = new CharSequence[] {activity.getResources().getString(R.string.disable_offset)};
		} else {
			items = new CharSequence[] {activity.getResources().getString(R.string.enable_offset)};
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.options);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, final int item) {
				setOffsetEnabled(!isOffsetEnabled());
			}
		});
		
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

}