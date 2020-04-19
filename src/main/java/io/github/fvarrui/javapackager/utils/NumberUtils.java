package io.github.fvarrui.javapackager.utils;

public class NumberUtils {

	public static int defaultIfNull(Integer value, int defaultValue) {
		if (value == null) return defaultValue;
		return value.intValue();
	}
	
}
