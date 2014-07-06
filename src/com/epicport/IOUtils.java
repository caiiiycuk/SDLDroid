package com.epicport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.content.Context;

public class IOUtils {
	
	private static final int DEFAULT_BUFFER_SIZE = 2048 * 4;

	private IOUtils() {};
	
	public static long copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}

		return count;
	}
	
	public static File createTempFile(String prefix, String extension, Context context) throws IOException {
		File outputDir = context.getCacheDir(); // context being the Activity pointer
		return File.createTempFile(prefix, extension, outputDir);		
	}
	
	public static void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir)
			throws IOException {
		if (entry.isDirectory()) {
			IOUtils.createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()) {
			IOUtils.createDir(outputFile.getParentFile());
		}

		BufferedInputStream inputStream = new BufferedInputStream(
				zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(outputFile));

		try {
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
			inputStream.close();
		}
	}
	
	public static void unzipEntry(ZipInputStream inputStream, ZipEntry entry, String outputDir)
			throws IOException {
		if (entry.isDirectory()) {
			IOUtils.createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()) {
			IOUtils.createDir(outputFile.getParentFile());
		}

		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(outputFile));

		try {
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
		}
	}
	
	public static void createDir(File dir) {
		if (dir.exists()) {
			return;
		}

		if (!dir.mkdirs()) {
			throw new RuntimeException("Can not create dir " + dir);
		}
	}


}
