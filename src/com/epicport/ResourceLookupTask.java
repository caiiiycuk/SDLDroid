package com.epicport;

import java.io.File;

import com.epicport.resourceprovider.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ResourceLookupTask extends AsyncTask<File, Void, Resource> {
	
	private final Activity activity;
	private final ResourceProviderConfig config;
	private Resource resource;
	private ProgressDialog progressDialog;
	
	public ResourceLookupTask(Activity activity, final ResourceProviderConfig config) {
		this.activity = activity;
		this.config = config;
	}
	
	Runnable onSuccess = new Runnable() {
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
	
	Runnable onFail = new Runnable() {
		@Override
		public void run() {
			new ResourceFinder(activity, config).execute();
		};
	};

	@Override
	protected void onPostExecute(Resource resource) {
		progressDialog.dismiss();
		this.resource = resource;
		
		if (resource != null) {
			new ResourcePrepareTask(activity, onSuccess, onFail, config).execute(resource);
		} else {
			Toast.makeText(activity, activity.getString(R.string.wrong_resource), Toast.LENGTH_LONG).show();
			onFail.run();
		}
	}
	
	@Override
	protected Resource doInBackground(File... params) {
		File resourceFile = params[0];
		
		Log.d("epicport-ResourceProvider", "Trying to unpack file " + resourceFile.getAbsoluteFile());
		
		Resource ezipResource = ResourceFactory.makeEzipResource(resourceFile, config);
		
		if (ezipResource != null) {
			return ezipResource;
		}
//		
//		Resource isoResource = ResourceFactory.makeIsoResource(resourceFile, config);
//		
//		if (isoResource != null) {
//			return isoResource;
//		}
		
		
		
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		String title = activity.getResources().getString(R.string.checking_resource);
		String message = activity.getResources().getString(
				R.string.checking_resource);

		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
}