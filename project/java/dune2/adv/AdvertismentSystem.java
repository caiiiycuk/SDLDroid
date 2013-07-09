package com.gamesinjs.dune2.adv;

import android.app.Activity;
import android.util.Log;
import android.view.View;

public class AdvertismentSystem extends Thread {

	private static boolean canShow;

	private static AdvertismentSystem instance;

	private static View view;

	private static Activity activity;

	static {
		instance = new AdvertismentSystem();
	}

	private AdvertismentSystem() {
		canShow = true;

		setName("Avertisment Thread");
		setDaemon(true);
	}

	@Override
	public void run() {
		Log.i("Dune_2", "Show advertisment: " + canShow);
		while (true) {
			try {
				boolean canShowNow = canShow();

				if (canShowNow != canShow) {
					Log.i("Dune_2", "Show advertisment: " + canShow);
					
					canShow = canShowNow;
					updateVisibility();
				}

				sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public static void init() {
		Log.i("Dune_2", "Initied");
		instance.start();
	}

	private static native boolean canShow();

	public static void setAdvertisment(View adView, Activity activity) {
		AdvertismentSystem.view = adView;
		AdvertismentSystem.activity = activity;
		updateVisibility();
	}

	private static void updateVisibility() {
		if (view != null) {
			class Callback implements Runnable {
				public void run() {
					if (canShow) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
				}
			}
			
			activity.runOnUiThread(new Callback());
		}
	}

}
