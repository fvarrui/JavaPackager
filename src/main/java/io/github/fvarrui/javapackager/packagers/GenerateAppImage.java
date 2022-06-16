package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public class GenerateAppImage extends ArtifactGenerator<LinuxPackager> {
	
	private static final int IMAGETOOL_VERSION = 13;
	private static final String IMAGETOOL_URL = "https://github.com/AppImage/AppImageKit/releases/download/" + IMAGETOOL_VERSION + "/appimagetool-%s.AppImage";
	
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
		File assetsFolder = packager.getAssetsFolder();
		File iconFile = packager.getIconFile();

		// output AppImage file
		File appImage = new File(outputFolder, name + ".AppImage");
		
		// AppDir folder
		File appDir = new File(assetsFolder, "AppDir");
		
		// gets/downloads AppImage tool
		Logger.info("Getting appimagetool...");
		File appImageTool = getAppImageTool(packager);
		Logger.info("App image tool found! " + appImageTool);

		// copies app folder to AppDir/usr/bin
		FileUtils.copyFolderContentToFolder(appFolder, new File(appDir, "usr/bin"));
				
		// generates AppImage desktop file from velocity template
		File desktopFile = new File(appDir, name + ".desktop");
		VelocityUtils.render("linux/desktop-appimage.vtl", desktopFile, packager);
		Logger.info("Desktop file rendered in " + desktopFile.getAbsolutePath());
		
		// creates AppRun symlink to startup script
		Logger.info("Creating AppRun symlink to startup script...");
		File appRun = new File(appDir, "AppRun");
		FileUtils.createSymlink(appRun, new File("usr/bin", executable.getName()));

		// creates AppRun symlink to startup script
		Logger.info("Creating symlink to icon ...");
		File appIconFile = new File(appDir, iconFile.getName());
		FileUtils.createSymlink(appIconFile, new File("usr/bin", iconFile.getName()));
		
		// runs appimagetool on appFolder
		Logger.info("Running appimagetool on " + appFolder);
		CommandUtils.execute(
				appImageTool,
				"--appimage-extract-and-run",
				appDir,
				appImage
			);
		
		Logger.info("Setting execution permissions to " + appImage);
		appImage.setExecutable(true);
		
		return appImage;
	}
	
	private File getAppImageTool(LinuxPackager packager) throws Exception {
		File assetsFolder = packager.getAssetsFolder();
		File appImageTool = new File(assetsFolder, "appimagetool"); 
		if (!appImageTool.exists()) {
			String imageToolUrl = String.format(IMAGETOOL_URL, getOSArch()); 
			try {
				FileUtils.downloadFromUrl(imageToolUrl, appImageTool);
			} catch (IOException e) {
				throw new Exception(imageToolUrl + "not found! ... Unsupported OS architecture " + getOSArch() + "?");
			}
			appImageTool.setExecutable(true);
		}
		return appImageTool;
	}
	
	private String getOSArch() {
		switch (SystemUtils.OS_ARCH) {
		case "amd64": 
			return "x86_64";
		case "x86": 
		case "i386": 
			return "i686";
		}
		return SystemUtils.OS_ARCH;
	}
	
}
