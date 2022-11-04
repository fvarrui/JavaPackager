package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a PKG installer file including all app folder's content only for MacOS so
 * app could be easily distributed
 */
public class GeneratePkg extends ArtifactGenerator<MacPackager> {

	public GeneratePkg() {
		super("PKG installer");
	}
	
	@Override
	public boolean skip(MacPackager packager) {
		
		if (!packager.getMacConfig().isGeneratePkg()) {
			return true;
		}
		
		if (!packager.getPlatform().isCurrentPlatform() && !packager.isForceInstaller()) {
			Logger.warn(getArtifactName() + " cannot be generated due to the target platform (" + packager.getPlatform() + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return true;
		}
		
		return false;
	}
	
	@Override
	protected File doApply(MacPackager packager) throws Exception {

		File appFile = packager.getAppFile();
		String name = packager.getName();
		File outputDirectory = packager.getOutputDirectory();
		String version = packager.getVersion();
		
		File pkgFile = new File(outputDirectory, name + "_" + version + ".pkg");
		
		// invokes pkgbuild command
		CommandUtils.execute("pkgbuild", "--install-location", "/Applications", "--component", appFile, pkgFile);

		// checks if pkg file was created
		if (!pkgFile.exists()) {
			throw new Exception(getArtifactName() + " generation failed!");
		}
		
		return pkgFile;
	}
	
}
