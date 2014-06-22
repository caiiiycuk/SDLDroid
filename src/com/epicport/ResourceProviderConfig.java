package com.epicport;

import java.io.File;

public interface ResourceProviderConfig {

	boolean isAcceptableResource(File zip, ResourceDescriptor resourceDescriptor);
	
	File dataDir();

	void onChoose(File file);

	void reset();

	void retry();

	String getSelectFileDescription();

	void reset(File file);
	
}
