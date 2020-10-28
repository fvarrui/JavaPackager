package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
	
	public MacPackager() {
		super();
		installerGenerators.addAll(Context.getContext().getMacInstallerGenerators());
	}	
	
	public File getAppFile() {
		return appFile;
	}

	@Override
	public void doInit() throws Exception {

		this.macConfig.setDefaults(this);
		
	}

	@Override
	protected void doCreateAppStructure() throws Exception {
		
		// initializes the references to the app structure folders
		this.appFile = new File(appFolder, name + ".app");
		this.contentsFolder = new File(appFile, "Contents");
		this.resourcesFolder = new File(contentsFolder, "Resources");
		this.javaFolder = new File(resourcesFolder, this.macConfig.isRelocateJar() ? "Java" : "");
		this.macOSFolder = new File(contentsFolder, "MacOS");

		// makes dirs
		
		FileUtils.mkdir(this.appFile);
		Logger.info("App file folder created: " + appFile.getAbsolutePath());
		
		FileUtils.mkdir(this.contentsFolder);
		Logger.info("Contents folder created: " + contentsFolder.getAbsolutePath());
		
		FileUtils.mkdir(this.resourcesFolder);
		Logger.info("Resources folder created: " + resourcesFolder.getAbsolutePath());
		
		FileUtils.mkdir(this.javaFolder);
		Logger.info("Java folder created: " + javaFolder.getAbsolutePath());
		
		FileUtils.mkdir(this.macOSFolder);
		Logger.info("MacOS folder created: " + macOSFolder.getAbsolutePath());

		// sets common folders
		this.executableDestinationFolder = macOSFolder;
		this.jarFileDestinationFolder = javaFolder;
		this.jreDestinationFolder = new File(contentsFolder, "PlugIns/" + jreDirectoryName + "/Contents/Home");
		this.resourcesDestinationFolder = resourcesFolder;

	}
	
	/**
	 * Creates a native MacOS app bundle
	 */
	@Override
	public File doCreateApp() throws Exception {
		
		// sets startup file
		this.executable = new File(macOSFolder, "startup");			

		// copies jarfile to Java folder
		FileUtils.copyFileToFolder(jarFile, javaFolder);
		
		// creates startup file to boot java app
		VelocityUtils.render("mac/startup.vtl", executable, this);
		executable.setExecutable(true, false);
		Logger.info("Startup script file created in " + executable.getAbsolutePath());

		// copies universalJavaApplicationStub startup file to boot java app
		File appStubFile = new File(macOSFolder, "universalJavaApplicationStub");
		FileUtils.copyResourceToFile("/mac/universalJavaApplicationStub", appStubFile, true);
		if (!macConfig.isRelocateJar()) { // Modifies the stub to look at the non-Java sub folder
			FileUtils.processFileContent(appStubFile, stub -> stub.replaceAll("^AppleJavaFolder=\"\\$\\{AppPackageFolder\\}\"/Contents/Resources/Java$", "AppleJavaFolder=\"${AppPackageFolder}\"/Contents/Resources"));
		}
		
		appStubFile.setExecutable(true, false);

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

}
