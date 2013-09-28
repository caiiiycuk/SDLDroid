package com.gamesinjs.dune2;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class ControlButton extends ImageButton {

	private static ControlButton instance = null;

	// FIXME
	private final static String DONATE_PURCHASE = "android.test.purchased";
	private final static String DONATE_CANCELED = "android.test.canceled";
	private final static String DONATE_REFUNDED = "android.test.refunded";
	private final static String DONATE_UNAVAILABLE = "android.test.item_unavailable";

	public static void createFor(FrameLayout layout, Activity activity,
			BillingThread billingThread) {
		instance = new ControlButton(activity, billingThread);
		layout.addView(instance);
	}

	private ControlButton(final Activity activity,
			final BillingThread billingThread) {
		super(activity);
		setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.TOP));
		setImageResource(R.drawable.billing);
		//setBackgroundColor(Color.TRANSPARENT);
		//setPadding(3, 3, 3, 3);
		setFocusable(false);
		setId(1);
		setClickable(true);
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CharSequence[] items = {"Donate 5$", "Donate 15$", "Donate 25$", "Buy 5 tanks", "Buy 10 tanks", "Buy 15 tanks"};

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Pick a color");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				        Toast.makeText(activity, items[item], Toast.LENGTH_SHORT).show();
				    }
				});
				builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				
//				PopupMenu popup = new PopupMenu(activity, v);
//				MenuInflater inflater = popup.getMenuInflater();
//				inflater.inflate(R.menu.popupmenu, popup.getMenu());
//				popup.show();
//
//				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						if (item.getItemId() == R.id.menu1) {
//							billingThread.purchase(DONATE_PURCHASE);
//							return true;
//						}
//						
//						if (item.getItemId() == R.id.menu3) {
//							billingThread.purchase(DONATE_REFUNDED);
//							return true;
//						}
//						
//						if (item.getItemId() == R.id.menu2) {
//							billingThread.purchase(DONATE_CANCELED);
//							return true;
//						}
//						
//						if (item.getItemId() == R.id.menu4) {
//							billingThread.purchase(DONATE_UNAVAILABLE);
//							return true;
//						}
//
//						return false;
//					}
//				});

			}
		});
	}

	public static boolean dispatch(MotionEvent ev) {
		if (instance == null) {
			return false;
		}

		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		boolean clickAction = action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_UP;

		if (clickAction && instance.getLeft() <= (int) ev.getX()
				&& instance.getRight() > (int) ev.getX()
				&& instance.getTop() <= (int) ev.getY()
				&& instance.getBottom() > (int) ev.getY()) {
			return true;
		}

		return false;
	}

}
