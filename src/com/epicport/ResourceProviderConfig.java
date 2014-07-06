package com.epicport;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface ResourceProviderConfig {
	
	boolean isAcceptableResource(EzipResourceDescriptor resourceDescriptor);
	
	int getResourceType(FileWithIdentity fileWithIdentity);
	
	File dataDir();

	void onChoose(File file);

	void reset();

	void retry();

	String getSelectFileDescription();

	void reset(File file);

	void archiveOpen(ArchiveResource archive, File targetDirectory, ProgressPublisher<ExtractProgress> publisher) throws IOException;
	
	void archiveAddEntry(FileWithIdentity fileWithIdentity, Object entry) throws IOException;

	List<? extends Object> archiveEntriesForUnpack() throws IOException;
	
	void archiveUnpack(File resourceFile, FileWithIdentity fileWithIdentity) throws IOException;

	void archiveClose() throws IOException;

	Collection<File> getResources();

}
