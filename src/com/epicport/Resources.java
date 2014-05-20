package com.epicport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Resources {
	private final Set<Resource> unpackedResources;
	private final Set<Resource> packedResources;
	private final List<Resource> all;
	
	public Resources(Set<Resource> unpacked, Set<Resource> packed) {
		this.unpackedResources = unpacked;
		this.packedResources = packed;
		
		this.all = new ArrayList<Resource>(unpacked.size() + packed.size());
		this.all.addAll(unpackedResources);
		this.all.addAll(packedResources);
	}

	public int size() {
		return all.size();
	}

	public Resource get(int i) {
		return all.get(i);
	}
	
	public boolean isUnpacked(Resource resource) {
		return unpackedResources.contains(resource);
	}
	
}