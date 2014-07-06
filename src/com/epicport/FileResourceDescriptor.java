package com.epicport;

import java.io.File;

public class FileResourceDescriptor extends ResourceDescriptor {

	private final String identity;
	private final String rootDir;
	private final String name;
	private final FileWithIdentity fileWithIdentity;

	public FileResourceDescriptor(FileWithIdentity fileWithIdentity,
			int resourceType) {
		super(resourceType);
		this.name = new File(fileWithIdentity.getFile()).getParent();
		this.identity = new File(fileWithIdentity.getFile()).getParentFile().getName() + "-" + fileWithIdentity.getIdentity();
		this.rootDir = name.replaceAll("/", "-") + "-" + fileWithIdentity.getIdentity();
		this.fileWithIdentity = new FileWithIdentity(fileWithIdentity);
	}

	@Override
	public String getIdentity() {
		return identity;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getRootDirectoryName() {
		return rootDir;
	}

	public FileWithIdentity getFileWithIdentity() {
		return fileWithIdentity;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("FileResourceDescriptor").append(" identity ")
				.append(identity).append(" name ").append(name)
				.append(" rootDir ").append(rootDir).toString();
	}

}
