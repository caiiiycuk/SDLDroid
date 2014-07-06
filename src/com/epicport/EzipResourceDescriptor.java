package com.epicport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class EzipResourceDescriptor extends ResourceDescriptor {

	private static final String IDENTITY = "identity";
	private static final String NAME = "name";
	private static final String PROJECT = "game";

	private final String json;
	private final String name;
	private final String identity;
	private final String project;
	private final String baseDir;

	public EzipResourceDescriptor(String baseDir, InputStream stream, int resourceType)
			throws JSONException, IOException {
		super(resourceType);
		
		this.json = toString(stream);

		JSONObject object = new JSONObject(json);
		this.name = object.getString(NAME);
		this.identity = object.getString(IDENTITY);
		this.project = object.getString(PROJECT);
		this.baseDir = baseDir;
	}

	public String getName() {
		return name;
	}

	public String getIdentity() {
		return identity;
	}

	public String getProject() {
		return project;
	}
	
	@Override
	public String getRootDirectoryName() {
		return baseDir;
	}

	private static String toString(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		StringBuilder contents = new StringBuilder();

		String line;
		while ((line = reader.readLine()) != null) {
			contents.append(line);
		}

		return contents.toString();
	}

	@Override
	public String toString() {
		return json;
	}

}
