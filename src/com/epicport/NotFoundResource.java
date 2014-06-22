package com.epicport;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.epicport.resourceprovider.R;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class NotFoundResource {

	public static final int FILE_CODE = 112;
	
	public static void show(final Activity activity,
			final ResourceProviderConfig config) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.not_found_resource_title);
		builder.setCancelable(true);

		builder.setPositiveButton(R.string.not_found_continue,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						accept(activity, config);
					}
				});

		builder.setNegativeButton(R.string.not_found_cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						config.retry();
					}
				});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				refuse(activity, config);
			}
		});

		builder.setMessage(config.getSelectFileDescription());
		builder.create().show();

		return;
	}

	private static void accept(Activity activity, ResourceProviderConfig config) {
		config.reset();
		Intent target = FileUtils.createGetContentIntent();
		Intent intent = Intent.createChooser(target, activity.getString(R.string.choose_file));
		try {
			activity.startActivityForResult(intent, FILE_CODE);
		} catch (ActivityNotFoundException e) {
			// The reason for the existence of aFileChooser
		}
	}

	private static void refuse(Activity activity, ResourceProviderConfig config) {
		Intent home = new Intent(Intent.ACTION_MAIN);
		home.addCategory(Intent.CATEGORY_HOME);
		home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(home);
		activity.finish();
	}
	
	public static void onActivityResult(int requestCode, int resultCode,
			Intent data, Activity activity, ResourceProviderConfig config) {
		switch (requestCode) {
		case FILE_CODE:
			if (resultCode == Activity.RESULT_OK) {
				File file = FileUtils.getFile(activity, data.getData());
				
				if (file != null) {
					config.reset(file);
				}
			}
		}
	}

}