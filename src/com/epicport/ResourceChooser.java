package com.epicport;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;

import com.epicport.resourceprovider.R;

public class ResourceChooser {

	public static void show(final Activity activity, final Resources resources, final ResourceProviderConfig config) {
		final CharSequence[] items = new CharSequence[resources.size() + 1];
		
		for (int i=0; i<resources.size(); ++i) {
			items[i] = resources.get(i).getResourceDescriptor().getName();
		}
		
		items[items.length - 1] = activity.getString(R.string.search_more);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.resource_choose_title);
		builder.setCancelable(true);
		
		final Runnable onFail = new Runnable() {
			
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
				onFail.run();
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
					
					if (!resources.isUnpacked(resource)) {
						Log.i("epicport-ResourceChooser", "Unpacking resource  " + resource.getZipFile());
						UnzipTask unzipTask = new UnzipTask(activity, onChoose, onChoose);
						unzipTask.execute(
							resource.getZipFile().getAbsoluteFile().toString(),
							config.dataDir().getAbsoluteFile().toString(),
							resource.getResourceDescriptor().getUnpackMarker());
					} else {
						Log.i("epicport-ResourceChooser", "Chosed unpacked resource  at " + resource.getBaseDirectory());
						onChoose.run();
					}
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
