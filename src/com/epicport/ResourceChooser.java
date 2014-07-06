package com.epicport;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;

import com.epicport.resourceprovider.R;

public class ResourceChooser {

	public static void show(final Activity activity, final List<Resource> resources, final ResourceProviderConfig config) {
		final CharSequence[] items = new CharSequence[resources.size() + 1];
		
		for (int i=0; i<resources.size(); ++i) {
			items[i] = resources.get(i).getName();
		}
		
		items[items.length - 1] = activity.getString(R.string.select_resource);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.resource_choose_title);
		builder.setCancelable(true);
		
		final Runnable onCancle = new Runnable() {
			
			@Override
			public void run() {
				config.reset();
				
				Intent home = new Intent(Intent.ACTION_MAIN);
				home.addCategory(Intent.CATEGORY_HOME);
				home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(home);
		        activity.finish();
			}
		};
		
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				onCancle.run();
			}
		});
		
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, final int item) {
				if (item == items.length - 1) {
					NotFoundResource.show(activity, config);
				} else {
					final Resource resource = resources.get(item);
					
					Runnable onChoose = new Runnable() {
						@Override
						public void run() {
							File baseDirectory = new File(resource.getBaseDirectory());
							if (baseDirectory.isAbsolute()) {
								config.onChoose(baseDirectory);
							} else {
								config.onChoose(new File(config.dataDir(), resource.getBaseDirectory()));
							}
						};
					};
					
					Runnable onError = new Runnable() {

						@Override
						public void run() {
							config.reset();
							new ResourceFinder(activity, config).execute();
						}
					};
					
					new ResourcePrepareTask(activity, onChoose, onError, config).execute(resource);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
