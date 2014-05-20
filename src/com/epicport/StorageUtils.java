package com.epicport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import android.os.Environment;

public class StorageUtils {

	public static HashSet<String> getStorageList() {
		String def_path = Environment.getExternalStorageDirectory().getPath();
		String def_path_state = Environment.getExternalStorageState();
		boolean def_path_available = def_path_state
				.equals(Environment.MEDIA_MOUNTED)
				|| def_path_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);

		HashSet<String> paths = new HashSet<String>();

		if (def_path_available) {
			paths.add(def_path);
		}

		BufferedReader buf_reader = null;
		try {
			buf_reader = new BufferedReader(new FileReader("/proc/mounts"));
			String line;
			while ((line = buf_reader.readLine()) != null) {
				if (line.contains("vfat") || line.contains("/mnt")) {
					StringTokenizer tokens = new StringTokenizer(line, " ");
					String unused = tokens.nextToken(); // device
					String mount_point = tokens.nextToken(); // mount point
					if (paths.contains(mount_point)) {
						continue;
					}
					unused = tokens.nextToken(); // file system
					List<String> flags = Arrays.asList(tokens.nextToken()
							.split(",")); // flags
					boolean readonly = flags.contains("ro");

					if (line.contains("/dev/block/vold")) {
						if (!line.contains("/mnt/secure")
								&& !line.contains("/mnt/asec")
								&& !line.contains("/mnt/obb")
								&& !line.contains("/dev/mapper")
								&& !line.contains("tmpfs")) {
							paths.add(mount_point);
						}
					}
				}
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (buf_reader != null) {
				try {
					buf_reader.close();
				} catch (IOException ex) {
				}
			}
		}
		
		return paths;
	}
}