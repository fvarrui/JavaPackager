package io.github.fvarrui.javapackager.packagers;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;

import io.github.fvarrui.javapackager.model.Platform;

public class PackagerFactory {
	
	public static Packager createPackager(Platform platform) throws MojoExecutionException {
		if (platform == Platform.auto) platform = Platform.getCurrentPlatform();
		switch (platform) {
		case mac:
			return new MacPackager();
		case linux:
			return new LinuxPackager(); 
		case windows:
			return new WindowsPackager();
		default:
			throw new MojoExecutionException("Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);
		}
	}

}
