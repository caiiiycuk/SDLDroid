package com.gamesinjs.dune2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gamesinjs.dune2.game.GameMode;
import com.gamesinjs.dune2.language.LanguageSelector;

public class LangButton extends ControlButton implements OnClickListener {

	LangButton(final Activity activity) {
		super(activity);

		setImageResource(R.drawable.lang);
		setOnClickListener(this);
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

	@Override
	public void onClick(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.select_language);
		builder.setItems(LanguageSelector.LANGUAGES, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, final int item) {
				SharedPreferences preferences = activity.getSharedPreferences(
						LanguageSelector.PREFERENCES_LANGUAGE, Activity.MODE_PRIVATE);

				preferences
						.edit()
						.putString(LanguageSelector.PREFERENCE_LANGUAGE_SUFFIX,
								LanguageSelector.LANGUAGE_SUFFIXES[item]).commit();

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(activity, R.string.restart_the_game,
								Toast.LENGTH_LONG).show();
					}
				});

			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}
}
