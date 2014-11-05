package com.gamesinjs.dune2.update;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.gamesinjs.dune2.UpdateButton;

public class UpdateTask extends AsyncTask<String, Void, UpdateDescriptor> {

	private static final int TIMEOUT = 5000;
	private static final AtomicBoolean pending = new AtomicBoolean(false);
	
	private static final String url(String versionName) {
		return "http://epicport.com/android/update/dune2?version=%7B%22version%22%3A%22" + versionName + "%22%7D";
	}

	@Override
	protected UpdateDescriptor doInBackground(String... versions) {
		if (pending.get()) {
			return UpdateDescriptor.UPDATE_NOT_FOUND;
		}

		pending.set(true);
		String request = url(versions[0]);
		URLConnection connection = null;
		
		Log.i("Dune2", "Update request url '" + request + "'");

		try {
			connection = new URL(request).openConnection();
			connection.setConnectTimeout(TIMEOUT);
			connection.setReadTimeout(5000);
			
			String contents = IOUtils.toString(connection.getInputStream());
			JSONObject object = new JSONObject(contents);
			
			if (object.isNull("version")) {
				return UpdateDescriptor.UPDATE_NOT_FOUND;
			}
			
			String version = object.getString("version");
			String versionCode = object.getString("versionCode");
			String title = object.getString("title");
			String apk = object.getString("apk");
			String[] changelog = changelog(object);
			return new UpdateDescriptor(version, versionCode, title, apk, changelog);
		} catch (Exception e) {
			Log.e("Dune2", e.getMessage(), e);
			return UpdateDescriptor.UPDATE_NOT_FOUND;
		} finally {
			pending.set(false);
		}
	}

	private String[] changelog(JSONObject object) {
		try {
			JSONArray array = object.getJSONArray("changelog");
			String[] changelog = new String[array.length()];
			for (int i = 0; i < array.length(); ++i) {
				changelog[i] = array.getString(i);
			}
			return changelog;
		} catch (JSONException e) {
			Log.e("Dune2", e.getMessage(), e);
			return new String[0];
		}
	}
	
	protected void onPostExecute(UpdateDescriptor result) {
		UpdateButton.setUpdateDescriptor(result);
		
		if (result.sholdUpdate()) {
			Log.i("Dune2", "Found new version " + result.version);
		} else {
			Log.i("Dune2", "Updates not found");
		}
	}

	public static void runOn(Context context) {
		String versionName = getVersionName(context);

		Log.d("Dune2", "[UpdateTask] Started with version name '" + versionName + "'");
		new UpdateTask().execute(versionName);
	}

	private static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "0";
		}
	}

}
