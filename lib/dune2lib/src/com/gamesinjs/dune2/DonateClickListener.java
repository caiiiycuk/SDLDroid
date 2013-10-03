package com.gamesinjs.dune2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

public class DonateClickListener implements OnClickListener {

	private final Activity activity;
	private final BillingThread billingThread;
	
	//private final static String DONATE_1 = "android.test.purchased";
	private final static String DONATE_1 	= "donate_1";
	private final static String DONATE_5 	= "donate_5";
	private final static String DONATE_15 	= "donate_15";

	public DonateClickListener(Activity activity, BillingThread billingThread) {
		this.activity = activity;
		this.billingThread = billingThread;
	}
	
	@Override
	public void onClick(View v) {
		final CharSequence[] items = {
				activity.getResources().getString(R.string.donate_1),
				activity.getResources().getString(R.string.donate_5),
				activity.getResources().getString(R.string.donate_15) };

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
}