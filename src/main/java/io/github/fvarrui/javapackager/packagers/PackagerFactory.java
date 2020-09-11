package io.github.fvarrui.javapackager.packagers;

import org.apache.commons.lang3.SystemUtils;

import io.github.fvarrui.javapackager.model.Platform;

public class PackagerFactory {
	
	public static Packager createPackager(Platform platform) throws Exception {
		if (platform == Platform.auto || platform == null) platform = Platform.getCurrentPlatform();
		Packager packager = null;
		switch (platform) {
		case mac:
			packager = new MacPackager(); break;
		case linux:
			packager = new LinuxPackager(); break; 
		case windows:
			packager = new WindowsPackager(); break;
		default:
			throw new Exception("Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);
		}
		packager.platform(platform);
		return packager;
	}

}
