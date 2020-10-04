package io.github.fvarrui.javapackager.packagers;

import java.io.File;

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
	public File apply(Packager packager) throws Exception {
		MacPackager macPackager = (MacPackager) packager;
		
		if (!macPackager.getMacConfig().isGeneratePkg()) {
			Logger.warn(getArtifactName() + " generation skipped by 'macConfig.generatePkg' property!");
			return null;
		}

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
