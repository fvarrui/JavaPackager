package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;

public class GenerateAppImage extends ArtifactGenerator<LinuxPackager> {
	
	private static final int IMAGETOOL_VERSION = 13;
	private static final String IMAGETOOL_URL = "https://github.com/AppImage/AppImageKit/releases/download/%d/appimagetool-%s.AppImage".formatted(IMAGETOOL_VERSION, SystemUtils.OS_ARCH);
	
	public GenerateAppImage() {
		super("AppImage");
	}
	
	@Override
	public boolean skip(LinuxPackager packager) {

		if (!packager.getLinuxConfig().isGenerateAppImage()) {
			return true;
		}
		
		if (!packager.getPlatform().isCurrentPlatform() && !packager.isForceInstaller()) {
			Logger.warn(getArtifactName() + " cannot be generated due to the target platform (" + packager.getPlatform() + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return true;
		}
		
		return false;		

	}
	
	@Override
	protected File doApply(LinuxPackager packager) throws Exception {

		File appFolder = packager.getAppFolder();
		File outputFolder = packager.getOutputDirectory();
		String name = packager.getName();
		File executable = packager.getExecutable();

		// output AppImage file
		File appImage = new File(outputFolder, name + ".AppImage");
		
		// AppRun symlink file
		File appRun = new File(appFolder, "AppRun");
		
		// gets/downloads AppImage tool
		Logger.info("Getting appimagetool...");
		File appImageTool = getAppImageTool(packager);
		Logger.info("App image tool found! " + appImageTool);
		
		// creates AppRun symlink to startup script
		Logger.info("Creating AppRun symlink to startup script...");
		FileUtils.createSymlink(appRun, new File(executable.getName()));
		
		// runs appimagetool on appFolder
		Logger.info("Running appimagetool on " + appFolder);
		CommandUtils.execute(
				appImageTool,
				appFolder,
				appImage
			);
		
		Logger.info("Setting execution permissions to " + appImage);
		appImage.setExecutable(true);
		
		return appImage;
	}
	
	private File getAppImageTool(LinuxPackager packager) throws IOException {
		File assetsFolder = packager.getAssetsFolder();
		File appImageTool = new File(assetsFolder, "appimagetool"); 
		if (!appImageTool.exists()) {
			FileUtils.downloadFromUrl(IMAGETOOL_URL, appImageTool);
			appImageTool.setExecutable(true);
		}
		return appImageTool;
	}

}
