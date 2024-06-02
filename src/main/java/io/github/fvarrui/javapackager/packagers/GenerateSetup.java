package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import net.jsign.WindowsSigner;

/**
 * Creates a Setup file including all app folder's content only for
 * Windows so app could be easily distributed
 */
public class GenerateSetup extends ArtifactGenerator<WindowsPackager> {

	public GenerateSetup() {
		super("Setup installer");
	}
	
	@Override
	public boolean skip(WindowsPackager packager) {
		
		if (!packager.getWinConfig().isGenerateSetup()) {
			return true;
		}
		
		if (!packager.getPlatform().isCurrentPlatform() && !packager.isForceInstaller()) {
			Logger.warn(getArtifactName() + " cannot be generated due to the target platform (" + packager.getPlatform() + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return true;
		}
		
		return false;
	}
	
	@Override
	protected File doApply(WindowsPackager packager) throws Exception {
		
		File iconFile = packager.getIconFile();
		File assetsFolder = packager.getAssetsFolder();
		String name = packager.getName();
		File outputDirectory = packager.getOutputDirectory();
		String version = packager.getVersion();
		
		// copies ico file to assets folder
		FileUtils.copyFileToFolder(iconFile, assetsFolder);
		
		// generates iss file from velocity template
		File issFile = new File(assetsFolder, name + ".iss");
		VelocityUtils.render("windows/iss.vtl", issFile, packager);

		// generates Windows installer with inno setup command line compiler
		CommandUtils.execute("iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + name + "_" + version, issFile);
		
		// setup file
		File setupFile = new File(outputDirectory, name + "_" + version + ".exe");
		if (!setupFile.exists()) {
			throw new Exception("Windows setup file generation failed!");
		}
		
		// sign installer
		WindowsSigner.sign(setupFile, packager.getDisplayName(), packager.getUrl(), packager.getWinConfig().getSigning());

		return setupFile;
	}
	
}
