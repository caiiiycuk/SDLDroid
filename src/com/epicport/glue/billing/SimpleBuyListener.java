package com.epicport.glue.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.epicport.R;
import com.epicport.glue.NativeGlue;

public class SimpleBuyListener implements OnClickListener {

	private final static int GOLD = 1;
	private final static int OIL = 2;
	private final static int WOOD = 3;

	private final Activity activity;
	private final BillingThread billingThread;

	private static UnitSku[] getUnits() {
		return new UnitSku[] {
				new UnitSku(R.string.rocket_launcher, GOLD, 10000, "gold"),
				new UnitSku(R.string.siege_tank, OIL, 10000, "oil"),
				new UnitSku(R.string.sonic_tank, WOOD, 10000, "wood") };
	}

	public SimpleBuyListener(Activity activity,
			BillingThread billingThread) {
		this.activity = activity;
		this.billingThread = billingThread;
	}

	@Override
	public void onClick(View v) {
		final UnitSku[] unitsSku = getUnits();
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