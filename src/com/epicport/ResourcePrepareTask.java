package com.epicport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.didion.loopy.FileEntry;
import net.didion.loopy.iso9660.ISO9660FileSystem;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.epicport.resourceprovider.R;

public class ResourcePrepareTask extends AsyncTask<Resource, ExtractProgress, Runnable> implements ProgressPublisher<ExtractProgress> {

	private final Activity activity;
	private final Runnable done;
	private final Runnable fail;
	private final ResourceProviderConfig config;
	private ProgressDialog progressDialog;

	public ResourcePrepareTask(Activity activity, Runnable done, Runnable fail,
			ResourceProviderConfig config) {
		this.activity = activity;
		this.done = done;
		this.fail = fail;
		this.config = config;
	}

	@Override
	protected void onPreExecute() {
		String title = activity.getResources().getString(R.string.unzip_title);
		String message = activity.getResources().getString(
				R.string.unzip_message);

		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	@Override
	protected void onPostExecute(Runnable result) {
		Log.i("epicport-ResourceProvider", "Resource prepare task [DONE]");
		progressDialog.dismiss();
		result.run();
	}

	@Override
	protected Runnable doInBackground(Resource... params) {
		Resource resource = params[0];

		Log.i("epicport-ResourceProvider", "Resource prepare task on resource "
				+ resource);

		switch (resource.getResourceDescriptor().getResourceType()) {
		case Resource.TYPE_RESOURCE:
			return simplyExtract(resource);

		case Resource.TYPE_PACKED_RESOURCE:
			return packedExtract(resource);

		default:
			return fail;
		}
	}

	private Runnable packedExtract(Resource resource) {
		FileWithIdentity fileWithIdentity = new FileWithIdentity();
		
		switch (resource.getType()) {
		case Resource.RESOURCE_ZIP_ARCHIVE: {
			ArchiveResource archive = (ArchiveResource) resource;
			
			try {
				config.archiveOpen(archive, new File(config.dataDir(),
						archive.getBaseDirectory()).getAbsoluteFile(), this);
				
				ZipFile zipFile = new ZipFile(archive.getArchiveFile());
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
								
				
				while (entries.hasMoreElements()) {
					ZipEntry fileEntry = entries.nextElement();
					fileWithIdentity.reset(fileEntry.getName(), fileEntry.getSize());
					config.archiveAddEntry(fileWithIdentity, fileEntry);
				}
				
				List<ZipEntry> unpackEntries =  (List<ZipEntry>) config.archiveEntriesForUnpack();
				
				for (ZipEntry fileEntry: unpackEntries) {
					fileWithIdentity.reset(fileEntry.getName(), fileEntry.getSize());
					
					InputStream inputStream = zipFile.getInputStream(fileEntry);
					File resourceFile = IOUtils.createTempFile("iso_", ".tmp", activity);
					FileOutputStream fileOutputStream = new FileOutputStream(resourceFile); 
					
					IOUtils.copy(inputStream, fileOutputStream);
					
					config.archiveUnpack(resourceFile, fileWithIdentity);
					
					inputStream.close();
					fileOutputStream.close();
					resourceFile.delete();
				}
				
				config.archiveClose();
				return done;
			} catch (Exception e) {
				Log.e("epicport-ResourceProvider", "RESOURCE_ZIP_FILE extraction error " + e.getMessage(), e);
			}
			break;
		}
		
		case Resource.RESOURCE_ISO_FILE:
			ArchiveResource archive = (ArchiveResource) resource;
			
			try {
				config.archiveOpen(archive, new File(config.dataDir(),
						archive.getBaseDirectory()).getAbsoluteFile(), this);
				
				ISO9660FileSystem iso9660FileSystem = new ISO9660FileSystem(archive.getArchiveFile(), true);
				Enumeration<FileEntry> entries = iso9660FileSystem.getEntries();
								
				
				while (entries.hasMoreElements()) {
					FileEntry fileEntry = entries.nextElement();
					fileWithIdentity.reset(fileEntry.getPath(), fileEntry.getSize());
					config.archiveAddEntry(fileWithIdentity, fileEntry);
				}
				
				List<FileEntry> unpackEntries =  (List<FileEntry>) config.archiveEntriesForUnpack();
				
				for (FileEntry fileEntry: unpackEntries) {
					fileWithIdentity.reset(fileEntry.getPath(), fileEntry.getSize());
					
					InputStream inputStream = iso9660FileSystem.getInputStream(fileEntry);
					File resourceFile = IOUtils.createTempFile("iso_", ".tmp", activity);
					FileOutputStream fileOutputStream = new FileOutputStream(resourceFile); 
					
					IOUtils.copy(inputStream, fileOutputStream);
					
					config.archiveUnpack(resourceFile, fileWithIdentity);
					
					inputStream.close();
					fileOutputStream.close();
					resourceFile.delete();
				}
				
				config.archiveClose();
				return done;
			} catch (Exception e) {
				Log.e("epicport-ResourceProvider", "RESOURCE_ISO_FILE extraction error " + e.getMessage(), e);
			}
			break;
		}

		return fail;
	}

	private Runnable simplyExtract(Resource resource) {
		switch (resource.getType()) {
		case Resource.RESOURCE_DIRECTORY:
			return done;
		case Resource.RESOURCE_ZIP_ARCHIVE:
			final ArchiveResource archive = (ArchiveResource) resource;
			final UnzipTask unzipTask = new UnzipTask(activity, done, fail);
			return new Runnable() {

				@Override
				public void run() {
					String target = new File(config.dataDir(),
							archive.getBaseDirectory()).getAbsoluteFile()
							.toString();

					if (archive.getResourceDescriptor() instanceof EzipResourceDescriptor) {
						target = config.dataDir().getAbsoluteFile().toString();
					}

					unzipTask.execute(archive.getArchiveFile()
							.getAbsoluteFile().toString(), target);
				}

			};
		}

		return fail;
	}
	
	@Override
	public void onProgressUpdate(ExtractProgress... values) {
		if (values != null && values.length > 0) {
			if (values[0] == null) {
				return;
			}
			
			progressDialog.setProgress(values[0].extracted);
			progressDialog.setMax(values[0].total);
			progressDialog.setMessage(values[0].message);
		}
	}

	@Override
	public void publish(ExtractProgress... values) {
		publishProgress(values);
	}
	
}
