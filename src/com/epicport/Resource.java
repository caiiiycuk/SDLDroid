package com.epicport;

public abstract class Resource {
	
	public static final int TYPE_NOT_A_RESOURCE = 0;
	public static final int TYPE_RESOURCE = 1;
	public static final int TYPE_PACKED_RESOURCE = 2;
	
	public static final int RESOURCE_ZIP_ARCHIVE = 1;
	public static final int RESOURCE_ISO_FILE = 2;
	public static final int RESOURCE_DIRECTORY = 4;
	
	public static final String DESCRIPTOR_FILE = "e.json";
	
	private final ResourceDescriptor resourceDescriptor;
	
	public Resource(ResourceDescriptor descriptor) {
		this.resourceDescriptor = descriptor;
	}
	
	public ResourceDescriptor getResourceDescriptor() {
		return resourceDescriptor;
	}
	
	@Override
	public String toString() {
		StringBuilder description = new StringBuilder();
		description.append("Resource#").append(resourceDescriptor.getIdentity());
		description.append(" name ").append(resourceDescriptor.getName());
		
		description.append(" resource type ");
		switch (getType()) {
			case RESOURCE_DIRECTORY: 
				description.append("RESOURCE_DIRECTORY");
			break;
			case RESOURCE_ZIP_ARCHIVE: 
				description.append("RESOURCE_ZIP_ARCHIVE");
			break;
			case RESOURCE_ISO_FILE: 
				description.append("RESOURCE_ISO_FILE");
			break;
			default:
				description.append("UNKNOWN");
		}
		
		description.append(" descriptor resource type ");
		switch (getResourceDescriptor().getResourceType()) {
			case TYPE_RESOURCE: 
				description.append("TYPE_RESOURCE");
			break;
			case TYPE_PACKED_RESOURCE: 
				description.append("TYPE_PACKED_RESOURCE");
			break;
			case TYPE_NOT_A_RESOURCE: 
				description.append("TYPE_NOT_A_RESOURCE");
			break;
			default:
				description.append("UNKNOWN");
		}
		
		return description.toString();
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
	
	public abstract String getBaseDirectory();

	public boolean isResourceReady() {
		return resourceDescriptor.getResourceType() == Resource.TYPE_RESOURCE;
	}
	
	public abstract int getType();

	public abstract String getName();
}
