package io.github.fvarrui.javapackager.utils;

public class StringUtils {
	
	public static String dosToUnix(String input) {
		return input.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
	}

}
