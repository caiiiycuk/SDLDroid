package com.epicport;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

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
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, final int item) {
				if (item == items.length - 1) {
					NotFoundResource.show(activity, config);
				} else {
					final Resource resource = resources.get(item);
					
					Runnable onChoose = new Runnable() {
						@Override
						public void run() {
							config.onChoose(new File(config.dataDir(), resource.getBaseDirectory()));
						};
					};
					
					if (!resources.isUnpacked(resource)) {
						UnzipTask unzipTask = new UnzipTask(activity, onChoose);
						unzipTask.execute(
							resource.getZipFile().getAbsoluteFile().toString(),
							config.dataDir().getAbsoluteFile().toString(),
							resource.getResourceDescriptor().getUnpackMarker());
					} else {
						onChoose.run();
					}
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
