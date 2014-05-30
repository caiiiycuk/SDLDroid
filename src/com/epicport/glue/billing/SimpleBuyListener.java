package com.epicport.glue.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import com.epicport.R;
import com.epicport.glue.NativeGlue;

public class SimpleBuyListener implements OnClickListener {

	private final Activity activity;
	private final BillingThread billingThread;
	private final SkuProvider skuProvider;

	public SimpleBuyListener(Activity activity,
			BillingThread billingThread, SkuProvider skuProvider) {
		this.activity = activity;
		this.billingThread = billingThread;
		this.skuProvider = skuProvider;
	}

	@Override
	public void onClick(View v) {
		final UnitSku[] unitsSku = skuProvider.getUnits();
		final CharSequence[] items = {
				activity.getResources().getString(unitsSku[0].name),
				activity.getResources().getString(unitsSku[1].name),
				activity.getResources().getString(unitsSku[2].name) };

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.reinforcement);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, final int item) {
				final UnitSku unitSku = unitsSku[item];
				
//				billingThread.purchase(unitSku.sku, new PurchaseListener() {
//
//					@Override
//					public void success() {
//						Log.d("native-glue", "User buy" + unitSku);
						NativeGlue.buyResource(unitSku.unitId, unitSku.count);
//					}
//
//					@Override
//					public void fail() {
//						Log.d("native-glue", "User wan`t buy resource");
//					}
//				});
			}
		});
		builder.setNegativeButton(R.string.reinforcement_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

}