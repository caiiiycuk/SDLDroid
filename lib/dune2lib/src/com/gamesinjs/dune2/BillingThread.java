package com.gamesinjs.dune2;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.gamesinjs.billing.IabException;
import com.gamesinjs.billing.IabHelper;
import com.gamesinjs.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.gamesinjs.billing.IabHelper.OnIabSetupFinishedListener;
import com.gamesinjs.billing.IabResult;
import com.gamesinjs.billing.Inventory;
import com.gamesinjs.billing.Purchase;

public class BillingThread extends Thread {

	private static final int RESPONSE_CODE = 1001;

	private static final long SLEEP_TIME = 1000;

	private final Activity activity;
	private final Handler handler;
	private final String appKey;

	private final AtomicBoolean alive;
	private final AtomicBoolean pendingForSetup;
	private final AtomicBoolean pendingAsync;

	private IabHelper helper;

	private String skuToBuy;

	public BillingThread(Activity activity, String appKey) {
		setName("Billing thread");
		setDaemon(true);

		this.activity = activity;
		this.handler = new Handler(activity.getMainLooper());
		this.appKey = appKey;

		this.alive = new AtomicBoolean(true);
		this.pendingForSetup = new AtomicBoolean(false);
		this.pendingAsync = new AtomicBoolean(false);

		this.skuToBuy = null;

		start();
	}

	public void dispose() {
		alive.set(false);

		if (helper != null) {
			helper.dispose();
			helper = null;
		}
	}

	@Override
	public void run() {
		while (alive.get()) {
			if (skuToBuy != null && !isInited() && !pendingForSetup.get()) {
				init(skuToBuy);
				skuToBuy = null;
				continue;
			}

			if (skuToBuy != null && isInited()) {
				try {
					Inventory inventory = helper.queryInventory(false, null);
					Purchase purchase = inventory.getPurchase(skuToBuy);

					if (purchase != null) {
						helper.consume(purchase);
					}

					launchPurchaseFlow(skuToBuy, RESPONSE_CODE);
				} catch (final IabException e) {
					message(e.getMessage());

					helper.dispose();
					helper = null;
				}

				skuToBuy = null;
				continue;
			}

			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	private void launchPurchaseFlow(final String skuToBuy,
			final int responseCode) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				pendingAsync.set(true);

				helper.launchPurchaseFlow(activity, skuToBuy, responseCode,
						new OnIabPurchaseFinishedListener() {
							@Override
							public void onIabPurchaseFinished(
									final IabResult result, Purchase info) {
								if (result.isFailure()) {
									message("Unable to purchase: " + result);
									helper.dispose();
									helper = null;
								} else {
									message("Thank you!");
								}

								pendingAsync.set(false);
							}
						});
			}
		});
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (helper == null) {
			return false;
		}

		return helper.handleActivityResult(requestCode, resultCode, data);
	}

	public void purchase(String sku) {
		if (pendingAsync.get() || skuToBuy != null) {
			message("Billing service is busy");
			return;
		}

		skuToBuy = sku;

		message("Please wait, connecting...");
	}

	private void init(final String sku) {
		pendingForSetup.set(true);

		if (helper != null) {
			helper.dispose();
		}

		helper = new IabHelper(activity, appKey);
		helper.startSetup(new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(final IabResult result) {
				if (!result.isSuccess()) {
					message("Problem setting up In-app Billing: " + result);
					helper.dispose();
					helper = null;
				} else {
					skuToBuy = sku;
				}

				pendingForSetup.set(false);
			}
		});
	}

	private void message(final String text) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
			}
		});
	}

	private boolean isInited() {
		return helper != null && helper.ismSetupDone();
	}

}
