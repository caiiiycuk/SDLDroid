package com.epicport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.epicport.resourceprovider.R;

public class UnzipTask extends AsyncTask<String, ExtractProgress, Boolean> {
	
	private final Activity activity;
	private final Runnable done;
	private final Runnable fail;
	private ProgressDialog progressDialog;

	public UnzipTask(Activity activity, Runnable done, Runnable fail) {
		this.activity = activity;
		this.done = done;
		this.fail = fail;
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
	protected void onPostExecute(Boolean success) {
		progressDialog.dismiss();
		
		if (success) {
			done.run();
		} else {
			Toast.makeText(activity, 
				"Error while extracting ezip file, please check free space...", Toast.LENGTH_LONG).show();
			fail.run();
		}
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String filePath = params[0];
		String destinationPath = params[1];
		
		Log.i("epicport-ResourceProvider", "Start unzip task from " + filePath + " to " + destinationPath);

		File archive = new File(filePath);
		try {
			ZipFile zipFile = new ZipFile(archive);

			int extracted = 0;
			int total = zipFile.size();

			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				IOUtils.unzipEntry(zipFile, entry, destinationPath);

				publishProgress(new ExtractProgress(extracted++, total, entry.getName()));
			}
		} catch (Exception e) {
			Log.e("UnzipTask", e.getMessage() + " while extracting from "
					+ filePath + " to " + destinationPath);
			return false;
		}

		return true;
	}

	@Override
	protected void onProgressUpdate(ExtractProgress... values) {
		if (values.length > 0) {
			progressDialog.setProgress(values[0].extracted);
			progressDialog.setMax(values[0].total);
//			progressDialog.setMessage(new File(values[0].message).getName());
		}
	}

}