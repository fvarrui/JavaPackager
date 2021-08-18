package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a PKG installer file including all app folder's content only for MacOS so
 * app could be easily distributed
 */
public class GeneratePkg extends ArtifactGenerator {

	public GeneratePkg() {
		super("PKG installer");
	}
	
	@Override
	public boolean skip(Packager packager) {
		
		if (!packager.getMacConfig().isGeneratePkg()) {
			return true;
		}
		
		if (!packager.getPlatform().isCurrentPlatform()) {
			Logger.warn(getArtifactName() + " cannot be generated due to the target platform (" + packager.getPlatform() + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return true;
		}
		
		return false;
	}
	
	@Override
	protected File doApply(Packager packager) throws Exception {
		MacPackager macPackager = (MacPackager) packager;

		File appFile = macPackager.getAppFile();
		String name = macPackager.getName();
		File outputDirectory = macPackager.getOutputDirectory();
		String version = macPackager.getVersion();
		
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
