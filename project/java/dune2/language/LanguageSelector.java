package com.gamesinjs.dune2.language;

import com.gamesinjs.dune2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;

import java.util.Locale;

public class LanguageSelector {
	
	private static final String PREFERENCE_LANGUAGE_SUFFIX = "language.suffix";
	private static final String PREFERENCES_LANGUAGE = "language";
	
	private static final CharSequence[] LANGUAGES = {
		"English",
		"Русский"
	};
	
	private static final String[] LANGUAGE_SUFFIXES = {
		"-en",
		"-ru"
	};

	public static interface OnLanguageSelected {
		void onLanguageSelected(String suffix);
	}

	public static void show(final Activity activity) {
		if (systemLanguage(activity)) {
			return;
		}
		
		final SharedPreferences preferences = activity.getSharedPreferences(
				PREFERENCES_LANGUAGE, Activity.MODE_PRIVATE);

		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setTitle(R.string.select_language);
		builder.setItems(LANGUAGES, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String suffix = LANGUAGE_SUFFIXES[which];
				
				preferences.edit()
					.putString(PREFERENCE_LANGUAGE_SUFFIX, suffix)
					.commit();
				
				selectLanguageSuffix(activity, suffix);
				dialog.dismiss();
			}
		});
		
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				selectLanguageSuffix(activity, LANGUAGE_SUFFIXES[0]);
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.setOwnerActivity(activity);
		alert.show();
	}

	private static boolean systemLanguage(final Activity activity) {
		String suffix = "-" + Locale.getDefault().getLanguage();
		
		for (String candidate: LANGUAGE_SUFFIXES) {
			if (candidate.equalsIgnoreCase(suffix)) {
				selectLanguageSuffix(activity, candidate);
				return true;
			}
		}
		
		return false;
	}

	private static void selectLanguageSuffix(Activity activity, String suffix) {
		if (activity instanceof OnLanguageSelected) {
			((OnLanguageSelected) activity).onLanguageSelected(suffix);
		}
	}
	
}