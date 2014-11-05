package com.gamesinjs.dune2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gamesinjs.dune2.game.GameMode;
import com.gamesinjs.dune2.game.GameModeChangeListener;
import com.gamesinjs.dune2.update.UpdateDescriptor;

public class UpdateButton extends Button implements GameModeChangeListener,
		OnClickListener {

	private static UpdateDescriptor updateDescriptor = UpdateDescriptor.UPDATE_NOT_FOUND;
	private static UpdateButton instance = null;

	public static void setUpdateDescriptor(UpdateDescriptor descriptor) {
		updateDescriptor = descriptor;

		if (instance != null) {
			instance.updateVisiblity();
		}
	}

	private final Activity activity;

	private int gameMode = GameMode.GM_MENU;

	public UpdateButton(Activity activity) {
		super(activity);

		this.activity = activity;
		this.setText(R.string.update_found);
		this.setTextSize(10);
		this.setTextColor(0xFFFFFFFF);
		this.getBackground().setColorFilter(0xFFFF0000,
				PorterDuff.Mode.MULTIPLY);
		this.setClickable(true);
		this.setOnClickListener(this);

		setVisibility(View.GONE);

		UpdateButton.instance = this;
	}

	@Override
	public void onGameModeChanged(int gameMode) {
		this.gameMode = gameMode;
		updateVisiblity();
	}

	private void updateVisiblity() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (UpdateButton.this.gameMode == GameMode.GM_MENU
						&& updateDescriptor.sholdUpdate()) {
					setVisibility(View.VISIBLE);
				} else {
					setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public void onClick(View view) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Version " + updateDescriptor.version);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.update_install,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.startActivity(new Intent(Intent.ACTION_VIEW,
								Uri.parse(updateDescriptor.apk)));
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.setMessage(updateDescriptor.title + "\nChangelog:"
				+ updateDescriptor.changelogAsString());
		builder.create().show();
	}

	public boolean dispatch(MotionEvent ev) {
		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		boolean clickAction = action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_UP;

		if (clickAction && getLeft() <= (int) ev.getX()
				&& getRight() > (int) ev.getX() && getTop() <= (int) ev.getY()
				&& getBottom() > (int) ev.getY()) {
			return true;
		}

		return false;
	}

}
