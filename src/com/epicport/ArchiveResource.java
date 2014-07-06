package com.epicport;

import java.io.File;

public class ArchiveResource extends Resource {

	private final int type;
	
	private final File archiveFile;
	
	public ArchiveResource(int type, File archiveFile, ResourceDescriptor descriptor) {
		super(descriptor);
		this.type = type;
		this.archiveFile = archiveFile;
	}
	

	public String getBaseDirectory() {
		return getResourceDescriptor().getRootDirectoryName();
	}

	
	@Override
	public boolean isResourceReady() {
		return false;
	}

	public File getArchiveFile() {
		return archiveFile;
	}
	
	public int getType() {
		return type;
	}

	@Override
	public String getName() {
		return "[" + archiveFile.getName() + "] " + getResourceDescriptor().getName();
	}
	
}