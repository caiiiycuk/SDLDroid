package com.epicport;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.resourceprovider.R;

public class ResourceChooser {

	public static void show(final Activity activity, final Resources resources, final ResourceProviderConfig config) {
		final CharSequence[] items = new CharSequence[resources.size()];
		
		for (int i=0; i<resources.size(); ++i) {
			items[i] = resources.get(i).getResourceDescriptor().getName();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.resource_choose_title);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, final int item) {
				Resource resource = resources.get(item);
				
				//TODO: DO SOMETHING
				new UnzipTask(activity).execute(
						resource.getZipFile().getAbsoluteFile().toString(),
						config.dataDir().getAbsoluteFile().toString());
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
