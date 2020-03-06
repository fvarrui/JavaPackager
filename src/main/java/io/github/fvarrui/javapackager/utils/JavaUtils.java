package io.github.fvarrui.javapackager.utils;

public class JavaUtils {
	
	public static int getJavaMajorVersion() {
		String version = System.getProperty("java.version");
		
		int major = Integer.parseInt(version.split("\\.")[0]);
		if (major >= 2) return major;
		
		return Integer.parseInt(version.split("\\.")[1]);
	}

}
