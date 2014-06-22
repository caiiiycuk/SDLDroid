package com.epicport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

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
						ResourceDescriptor resourceDescriptor = new ResourceDescriptor(
								element.getName(), stream);
						if (config
								.isAcceptableResource(zip, resourceDescriptor)) {
							return new Resource(zip, resourceDescriptor);
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
}
