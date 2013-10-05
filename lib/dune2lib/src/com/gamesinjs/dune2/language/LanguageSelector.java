package com.gamesinjs.dune2.language;

import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;

public class LanguageSelector {
	
	public static final String PREFERENCE_LANGUAGE_SUFFIX = "language.suffix";
	public static final String PREFERENCES_LANGUAGE = "language";
	
	public static final CharSequence[] LANGUAGES = {
		"English",
		"Русский"
	};
	
	public static final String[] LANGUAGE_SUFFIXES = {
		"-en",
		"-ru"
	};

	public static interface OnLanguageSelected {
		void onLanguageSelected(String suffix);
	}

	public static void show(final Activity activity) {
		final SharedPreferences preferences = activity.getSharedPreferences(
				PREFERENCES_LANGUAGE, Activity.MODE_PRIVATE);

		String suffix = preferences.getString(PREFERENCE_LANGUAGE_SUFFIX, systemSuffix());
		
		selectLanguageSuffix(activity, suffix);
	}

	private static String systemSuffix() {
		String suffix = "-" + Locale.getDefault().getLanguage();
		
		for (String candidate: LANGUAGE_SUFFIXES) {
			if (candidate.equalsIgnoreCase(suffix)) {
				return suffix;
			}
		}
		
		return LANGUAGE_SUFFIXES[0];
	}

	private static void selectLanguageSuffix(Activity activity, String suffix) {
		if (activity instanceof OnLanguageSelected) {
			((OnLanguageSelected) activity).onLanguageSelected(suffix);
		}
	}
	
}