package com.epicport;

public class FileWithIdentity {

	public final static long IDENITITY_AUTO = 0;

	private String file;

	private long indentity;

	public FileWithIdentity() {
	}

	public FileWithIdentity(String file) {
		this(file, IDENITITY_AUTO);
	}

	public FileWithIdentity(String file, long indentity) {
		reset(file, indentity);
	}

	public FileWithIdentity(FileWithIdentity fileWithIdentity) {
		reset(fileWithIdentity.file, fileWithIdentity.indentity);
	}

	public FileWithIdentity reset(String file, long indentity) {
		this.file = file;
		this.indentity = indentity;
		return this;
	}

	public FileWithIdentity reset(String file) {
		return reset(file, IDENITITY_AUTO);
	}

	public String getFile() {
		return file;
	}

	public long getIdentity() {
		if (IDENITITY_AUTO == indentity) {
			return file.length();
		}

		return indentity;
	}

	@Override
	public String toString() {
		return new StringBuilder("FileWithIdentity").append(" file ")
				.append(file).append(" identity ").append(getIdentity())
				.toString();
	}

}
