package com.gamesinjs.dune2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import mp.MpUtils;
import mp.PaymentResponse;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

@SuppressLint("UseSparseArrays")
public class PaymentRegistry {

	public static final String SERVICE_ID = "b8da143331952b2df3ec48cdbebd2b9d";
	
	public static final String APP_SECRET = "<secret>";
	
	private static final AtomicInteger transactionIndex = new AtomicInteger(911);
	
	private static Map<Integer, PurchaseListener> purchaseMap = Collections
			.synchronizedMap(new HashMap<Integer, PurchaseListener>());

	public static int getRequestCode() {
		return transactionIndex.incrementAndGet();
	}
	
	public static synchronized void purchase(int code,
			PurchaseListener purchaseListener) {
		if (purchaseMap.containsKey(code)) {
			purchaseListener.fail();
		} else {
			purchaseMap.put(code, purchaseListener);
		}
	}

	public static synchronized boolean onActivityResult(int code,
			int resultCode, Intent data) {
		if (!purchaseMap.containsKey(code)) {
			return false;
		}

		if (data == null) {
			fail(code);
			return true;
		}

		if (resultCode == Activity.RESULT_OK) {
			PaymentResponse response = new PaymentResponse(data);

			switch (response.getBillingStatus()) {
			case MpUtils.MESSAGE_STATUS_BILLED:
				success(code);
				break;
			case MpUtils.MESSAGE_STATUS_FAILED:
				fail(code);
				break;
			case MpUtils.MESSAGE_STATUS_PENDING:
				success(code);
				break;
			}
		} else {
			fail(code);
		}

		return true;
	}

	private static void success(int code) {
		PurchaseListener purchaseListener = purchaseMap.get(code);
		if (purchaseListener != null) {
			purchaseMap.remove(code);
			purchaseListener.success();
		}
	}

	private static void fail(int code) {
		PurchaseListener purchaseListener = purchaseMap.get(code);
		if (purchaseListener != null) {
			purchaseMap.remove(code);
			purchaseListener.fail();
		}
	}

}
