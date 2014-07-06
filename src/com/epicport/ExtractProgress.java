package com.epicport;

public class ExtractProgress {
	public final int extracted;
	public final int total;
	public final String message;
	
	public ExtractProgress(int extracted, int total, String message) {
		this.extracted = extracted;
		this.total = total;
		this.message = message;
	}
	
	public static ExtractProgress make(int total, String message) {
		return new ExtractProgress(0, total, message);
	}
	
	public static ExtractProgress make(int extracted, int total, String message) {
		return new ExtractProgress(extracted, total, message);
	}
}
