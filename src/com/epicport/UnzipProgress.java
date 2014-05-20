package com.epicport;

public class UnzipProgress {
	public final int extracted;
	public final int total;
	public final String message;
	
	public UnzipProgress(int extracted, int total, String message) {
		this.extracted = extracted;
		this.total = total;
		this.message = message;
	}
}
