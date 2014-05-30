package com.epicport.glue.billing;

import android.app.Activity;

import com.epicport.R;
import com.epicport.glue.ui.ControlButton;

public class BillingButton extends ControlButton {

	private final SimpleBuyListener clickListener;

	public BillingButton(final Activity activity, final BillingThread billingThread, final SkuProvider skuProvider) {
		super(activity);

		this.clickListener = new SimpleBuyListener(
				activity, billingThread, skuProvider);

		setImageResource(R.drawable.billing);
		setOnClickListener(clickListener);
	}

	@Override
	public void onGameModeChanged(final int gameMode) {
	}

}
