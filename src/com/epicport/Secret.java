package com.epicport;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Secret {

	private Secret() {
	}

	public static String secret() {
		Long timePart = System.currentTimeMillis();
		String salt = "пашаГероЙИнтернета";
				
	    final String MD5 = "MD5";
		
	    try {
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance(MD5);
	        digest.update(timePart.toString().getBytes());
	        digest.update(salt.getBytes());
	        
	        byte messageDigest[] = digest.digest();

	        StringBuilder hexString = new StringBuilder();
	        for (byte aMessageDigest : messageDigest) {
	            String h = Integer.toHexString(0xFF & aMessageDigest);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        
	        return timePart + ":" + hexString.toString();
	    } catch (NoSuchAlgorithmException e) {
	        return "nosuchalgorithm";
	    }
	}
	
}
