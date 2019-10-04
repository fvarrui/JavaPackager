package fvarrui.maven.plugin.javapackager.utils;

public class JavaUtils {
	
	public static int getJavaMajorVersion() {
		String version = System.getProperty("java.version");
		return Integer.parseInt(version.split("\\.")[0]);
	}

}
