package io.github.fvarrui.javapackager.utils;

import io.github.fvarrui.javapackager.model.Platform;

public class IconUtils {

	public static String getIconFileExtensionByPlatform(Platform platform) {
		switch (platform) {
		case linux: 	return ".png";
		case mac: 		return ".icns";
		case windows: 	return ".ico";
		default:		return null;
		}
	}
	
}
