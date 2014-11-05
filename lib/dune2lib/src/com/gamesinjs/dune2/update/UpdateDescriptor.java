package com.gamesinjs.dune2.update;

public class UpdateDescriptor {

	public static final UpdateDescriptor UPDATE_NOT_FOUND = new UpdateDescriptor(
			"<not-found>", "0", "<not an update>", "???", new String[0]);

	public final String version;
	public final String versionCode;
	public final String title;
	public final String apk;
	public final String[] changelog;

	public UpdateDescriptor(String version, String versionCode, String title,
			String apk, String[] changelog) {
		this.version = version;
		this.versionCode = versionCode;
		this.title = title;
		this.apk = apk;
		this.changelog = changelog;
	}
	
	public boolean sholdUpdate() {
		return this != UPDATE_NOT_FOUND;
	}
	
	public String changelogAsString() {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < changelog.length; ++i) {
			builder.append("\n\t").append(changelog[i]);
		}
		
		return builder.toString();
	}
	
}
