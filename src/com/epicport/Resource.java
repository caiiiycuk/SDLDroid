package com.epicport;

import java.io.File;

public class Resource {

	private final File zipFile;
	private final ResourceDescriptor resourceDescriptor;

	public Resource(File zipFile, ResourceDescriptor resourceDescriptor) {
		this.zipFile = zipFile;
		this.resourceDescriptor = resourceDescriptor;
	}

	public ResourceDescriptor getResourceDescriptor() {
		return resourceDescriptor;
	}

	public File getZipFile() {
		return zipFile;
	}

	@Override
	public String toString() {
		return resourceDescriptor.getName();
	}

	@Override
	public int hashCode() {
		return resourceDescriptor.getIdentity().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Resource) {
			return ((Resource) o).getResourceDescriptor().getIdentity()
					.equals(getResourceDescriptor().getIdentity());
		}

		return false;
	}

}
