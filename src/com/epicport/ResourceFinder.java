package com.epicport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.epicport.resourceprovider.R;

public class ResourceFinder extends AsyncTask<Void, Integer, Resources> {

	private static final String DESCRIPTOR_FILE = "e.json";
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
	protected void onPostExecute(Resources result) {
		progressDialog.dismiss();

		if (result.size() > 0) {
			ResourceChooser.show(activity, result, config);
		} else {
			NotFoundResource.show(activity, config);
		}
	}

	@Override
	protected Resources doInBackground(Void... params) {
		return findCandidates(activity, config);
	}

	private Resources findCandidates(Activity activity,
			ResourceProviderConfig config) {
		String[] paths = StorageUtils.getStorageList().toArray(new String[0]);

		List<File> zipFiles = new ArrayList<File>();

		lookupPath(new File(""), zipFiles, 1);

		for (String path : paths) {
			lookupPath(new File(path), zipFiles, 1);
		}

		List<Resource> resources = new ArrayList<Resource>(zipFiles.size());

		for (File zip : zipFiles) {
			if (isCancelled()) {
				break;
			}

			Resource resource = makeResource(zip);

			if (resource != null) {
				resources.add(resource);
			}
		}

		if (resources.size() == 0) {
			return fallbackSearchInAppFolder();
		} else {
			return categorize(resources, activity, config);
		}
	}

	private Resources fallbackSearchInAppFolder() {
		Set<Resource> unpackedResources = new HashSet<Resource>();

		File appDir = config.dataDir();
		List<File> candidates = new ArrayList<File>();

		addFile(DESCRIPTOR_FILE, appDir, candidates);

		File[] subdirs = appDir.listFiles();
		if (subdirs != null) {
			for (File subdir : subdirs) {
				if (subdir.isDirectory() && !subdir.getName().startsWith(".")) {
					addFile(DESCRIPTOR_FILE, subdir, candidates);
				}
			}
		}

		for (File file : candidates) {
			try {
				FileInputStream stream = new FileInputStream(file);
				try {
					ResourceDescriptor resourceDescriptor = new ResourceDescriptor(
							file, stream);
					if (config.isAcceptableResource(null, resourceDescriptor)) {
						unpackedResources.add(new Resource(null,
								resourceDescriptor));
					}
				} catch (Exception e) {
					stream.close();
				}
			} catch (IOException e) {
				// ignore
			}
		}

		return new Resources(unpackedResources, new HashSet<Resource>());
	}

	private void addFile(String name, File root, List<File> container) {
		File[] listFiles = root.listFiles();
		if (listFiles != null) {
			for (File candidate : listFiles) {
				if (name.equals(candidate.getName())) {
					container.add(candidate);
					return;
				}
			}
		}
	}

	private static Resources categorize(List<Resource> resources,
			Activity activity, ResourceProviderConfig config) {
		Set<Resource> unpacked = new HashSet<Resource>(resources.size());
		Set<Resource> packed = new HashSet<Resource>(resources.size());

		File applicationDataDir = config.dataDir();

		for (Resource resource : resources) {
			if (isUnpacked(resource, applicationDataDir)) {
				unpacked.add(resource);
			} else {
				packed.add(resource);
			}
		}

		return new Resources(unpacked, packed);
	}

	private static boolean isUnpacked(Resource resource, File applicationDataDir) {
		File target = new File(applicationDataDir, resource
				.getResourceDescriptor().getUnpackMarker());
		return target.exists();
	}

	private Resource makeResource(File zip) {
		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile(zip);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry element = entries.nextElement();
				if (element.getName().endsWith(DESCRIPTOR_FILE)) {
					InputStream stream = zipFile.getInputStream(element);
					try {
						ResourceDescriptor resourceDescriptor = new ResourceDescriptor(
								element.getName(), stream);
						if (config
								.isAcceptableResource(zip, resourceDescriptor)) {
							return new Resource(zip, resourceDescriptor);
						}
					} catch (Exception e) {
						stream.close();
					}
				}
			}

		} catch (ZipException e) {
			// do nothing
		} catch (IOException e) {
			// do nothing
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		return null;
	}

	private static void lookupPath(File root, List<File> zipFiles,
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
				continue;
			}

			if (maxNesting <= 0) {
				continue;
			}

			if (isHighPriorityPath(fileName)) {
				lookupPath(file, zipFiles, 5);
			} else {
				lookupPath(file, zipFiles, maxNesting - 1);
			}
		}
	}

	private static boolean isHighPriorityPath(String fileName) {
		return fileName.contains("download");
	}

}
