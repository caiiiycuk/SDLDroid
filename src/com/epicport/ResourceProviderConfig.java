package com.epicport;

import java.io.File;

import android.net.Uri;

public interface ResourceProviderConfig {

	Uri resourceDownloadPage();

	boolean isAcceptableResource(File zip, ResourceDescriptor resourceDescriptor);
	
	File dataDir();

	void onChoose(File file);

	void reset();

}
