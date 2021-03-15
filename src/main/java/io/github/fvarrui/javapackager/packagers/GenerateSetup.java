package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates a Setup file including all app folder's content only for
 * Windows so app could be easily distributed
 */
public class GenerateSetup extends WindowsArtifactGenerator {

	public GenerateSetup() {
		super("Setup installer");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getWinConfig().isGenerateSetup();
	}
	
	@Override
	protected File doApply(Packager packager) throws Exception {
		WindowsPackager windowsPackager = (WindowsPackager) packager;
		
		File iconFile = windowsPackager.getIconFile();
		File assetsFolder = windowsPackager.getAssetsFolder();
		String name = windowsPackager.getName();
		File outputDirectory = windowsPackager.getOutputDirectory();
		String version = windowsPackager.getVersion();
		
		// copies ico file to assets folder
		FileUtils.copyFileToFolder(iconFile, assetsFolder);
		
		// generates iss file from velocity template
		File issFile = new File(assetsFolder, name + ".iss");
		VelocityUtils.render("windows/iss.vtl", issFile, windowsPackager);

		// generates windows installer with inno setup command line compiler
		CommandUtils.execute("iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + name + "_" + version, issFile);
		
		// setup file
		File setupFile = new File(outputDirectory, name + "_" + version + ".exe");
		if (!setupFile.exists()) {
			throw new Exception("Windows setup file generation failed!");
		}
		
		// sign installer
		sign(setupFile, windowsPackager);
		
		return setupFile;
	}
	
}
