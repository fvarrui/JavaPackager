package io.github.fvarrui.javapackager.model;

import org.apache.commons.lang3.SystemUtils;

/**
 * JavaPackager target platform 
 */
public enum Platform {
	auto,
	linux,
	mac,
	windows;
	
	public boolean isCurrentPlatform() {
		if (this == auto) return true;
		return this == getCurrentPlatform();
	}
	
	public static Platform getCurrentPlatform() {
		if (SystemUtils.IS_OS_WINDOWS)	return windows; 
		if (SystemUtils.IS_OS_LINUX)	return linux; 
		if (SystemUtils.IS_OS_MAC)		return mac;
		return null;
	}
	
	public static Platform getPlatform(String platformString) {
		switch (platformString.toLowerCase()) {
		case "linux": return linux;
		case "darwin": return mac;
		case "windows": return windows;
		default: throw new IllegalArgumentException("Unknown platform " + platformString);
		}
	}
	
}
