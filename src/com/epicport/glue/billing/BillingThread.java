package com.epicport.glue.billing;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.epicport.R;
import com.epicport.glue.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.epicport.glue.billing.IabHelper.OnIabSetupFinishedListener;

public class BillingThread extends Thread {
	
	public static String TEST_APP_KEY = "@test-app-key";

	private static final int RESPONSE_CODE = 1001;

	private static final long SLEEP_TIME = 1000;

	private final Activity activity;
	private final Handler handler;
	private final String appKey;

	private final AtomicBoolean alive;
	private final AtomicBoolean pendingForSetup;
	private final AtomicBoolean pendingAsync;
	
	private PurchaseListener purchaseListener;

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
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							break;
						}
					}

					launchPurchaseFlow(skuToBuy, RESPONSE_CODE);
				} catch (final IabException e) {
					fireFailure();
					
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
								if (result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED) {
									//do nothing user want buy
									fireFailure();
								} else if (result.isFailure()) {
									fireFailure();
									
									message(R.string.unable_to_purchase, result.toString());
									helper.dispose();
									helper = null;
								} else {
									fireSuccess();
								}

								pendingAsync.set(false);
							}
						});
			}
		});
	}
	
	private void fireSuccess() {
		if (purchaseListener != null) {
			purchaseListener.success();
			purchaseListener = null;
		} else {
			message(R.string.thank_you);	
		}
	}

	private void fireFailure() {
		if (purchaseListener != null) {
			purchaseListener.fail();
			purchaseListener = null;
		}
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (helper == null) {
			return false;
		}

		return helper.handleActivityResult(requestCode, resultCode, data);
	}

	public void purchase(String sku) {
		purchase(sku, purchaseListener);
	}
	
	public void purchase(String sku, PurchaseListener purchaseListener) {
		if (TEST_APP_KEY.equals(appKey)) {
			purchaseListener.success();
			return;
		}
		
		if (pendingAsync.get() || skuToBuy != null) {
			message(R.string.billing_busy);
			
			if (purchaseListener != null) {
				purchaseListener.fail();
			}
			
			return;
		}

		skuToBuy = sku;
		this.purchaseListener = purchaseListener;

		message(R.string.connecting);
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
					fireFailure();
					message(R.string.billing_setup_problem, result.toString());
					helper.dispose();
					helper = null;
				} else {
					skuToBuy = sku;
				}

				pendingForSetup.set(false);
			}
		});
	}

	private void message(final String message) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void message(int resource) {
		message(activity.getResources().getString(resource));
	}
	
	private void message(int resource, String detailed) {
		message(activity.getResources().getString(resource) + detailed);
	}

	private boolean isInited() {
		return helper != null && helper.ismSetupDone();
	}

}
