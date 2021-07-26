package io.github.fvarrui.javapackager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.github.fvarrui.javapackager.model.Platform;

/**
 * JDK utils 
 */
public class JDKUtils {
	
	/**
	 * Converts "release" file from specified JDK or JRE to map
	 * @param jdkPath JDK directory path
	 * @return Map with all properties
	 * @throws FileNotFoundException release file not found
	 * @throws IOException release file could not be read
	 */
	private static Map<String, String> getRelease(File jdkPath) throws FileNotFoundException, IOException {
		Map<String, String> propertiesMap = new HashMap<>();
		File releaseFile = new File(jdkPath, "release");
		if (!releaseFile.exists()) {
			throw new FileNotFoundException("release file not found: " + releaseFile); 
		}
		Properties properties = new Properties();
		properties.load(new FileInputStream(releaseFile));
		properties.forEach((key, value) -> propertiesMap.put(key.toString(), value.toString().replaceAll("^\"|\"$", "")));
		return propertiesMap;
	}
	
	/**
	 * Checks if the platform specified in the "release" file matches the required platform 
	 * @param platform
	 * @param jdkPath
	 * @return true if JDK is for platform
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static boolean checkPlatform(Platform platform, File jdkPath) throws FileNotFoundException, IOException {
		Map<String, String> releaseMap = getRelease(jdkPath);
		String osName = releaseMap.get("OS_NAME");
		switch (platform) {
		case linux:		return "Linux".equalsIgnoreCase(osName);
		case mac:		return "Darwin".equalsIgnoreCase(osName);
		case windows:	return "Windows".equalsIgnoreCase(osName);
		default:		return false;
		}
	}
	
	/**
	 * Checks if a JDK is for platform
	 * @param platform Specific platform
	 * @param jdkPath Path to the JDK folder
	 * @return true if is valid, otherwise false
	 * @throws FileNotFoundException Path to JDK not found 
	 * @throws IOException Error reading JDK "release" file
	 */
	public static boolean isValidJDK(Platform platform, File jdkPath) throws FileNotFoundException, IOException {
		return checkPlatform(platform, jdkPath);
	}
	
	/**
	 * Checks if a JRE is for platform
	 * @param platform Specific platform
	 * @param jrePath Path to the JRE folder
	 * @return true if is valid, otherwise false
	 * @throws IOException Error reading JDK "release" file
	 */
	public static boolean isValidJRE(Platform platform, File jrePath) throws IOException {
		try {
			return checkPlatform(platform, jrePath);
		} catch (FileNotFoundException e) {
			return new File(jrePath, "bin/java").exists() || new File(jrePath, "bin/java.exe").exists();
		}
	}

}
