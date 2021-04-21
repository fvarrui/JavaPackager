package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.utils.CommandUtils;

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
		return !packager.getMacConfig().isGeneratePkg();
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
