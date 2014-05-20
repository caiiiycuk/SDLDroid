package com.epicport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class ResourceDescriptor {

	private static final String IDENTITY 	= "identity";
	private static final String NAME 		= "name";
	private static final String PROJECT 	= "game";
	
	private final String descriptorInArchive;
	
	private final String json;
	private final String name;
	private final String identity;
	private final String project;

	public ResourceDescriptor(String descriptorInArchive, InputStream stream) throws JSONException,
			IOException {
		this.descriptorInArchive = descriptorInArchive;
		
		this.json = toString(stream);
		
		JSONObject object = new JSONObject(json);
		this.name = object.getString(NAME);
		this.identity = object.getString(IDENTITY);
		this.project = object.getString(PROJECT);
	}
	
	public String getDescriptorInArchive() {
		return descriptorInArchive;
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
