package io.github.fvarrui.javapackager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.github.fvarrui.javapackager.model.Arch;
import io.github.fvarrui.javapackager.model.Platform;

/**
 * JDK utils
 */
public class JDKUtils {
	
	/**
	 * Converts "release" file from specified JDK or JRE to map
	 * 
	 * @param jdkPath JDK directory path
	 * @return Map with all properties
	 * @throws IOException release file could not be read
	 */
	public static Map<String, String> getRelease(File jdkPath) throws IOException {
		Map<String, String> propertiesMap = new HashMap<>();
		File releaseFile = new File(jdkPath, "release");
		if (!releaseFile.exists()) {
			return null;
		}
		Properties properties = new Properties();
		properties.load(new FileInputStream(releaseFile));
		properties.forEach((key, value) -> propertiesMap.put(key.toString(), value.toString().replaceAll("^\"|\"$", "")));
		return propertiesMap;
	}

	/**
	 * Checks if the platform specified in the "release" map matches the required
	 * platform
	 * 
	 * @param platform Platform to match
	 * @param releaseMap Map containing all JDK/JRE release file's properties
	 * @return true if JDK is for platform
	 */
	private static boolean checkPlatform(Platform platform, Map<String, String> releaseMap) {
		try {
			return (releaseMap.get("OS_NAME") == null || platform == Platform.getPlatform(releaseMap.get("OS_NAME")));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the architecture specified in the "release" map matches the required
	 * architecture
	 * 
	 * @param arch Platform to match
	 * @param releaseMap Map containing all JDK/JRE release file's properties
	 * @return true if JDK is for platform
	 */
	private static boolean checkArchitecture(Arch arch, Map<String, String> releaseMap) {
		try {
			return (releaseMap.get("OS_ARCH") == null || arch == null || arch == Arch.getArch(releaseMap.get("OS_ARCH")));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the image type specified in the "release" map matches the required
	 * type
	 * 
	 * @param type Image type to match
	 * @param releaseMap Map containing all JDK/JRE release file's properties
	 * @return true if JDK is for platform
	 */
	private static boolean checkImageType(String imageType, Map<String, String> releaseMap) {
		try {
			return (releaseMap.get("IMAGE_TYPE") == null || imageType.equals(releaseMap.get("IMAGE_TYPE")));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Checks if a JDK is for platform and architecture
	 * 
	 * @param platform Specific platform
	 * @param arch Specific architecture
	 * @param jdkPath Path to the JDK folder
	 * @return true if is valid, otherwise false
	 * @throws FileNotFoundException Path to JDK not found
	 * @throws IOException Error reading JDK "release" file
	 */
	public static boolean isValidJDK(Platform platform, Arch arch, File jdkPath) throws FileNotFoundException, IOException {
		if (jdkPath == null || !jdkPath.isDirectory()) {
			return false;
		}
		Map<String, String> releaseMap = getRelease(jdkPath);
		if (releaseMap != null) {
			return checkPlatform(platform, releaseMap) && checkArchitecture(arch, releaseMap) && checkImageType("JDK", releaseMap);
		}
		return true;
	}
	
	/**
	 * Checks if a JDK is for platform
	 * 
	 * @param platform Specific platform
	 * @param jdkPath Path to the JDK folder
	 * @return true if is valid, otherwise false
	 * @throws FileNotFoundException Path to JDK not found
	 * @throws IOException Error reading JDK "release" file
	 */
	public static boolean isValidJDK(Platform platform, File jdkPath) throws FileNotFoundException, IOException {
		return isValidJDK(platform, null, jdkPath);
	}

	/**
	 * Checks if a JRE is for platform and architecture
	 * 
	 * @param platform Specific platform
	 * @param arch Specific architecture
	 * @param jrePath  Path to the JRE folder
	 * @return true if is valid, otherwise false
	 * @throws IOException Error reading JDK "release" file
	 */
	public static boolean isValidJRE(Platform platform, Arch arch, File jrePath) throws IOException {
		if (jrePath == null || !jrePath.isDirectory()) {
			return false;
		}
		Map<String, String> releaseMap = getRelease(jrePath);
		if (releaseMap != null) {
			return checkPlatform(platform, releaseMap) && checkArchitecture(arch, releaseMap);
		} else if (new File(jrePath, "bin/java").exists() || new File(jrePath, "bin/java.exe").exists()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a JRE is for platform
	 * 
	 * @param platform Specific platform
	 * @param jrePath Path to the JRE folder
	 * @return true if is valid, otherwise false
	 * @throws IOException Error reading JDK "release" file
	 */
	public static boolean isValidJRE(Platform platform, File jrePath) throws IOException {
		return isValidJRE(platform, null, jrePath);
	}
	
	/**
	 * Checks if release's file property "IMAGE_TYPE==JDK"
	 * @param jdkPath JDK path 
	 * @return true if is a JRE
	 * @throws IOException Error reading JDK "release" file
	 */
	public static boolean isJDK(File jdkPath) throws IOException {
		Map<String, String> releaseMap = getRelease(jdkPath);
		if (releaseMap != null) {
			return releaseMap.get("IMAGE_TYPE") == "JDK";
		}
		return true;
	}

}
