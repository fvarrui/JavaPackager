package io.github.fvarrui.javapackager.utils;

/**
 * Java utils
 */
public class VersionUtils {
	
	private static int [] parseVersion(String version) {
		String [] splittedVersion = version.split("\\.");
		int [] parsedVersion = new int[splittedVersion.length];
		for (int i = 0; i < splittedVersion.length; i++) {
			parsedVersion[i] = Integer.parseInt(splittedVersion[i]);
		}
		return parsedVersion;
	}
	
	/**
	 * Compares two strings with version numbers
	 * @param v1 First version string
	 * @param v2 Second version string
	 * @return 0 if equals, -1 if v1 less than v2, +1 if v1 greater than v2
	 */
	public static int compareVersions(String v1, String v2) {
		int [] parsed1 = parseVersion(v1);
		int [] parsed2 = parseVersion(v2);
		int size = Math.min(parsed1.length, parsed2.length);
		for (int i = 0; i < size; i++) {
			if (parsed1[i] > parsed2[i]) return -1;
			if (parsed1[i] < parsed2[i]) return 1;
		}
		return 0;
	}

}
