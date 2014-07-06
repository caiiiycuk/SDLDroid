package com.epicport;

public abstract class ResourceDescriptor {
	
	private final int resourceType;
	
	public ResourceDescriptor(int resourceType) {
		this.resourceType = resourceType;
	}
	
	public abstract String getIdentity();
	
	public abstract String getName();
	
	public abstract String getRootDirectoryName();
	
	@Override
	public int hashCode() {
		return getIdentity().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ResourceDescriptor) {
			return getIdentity().equals(((ResourceDescriptor) o).getIdentity());
		}
		return super.equals(o);
	}
	
	public int getResourceType() {
		return resourceType;
	}
	
}