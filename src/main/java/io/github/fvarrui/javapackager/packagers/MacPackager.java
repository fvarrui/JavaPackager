package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import io.github.fvarrui.javapackager.utils.VersionUtils;
import io.github.fvarrui.javapackager.utils.XMLUtils;

/**
 * Packager for Mac OS X
 */
public class MacPackager extends Packager {

	private File appFile;
	private File contentsFolder;
	private File resourcesFolder;
	private File javaFolder;
	private File macOSFolder;

	public File getAppFile() {
		return appFile;
	}

	@Override
	public void doInit() throws Exception {

		this.macConfig.setDefaults(this);

		// FIX useResourcesAsWorkingDir=false doesn't work fine on Mac OS (option
		// disabled)
		if (!this.isUseResourcesAsWorkingDir()) {
			this.useResourcesAsWorkingDir = true;
			Logger.warn(
					"'useResourcesAsWorkingDir' property disabled on Mac OS (useResourcesAsWorkingDir is always true)");
		}

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

		// copies jarfile to Java folder
		FileUtils.copyFileToFolder(jarFile, javaFolder);

		if (this.administratorRequired) {

			// sets startup file
			this.executable = new File(macOSFolder, "startup");

			// creates startup file to boot java app
			VelocityUtils.render("mac/startup.vtl", executable, this);
			Logger.info("Startup script file created in " + executable.getAbsolutePath());
			if(!executable.setExecutable(true, false)){
				Logger.error("Failed to set executable permissions for " + executable);
			}


		} else {

			// sets startup file
			this.executable = new File(macOSFolder, "universalJavaApplicationStub");
			Logger.info("Using " + executable.getAbsolutePath() + " as startup script");

		}

		// copies universalJavaApplicationStub startup file to boot java app
		File appStubFile = new File(macOSFolder, "universalJavaApplicationStub");
		String universalJavaApplicationStubResource = null;
		switch (macConfig.getMacStartup()) {
		case UNIVERSAL:	universalJavaApplicationStubResource = "universalJavaApplicationStub"; break;
		case X86_64:	universalJavaApplicationStubResource = "universalJavaApplicationStub.x86_64"; break;
		case ARM64: 	universalJavaApplicationStubResource = "universalJavaApplicationStub.arm64"; break;
		case SCRIPT: 	universalJavaApplicationStubResource = "universalJavaApplicationStub.sh"; break;
		}
		FileUtils.copyResourceToFile("/mac/" + universalJavaApplicationStubResource, appStubFile);
		if(!appStubFile.setExecutable(true, false)){
			Logger.error("Failed to make app-stub executable");
		}

		// process classpath
		classpath = (this.macConfig.isRelocateJar() ? "Java/" : "") + this.jarFile.getName() + (classpath != null ? ":" + classpath : "");
		classpaths = Arrays.asList(classpath.split("[:;]"));
		if (!isUseResourcesAsWorkingDir()) {
			classpaths = classpaths
					.stream()
					.map(cp -> new File(cp).isAbsolute() ? cp : "$ResourcesFolder/" + cp)
					.collect(Collectors.toList());
		}
		classpath = StringUtils.join(classpaths, ":");

		// creates and write the Info.plist file
		File infoPlistFile = new File(contentsFolder, "Info.plist");
		VelocityUtils.render("mac/Info.plist.vtl", infoPlistFile, this);
		XMLUtils.prettify(infoPlistFile);
		Logger.info("Info.plist file created in " + infoPlistFile.getAbsolutePath());

		// copy provisionprofile
		if (macConfig.getProvisionProfile() != null) {
			// file name must be 'embedded.provisionprofile'
			File provisionProfile = new File(contentsFolder, "embedded.provisionprofile");
			FileUtils.copyFileToFile(macConfig.getProvisionProfile(), provisionProfile);
			Logger.info("Provision profile file created from " + "\n" +
					macConfig.getProvisionProfile() + " to \n" +
					provisionProfile.getAbsolutePath());
		}

		// codesigns app folder
		if (!Platform.mac.isCurrentPlatform()) {
			// TODO: is this check needed? We are in the *MacPackager*
			Logger.warn("Generated app could not be signed due to current platform is " + Platform.getCurrentPlatform());
		} else if (!getMacConfig().isCodesignApp()) {
			Logger.warn("App codesigning disabled");
		} else {
			codesign(this.macConfig.getDeveloperId(), this.macConfig.getEntitlements(), this.appFile);
		}

		return appFile;
	}

	private void codesign(String developerId, File entitlements, File appFile) throws Exception {

		// checks --option flags
		List<String> flags = new ArrayList<>();
		if (macConfig.isHardenedCodesign()) {
			if (VersionUtils.compareVersions("10.13.6", SystemUtils.OS_VERSION) >= 0) {
				flags.add("runtime"); // enable hardened runtime if Mac OS version >= 10.13.6
			} else {
				Logger.warn("Mac OS version detected: " + SystemUtils.OS_VERSION + " ... hardened runtime disabled!");
			}
		}
		
		// if entitlements.plist file not specified, use a default one
		if (entitlements == null) {	
			Logger.warn("Entitlements file not specified. Using defaults!");			
			entitlements = new File(assetsFolder, "entitlements.plist");
			VelocityUtils.render("mac/entitlements.plist.vtl", entitlements, this);
		} else if (!entitlements.exists()) {
			throw new Exception("Entitlements file doesn't exist: " + entitlements);
		}

		// prepare params array
		List<Object> codesignArgs = new ArrayList<>();
		codesignArgs.add("--force");
		if (!flags.isEmpty()) {
			codesignArgs.add("--options");
			codesignArgs.add(StringUtils.join(flags, ","));
		}
		codesignArgs.add("--deep");		
		codesignArgs.add("--entitlements");
		codesignArgs.add(entitlements);
		codesignArgs.add("--sign");
		codesignArgs.add(developerId);
		codesignArgs.add(appFile);
		
		// run codesign
		CommandUtils.execute("codesign", codesignArgs.toArray(new Object[codesignArgs.size()]));
	}

}
