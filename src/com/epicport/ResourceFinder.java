package com.epicport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.epicport.resourceprovider.R;

public class ResourceFinder extends AsyncTask<Void, String, List<Resource>> {

	private ProgressDialog progressDialog;
	private Activity activity;
	private ResourceProviderConfig config;

	public ResourceFinder(Activity activity, ResourceProviderConfig config) {
		this.activity = activity;
		this.config = config;
	}

	@Override
	protected void onPreExecute() {
		String title = activity.getResources().getString(
				R.string.resource_finder_title);
		String message = activity.getResources().getString(
				R.string.resource_finder_message);

		progressDialog = ProgressDialog.show(activity, title, message, false,
				false);
	}

	@Override
	protected void onPostExecute(List<Resource> result) {
		progressDialog.dismiss();

		if (result.size() > 0) {
			ResourceChooser.show(activity, result, config);
		} else {
			NotFoundResource.show(activity, config);
		}
	}

	@Override
	protected List<Resource> doInBackground(Void... params) {
		return findCandidates(activity, config);
	}

	private List<Resource> findCandidates(Activity activity,
			ResourceProviderConfig config) {
		String[] paths = StorageUtils.getStorageList().toArray(new String[0]);

		List<File> zipFiles = new ArrayList<File>();
		List<File> isoFiles = new ArrayList<File>();

		for (String path : paths) {
			publishProgress(activity.getString(R.string.scanning_fs) + 
					"(" + path + ")");
			lookupPath(new File(path), zipFiles, isoFiles, 1);
		}

		Set<File> proposed = new HashSet<File>(config.getResources());
		List<Resource> resources = new ArrayList<Resource>(zipFiles.size() + proposed.size());
		
		for (File file: proposed) {
			publishProgress(activity.getString(R.string.scanning_file) + 
					"(" + file + ")");
			ResourceFactory.makeResource(file, config, resources);
		}

		for (File zip: zipFiles) {
			if (isCancelled()) {
				break;
			}
			
			if (proposed.contains(zip)) {
				continue;
			}

			publishProgress(activity.getString(R.string.scanning_file) + 
					"(" + zip + ")");
			ResourceFactory.makeEzipResource(zip, config, resources);
		}
		
		for (File iso: isoFiles) {
			if (isCancelled()) {
				break;
			}
			
			if (proposed.contains(iso)) {
				continue;
			}
			
			publishProgress(activity.getString(R.string.scanning_file) + 
					"(" + iso + ")");
			ResourceFactory.makeIsoResource(iso, config, resources);
		}

		publishProgress(activity.getString(R.string.scanning_extracted));
		List<Resource> inAppResources = searchInAppFolder();
		inAppResources.addAll(resources);
		Collections.sort(resources, new Comparator<Resource>() {

			@Override
			public int compare(Resource lhs, Resource rhs) {
				if (lhs.isResourceReady() && !rhs.isResourceReady()) {
					return -1;
				}
				
				if (!lhs.isResourceReady() && rhs.isResourceReady()) {
					return 1;
				}
				
				return 0;
			}
		});
		return inAppResources;
	}

	private List<Resource> searchInAppFolder() {
		List<Resource> resources = new ArrayList<Resource>();

		File appDir = config.dataDir();
		collectResources(appDir, resources);

		File[] subdirs = appDir.listFiles();
		if (subdirs != null) {
			for (File subdir : subdirs) {
				if (subdir.isDirectory() && !subdir.getName().startsWith(".")) {
					collectResources(subdir, resources);
				}
			}
		}
		
		return resources;
	}

	private void collectResources(File root, List<Resource> container) {
		FileWithIdentity fileWithIdentity = new FileWithIdentity();
		File[] listFiles = root.listFiles();
		if (listFiles != null) {
			for (File file : listFiles) {
				fileWithIdentity.reset(file.getAbsoluteFile().toString());
				int resourceType = config.getResourceType(fileWithIdentity);
				Log.i("epicport", file.getName() + " type " + resourceType);
				if (resourceType == Resource.TYPE_RESOURCE) {
					container.add(new FolderResource(file.getParentFile(), 
							new FileResourceDescriptor(fileWithIdentity, resourceType)));
				}
			}
			
			return;
		}
	}

	private static void lookupPath(File root, List<File> zipFiles, List<File> isoFiles,
			int maxNesting) {
		if (!root.exists()) {
			return;
		}

		File[] files = root.listFiles();

		if (files == null) {
			return;
		}

		for (File file : files) {
			String fileName = file.getName().toLowerCase();

			if (file.isFile()) {
				if (fileName.endsWith(".ezip")) {
					zipFiles.add(file);
				}
				
				if (fileName.endsWith(".iso")) {
					isoFiles.add(file);
				}
				
				continue;
			}

			if (maxNesting <= 0) {
				continue;
			}

			if (isHighPriorityPath(fileName)) {
				lookupPath(file, zipFiles, isoFiles, 5);
			} else {
				lookupPath(file, zipFiles, isoFiles, maxNesting - 1);
			}
		}
	}

	private static boolean isHighPriorityPath(String fileName) {
		return fileName.contains("download") || fileName.contains("downloads");
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		if (values.length > 0) {
			if (values[0] != null) {
				progressDialog.setMessage(values[0]);
			}
		}
	}

}
