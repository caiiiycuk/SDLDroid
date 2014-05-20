package com.epicport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.epicport.resourceprovider.R;

public class NotFoundResource {

	public static void show(final Activity activity, final ResourceProviderConfig config) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					activity);
			builder.setTitle(R.string.not_found_resource_title);
			builder.setCancelable(true);
			
			builder.setPositiveButton(R.string.not_found_download,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							accept(activity, config);
						}
					});
			
			builder.setNegativeButton(R.string.not_found_exit,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							refuse(activity, config);
						}
					});
			
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					refuse(activity, config);
				}
			});
			
			builder.setMessage(R.string.not_found_text);
			builder.create().show();
			
			return;
	}

	private static void accept(Activity activity, ResourceProviderConfig config) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, config.resourceDownloadPage());
		activity.startActivity(browserIntent);
	}

	private static void refuse(Activity activity, ResourceProviderConfig config) {
	}

}