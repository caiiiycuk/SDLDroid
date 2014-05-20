package com.epicport;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.resourceprovider.R;

public class Main extends Activity {

	private ResourceProviderConfig config = new ResourceProviderConfig() {
		@Override
		public Uri resourceDownloadPage() {
			return Uri.parse("http://eepicport.com/ru/private/wargus/?secret="
					+ Secret.secret());
		}

		@Override
		public boolean isAcceptableResource(File zip,
				ResourceDescriptor resourceDescriptor) {
			return "wargus".equals(resourceDescriptor.getProject());
		}

		@SuppressLint("NewApi")
		public File dataDir() {
			try {
				File storage = getExternalFilesDir(null);
				
				if (storage == null) {
					return new File(getApplicationInfo().dataDir);
				}

				return storage;
			} catch (Throwable t) {
				return new File(getApplicationInfo().dataDir);
			}

		};

	};

	private ResourceFinder resourceFinder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (resourceFinder == null) {
			resourceFinder = new ResourceFinder(this, config);
			resourceFinder.execute();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (resourceFinder != null) {
			resourceFinder.cancel(false);
			resourceFinder = null;
		}
	}

}
