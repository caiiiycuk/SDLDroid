package com.gamesinjs.dune2;

import java.util.Arrays;

import mp.MpUtils;
import mp.PaymentRequest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gamesinjs.dune2.game.GameMode;

public class ReinforcementClickListener implements OnClickListener {

	private static final int UNIT_INVALID    = -1;
	private final static int UNIT_DEVIATOR   = 8;
	private final static int UNIT_LAUNCHER   = 7;
	private final static int UNIT_SIEGE_TANK = 10;
	private final static int UNIT_DEVASTATOR = 11;
	private final static int UNIT_SONIC_TANK = 12;

	private final Activity activity;
	private final Handler handler;

	private static final SparseArray<UnitSku[]> unitMap;

	static {
		unitMap = new SparseArray<UnitSku[]>();
		unitMap.put(GameMode.HOUSE_ATREIDES, new UnitSku[] {
				new UnitSku(R.string.rocket_launcher, UNIT_LAUNCHER, 6, R.drawable.a_rocket_launcher,  "rocket_launcher"),
				new UnitSku(R.string.siege_tank, UNIT_SIEGE_TANK, 8, R.drawable.a_siege_tank, "sieges"),
				new UnitSku(R.string.sonic_tank, UNIT_SONIC_TANK, 4, R.drawable.a_sonic_tank, "sonics") });
		unitMap.put(GameMode.HOUSE_ORDOS, new UnitSku[] {
				new UnitSku(R.string.rocket_launcher, UNIT_LAUNCHER, 6, R.drawable.o_rocket_launcher, "rocket_launcher"),
				new UnitSku(R.string.siege_tank, UNIT_SIEGE_TANK, 8, R.drawable.o_siege_tank, "sieges"),
				new UnitSku(R.string.deviator, UNIT_DEVIATOR, 5, R.drawable.o_deviator, "deviators") });
		unitMap.put(GameMode.HOUSE_HARKONNEN, new UnitSku[] {
				new UnitSku(R.string.rocket_launcher, UNIT_LAUNCHER, 6, R.drawable.h_rocket_launcher, "rocket_launcher"),
				new UnitSku(R.string.siege_tank, UNIT_SIEGE_TANK, 8, R.drawable.h_siege_tank, "sieges"),
				new UnitSku(R.string.devastator, UNIT_DEVASTATOR, 4, R.drawable.h_devastator, "devastators") });
	}
	
	private static UnitSku[] getUnits() {
		return unitMap.get(GameMode.playerHouse());
	}

	public ReinforcementClickListener(Activity activity) {
		this.activity = activity;
		this.handler = new Handler(activity.getMainLooper());
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
				UnitSku unitSku = unitsSku[item];
				
				final long[] units = new long[unitSku.count];

				for (int i=0; i < units.length; ++i) {
					units[i] = UNIT_INVALID;
				}
				

				GameMode.allocateUnits(unitSku.unitId, units);
				Log.d("OpenDUNE", "Allocationg units to buy -> " + Arrays.toString(units));

				if (haveInvalidUnits(units)) {
					message(activity.getResources().getString(R.string.too_many_units));
					Log.d("OpenDUNE", "Too_many_units -> disposing allocated");
					GameMode.freeUnits(units);
				} else {
					 PaymentRequest.PaymentRequestBuilder builder = new PaymentRequest.PaymentRequestBuilder();
		             builder.setService(PaymentRegistry.SERVICE_ID, PaymentRegistry.APP_SECRET);
		             builder.setDisplayString(activity.getResources().getString(unitSku.name));     
		             builder.setType(MpUtils.PRODUCT_TYPE_CONSUMABLE);
		             builder.setIcon(unitSku.drawable);

		             PaymentRequest pr = builder.build();
		             int requestCode = PaymentRegistry.getRequestCode();
		             
		             activity.startActivityForResult(pr.toIntent(activity), requestCode);
		             PaymentRegistry.purchase(requestCode, new PurchaseListener() {

						@Override
						public void success() {
							Log.d("OpenDUNE", "User succesfull but items -> placing");
							GameMode.placeUnits(units);
						}

						@Override
						public void fail() {
							Log.d("OpenDUNE", "User wan`t buy units -> disposing allocated");
							GameMode.freeUnits(units);
						}
						
					});
				}
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

	private boolean haveInvalidUnits(long[] units) {
		for (long unit : units) {
			if (unit < 0) {
				return true;
			}
		}

		return false;
	}
	
	private void message(final String text) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
			}
		});
	}
}