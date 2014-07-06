package com.epicport;

import java.io.File;

public class FolderResource extends Resource {

	private final File folder;
	
	public FolderResource(File folder, ResourceDescriptor descriptor) {
		super(descriptor);
		this.folder = folder;
	}
	

	public String getBaseDirectory() {
		return folder.getAbsolutePath();
	}
	
	
	@Override
	public int getType() {
		return RESOURCE_DIRECTORY;
	}
	
	@Override
	public String getName() {
		if (getResourceDescriptor().getResourceType() == TYPE_PACKED_RESOURCE) {
			return "[directory] " + getResourceDescriptor().getName();
		}
		
		return "[extracted] " + getResourceDescriptor().getName();
	}
	
}
