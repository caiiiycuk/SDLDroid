package com.epicport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.didion.loopy.FileEntry;
import net.didion.loopy.iso9660.ISO9660FileSystem;
import android.util.Log;

public class ResourceFactory {
	
	public static Resource makeEzipResource(File zip, ResourceProviderConfig config) {
		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile(zip);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry element = entries.nextElement();
				if (element.getName().endsWith(Resource.DESCRIPTOR_FILE)) {
					InputStream stream = zipFile.getInputStream(element);
					try {
						EzipResourceDescriptor resourceDescriptor = new EzipResourceDescriptor(
								new File(element.getName()).getParent(), stream, Resource.TYPE_RESOURCE);
						if (config.isAcceptableResource(resourceDescriptor)) {
							return new ArchiveResource(Resource.RESOURCE_ZIP_ARCHIVE, zip, resourceDescriptor);
						}
					} catch (Exception e) {
						Log.e("epicport-ResourceProvider",
								"Rejected resource from "
										+ zip.getAbsoluteFile().toString()
										+ ", cause: " + e.getMessage());
						stream.close();
					}
				}
			}

		} catch (ZipException e) {
			// do nothing
		} catch (IOException e) {
			// do nothing
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		return null;
	}

	public static Collection<Resource> makeIsoResource(File resourceFile,
			ResourceProviderConfig config) {
		List<Resource> resources = new ArrayList<Resource>();
		FileWithIdentity fileWithIdentity = new FileWithIdentity();
		
		try {
			ISO9660FileSystem iso9660FileSystem = new ISO9660FileSystem(resourceFile, true);
			Enumeration<FileEntry> entries = iso9660FileSystem.getEntries();
			while (entries.hasMoreElements()) {
				FileEntry file = entries.nextElement();
				fileWithIdentity.reset(file.getPath(), file.getSize());
				int resourceType = config.getResourceType(fileWithIdentity);
				if (resourceType != Resource.TYPE_NOT_A_RESOURCE) {
					resources.add(new ArchiveResource(Resource.RESOURCE_ISO_FILE, resourceFile, 
							new FileResourceDescriptor(fileWithIdentity, resourceType)));
				} 
			}
		} catch (Throwable e) {
			Log.d("epicport-ResourceProvider", "Not iso resource " + e.getMessage());
		}
		
		return resources;
	}
}
