package io.github.fvarrui.javapackager.gradle;

import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Packaging task fro Gradle 
 */
public class PackageTask extends AbstractPackageTask {
	public GradlePackagerSettings settings = new GradlePackagerSettings(this);

	@SuppressWarnings("unchecked")
	@Override
	protected Packager createPackager() throws Exception {
		return settings.createPackager();
	}
	
}
