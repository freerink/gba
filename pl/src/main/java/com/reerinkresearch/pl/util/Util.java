package com.reerinkresearch.pl.util;

public class Util {

	public static String zeroPrefix(int value, int length) {
		return zeroPrefix("" + value, length);
	}
	
	public static String zeroPrefix(String value, int length) {
		final String zeros = "0000000000000000000000000000000000000000";
		
		if(value == null) {
			throw new IllegalArgumentException("");
		}
		if(length > zeros.length() + value.length()) {
			throw new IllegalArgumentException("Length of result exceeds zeros buffer length");
		}
		return zeros.substring(0, length - value.length()) + value;
	}
}
