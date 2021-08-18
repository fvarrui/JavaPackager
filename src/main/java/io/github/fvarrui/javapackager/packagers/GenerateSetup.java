package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.Registry;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
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
		
		if (!packager.getWinConfig().isGenerateSetup()) {
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
		WindowsPackager windowsPackager = (WindowsPackager) packager;
		
		File iconFile = windowsPackager.getIconFile();
		File assetsFolder = windowsPackager.getAssetsFolder();
		String name = windowsPackager.getName();
		File outputDirectory = windowsPackager.getOutputDirectory();
		String version = windowsPackager.getVersion();
		Registry registry = windowsPackager.getWinConfig().getRegistry();
		
		// checks if registry entries' names are not empy
		if (registry.getEntries().stream().anyMatch(e -> StringUtils.isBlank(e.getKey()) || StringUtils.isBlank(e.getValueName()))) {
			throw new Exception("One or more registry entries have no key and/or value name");
		}
		
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
