package io.github.fvarrui.javapackager.packagers;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public class MacPackager extends Packager {
	
	private File appFile;
	private File contentsFolder;
	private File resourcesFolder;
	private File javaFolder;
	private File macOSFolder;
	
	/**
	 * Creates a native MacOS app bundle
	 * 
	 * @throws MojoExecutionException
	 */
	@Override
	public File doCreateApp() throws MojoExecutionException {
		
		// copies jarfile to Java folder
		FileUtils.copyFileToFolder(jarFile, javaFolder);
		
		// creates startup file to boot java app
		File startupFile = new File(macOSFolder, "startup");
		VelocityUtils.render("mac/startup.vtl", startupFile, this);
		startupFile.setExecutable(true);
		Logger.info("Startup script file created in " + startupFile.getAbsolutePath());

		// copies universalJavaApplicationStub startup file to boot java app
		File appStubFile = new File(macOSFolder, "universalJavaApplicationStub");
		FileUtils.copyResourceToFile("/mac/universalJavaApplicationStub", appStubFile);
		appStubFile.setExecutable(true);

		// creates and write the Info.plist file
		File infoPlistFile = new File(contentsFolder, "Info.plist");
		VelocityUtils.render("mac/Info.plist.vtl", infoPlistFile, this);
		Logger.info("Info.plist file created in " + infoPlistFile.getAbsolutePath());

		// codesigns app folder
		if (Platform.mac.isCurrentPlatform()) {
			CommandUtils.execute("codesign", "--force", "--deep", "--sign", "-", appFile);
		} else {
			Logger.warn("Generated app could not be signed due to current platform is " + Platform.getCurrentPlatform());
		}
		
		return appFile;
	}

	/**
	 * Creates a DMG image file including all app folder's content only for MacOS so
	 * app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private File generateDmg() throws MojoExecutionException {
		
		Logger.append("Generating DMG disk image file ...");
		
		String volumeName = defaultIfBlank(macConfig.getVolumeName(), name);
		
		// removes whitespaces from volume name
		if (StringUtils.containsWhitespace(volumeName)) {
			volumeName = volumeName.replaceAll(" ", "");
			Logger.warn("Whitespaces has been removed from volume name: " + volumeName);
		}
		
		// final dmg file
		File dmgFile = new File(outputDirectory, name + "_" + version + ".dmg");
		
		// temp dmg file
		File tempDmgFile = new File(assetsFolder, dmgFile.getName());

		// mount dir
		File mountFolder = new File("/Volumes/" + volumeName);

		// creates a symlink to Applications folder
		Logger.info("Creating Applications link");
		File targetFolder = new File("/Applications");
		File linkFile = new File(appFolder, "Applications");
		FileUtils.createSymlink(linkFile, targetFolder);

		// copies background file
		Logger.info("Copying background image");
		File backgroundFolder = FileUtils.mkdir(appFolder, ".background");
		File backgroundFile = new File(backgroundFolder, "background.png");
		if (macConfig.getBackgroundImage() != null)
			FileUtils.copyFileToFile(macConfig.getBackgroundImage(), backgroundFile);
		else 
			FileUtils.copyResourceToFile("/mac/background.png", backgroundFile);
		
		// copies volume icon
		Logger.info("Copying icon file: " + iconFile.getAbsolutePath());
		File volumeIcon = (macConfig.getVolumeIcon() != null) ? macConfig.getVolumeIcon() : iconFile;  
		FileUtils.copyFileToFile(volumeIcon, new File(appFolder, ".VolumeIcon.icns"));
		
		// creates image
		Logger.info("Creating image: " + tempDmgFile.getAbsolutePath());
		CommandUtils.execute("hdiutil", "create", "-srcfolder", appFolder, "-volname", volumeName, "-fs", "HFS+", "-format", "UDRW", tempDmgFile);
		
		// mounts image
		Logger.info("Mounting image: " + tempDmgFile.getAbsolutePath());
		String result = CommandUtils.execute("hdiutil", "attach", "-readwrite", "-noverify", "-noautoopen", tempDmgFile);
		String deviceName = Arrays.asList(result.split("\n"))
									.stream()
									.filter(s -> s.contains(mountFolder.getAbsolutePath()))
									.map(s -> StringUtils.normalizeSpace(s))
									.map(s -> s.split(" ")[0])
									.findFirst().get();
		Logger.info("- Device name: " + deviceName);
		
		// renders applescript 
		File applescriptFile = new File(assetsFolder, "customize-dmg.applescript");
		VelocityUtils.render("/mac/customize-dmg.applescript.vtl", applescriptFile, this);
		Logger.info("Rendering applescript: " + applescriptFile.getAbsolutePath());
		
		// runs applescript 
		Logger.info("Running applescript");
		CommandUtils.execute("/usr/bin/osascript", applescriptFile, volumeName);
	
		// makes sure it's not world writeable
		Logger.info("Fixing permissions...");
		CommandUtils.execute("chmod", "-Rf", "go-w", mountFolder);
		
		// makes the top window open itself on mount:
		Logger.info("Blessing ...");
		CommandUtils.execute("bless", "--folder", mountFolder, "--openfolder", mountFolder);

		// tells the volume that it has a special file attribute
		CommandUtils.execute("SetFile", "-a", "C", mountFolder);
		
		// unmounts
		Logger.info("Unmounting disk image...");
		CommandUtils.execute("hdiutil", "detach", deviceName);
		
		// compress image
		Logger.info("Compressing disk image...");
		CommandUtils.execute("hdiutil", "convert", tempDmgFile, "-format", "UDZO", "-imagekey", "zlib-level=9", "-o", dmgFile);
		tempDmgFile.delete();
		
		// checks if dmg file was created
		if (!dmgFile.exists()) {
			Logger.error("DMG disk image file " + dmgFile.getAbsolutePath() + " cannot be generated!");
			Logger.subtract("DMG disk image file not generated!");
			dmgFile = null;
		} else {
			Logger.subtract("DMG disk image file generated in " + dmgFile.getAbsolutePath() + "!");
		}
		
		return dmgFile;
	}
	
	/**
	 * Creates a DMG image file including all app folder's content only for MacOS so
	 * app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private File generatePkg() throws MojoExecutionException {

		Logger.append("Generating Mac OS X installer package ...");
		
		File pkgFile = new File(outputDirectory, name + "_" + version + ".pkg");
		
		// invokes pkgbuild command
		CommandUtils.execute("pkgbuild", "--install-location", "/Applications", "--component", appFile, pkgFile);

		// checks if pkg file was created
		if (!pkgFile.exists()) {
			Logger.error("Installation package " + pkgFile.getAbsolutePath() + " cannot be generated!");
			Logger.subtract("Mac OS X installer package not generated!");
			pkgFile = null;
		} else {
			Logger.subtract("Mac OS X installer package generated in " + pkgFile.getAbsolutePath() + "!");
		}
		
		return pkgFile;
	}


	@Override
	public void doGenerateInstallers(List<File> installers) throws MojoExecutionException {
		
		if (macConfig.isGenerateDmg()) {
			File dmgFile = generateDmg();
			if (dmgFile != null) installers.add(dmgFile);
		}
		
		if (macConfig.isGeneratePkg()) {
			File pkgFile = generatePkg();
			if (pkgFile != null) installers.add(pkgFile);
		}
		
	}

	@Override
	protected void createSpecificAppStructure() throws MojoExecutionException {
		
		appFile 			= FileUtils.mkdir(appFolder, name + ".app");
		Logger.info("App file folder created: " + appFile.getAbsolutePath());
		
		contentsFolder 		= FileUtils.mkdir(appFile, "Contents");
		Logger.info("Contents folder created: " + contentsFolder.getAbsolutePath());
		
		resourcesFolder 	= FileUtils.mkdir(contentsFolder, "Resources");
		Logger.info("Resources folder created: " + resourcesFolder.getAbsolutePath());
		
		javaFolder 			= FileUtils.mkdir(resourcesFolder, "Java");
		Logger.info("Java folder created: " + javaFolder.getAbsolutePath());
		
		macOSFolder 		= FileUtils.mkdir(contentsFolder, "MacOS");
		Logger.info("MacOS folder created: " + macOSFolder.getAbsolutePath());

		this.executableDestinationFolder = macOSFolder;
		this.jarFileDestinationFolder = javaFolder;
		this.jreDestinationFolder = new File(contentsFolder, "PlugIns/" + jreDirectoryName + "/Contents/Home");
		this.resourcesDestinationFolder = resourcesFolder;
		
	}
	
}
