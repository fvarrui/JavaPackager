package io.github.fvarrui.javapackager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.github.fvarrui.javapackager.model.Platform;

public class JDKUtils {
	
	/**
	 * Converts "release" file from specified JDK to map
	 * @param jdkPath JDK directory path
	 * @return Map with all properties
	 * @throws FileNotFoundException release file not found
	 * @throws IOException release file could not be read
	 */
	public static Map<String, String> getRelease(File jdkPath) throws FileNotFoundException, IOException {
		Map<String, String> propertiesMap = new HashMap<>();
		
		File releaseFile = new File(jdkPath, "release");
		
		if (!releaseFile.exists()) {
			releaseFile = new File(jdkPath, "Contents/Home/release");
		}
		
		if (!releaseFile.exists()) {
			throw new FileNotFoundException("release file not found"); 
		}
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(releaseFile));
		properties.forEach((key, value) -> propertiesMap.put(key.toString(), value.toString().replaceAll("^\"|\"$", "")));
		
		return propertiesMap;
	}
	
	public static boolean isValidJdk(Platform platform, File jdkPath) throws Exception {
		
		try {
			Map<String, String> releaseMap = getRelease(jdkPath);
			String osName = releaseMap.get("OS_NAME");
			switch (platform) {
			case linux:		return "Linux".equalsIgnoreCase(osName);
			case mac:		return "Darwin".equalsIgnoreCase(osName);
			case windows:	return "Windows".equalsIgnoreCase(osName);
			default:
			}
		} catch (FileNotFoundException e) {
			throw new Exception(e.getMessage(), e);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
		
		return false;
	}

}
