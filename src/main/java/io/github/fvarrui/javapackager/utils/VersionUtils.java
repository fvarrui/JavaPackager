package io.github.fvarrui.javapackager.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Java utils
 */
public class VersionUtils {
	
	/**
	 * Splits a version string like x.y.z in an array of integers. If a part
	 * of the version is not a number, the parsing process breaks and returns 
	 * till this numnber. 
	 * @param version Version string like "x.y.z"
	 * @return An integer array like [ x, y, z ] 
	 */
	private static Integer [] parseVersion(String version) {
		String [] splittedVersion = version.split("\\.");
		List<Integer> parsedVersion = new ArrayList<>();
		for (int i = 0; i < splittedVersion.length; i++) {
			try { 
				parsedVersion.add(Integer.parseInt(splittedVersion[i]));
			} catch (NumberFormatException e) {
				break;
			}
		}
		return parsedVersion.toArray(new Integer[0]);
	}
	
	/**
	 * Compares two strings with version numbers
	 * @param v1 First version string
	 * @param v2 Second version string
	 * @return 0 if equals, -1 if v1 less than v2, +1 if v1 greater than v2
	 */
	public static int compareVersions(String v1, String v2) {
		Integer [] parsed1 = parseVersion(v1);
		Integer [] parsed2 = parseVersion(v2);
		int size = Math.min(parsed1.length, parsed2.length);
		for (int i = 0; i < size; i++) {
			if (parsed1[i] > parsed2[i]) return -1;
			if (parsed1[i] < parsed2[i]) return 1;
		}
		return 0;
	}
	
	/**
	 * Returns Java runtime major version (e.g.: 7 for Java 1.7, 8 for Java 1.8, 
	 * 9 for Java 9, ..., and so on)
	 * @return Java runtime major version
	 */
	public static int getJavaMajorVersion() {
		Integer [] parsed = parseVersion(System.getProperty("java.version"));
		int major = parsed[0];
		if (major >= 2) return major;
		return parsed[1];
	}

}
