package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.fvarrui.javapackager.PackageTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import io.github.fvarrui.javapackager.model.MacStartup;
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

	public MacPackager(PackageTask task) {
		super(task);
	}

	public File getAppFile() {
		return appFile;
	}

	@Override
	public void doInit() throws Exception {

		this.task.getMacConfig().setDefaults(this);

		// FIX useResourcesAsWorkingDir=false doesn't work fine on Mac OS (option
		// disabled)
		if (!this.task.isUseResourcesAsWorkingDir()) {
			this.task.useResourcesAsWorkingDir(true);
			Logger.warn(
					"'useResourcesAsWorkingDir' property disabled on Mac OS (useResourcesAsWorkingDir is always true)");
		}

	}

	@Override
	protected void doCreateAppStructure() throws Exception {

		// initializes the references to the app structure folders
		this.appFile = new File(appFolder, task.getAppName() + ".app");
		this.contentsFolder = new File(appFile, "Contents");
		this.resourcesFolder = new File(contentsFolder, "Resources");
		this.javaFolder = new File(resourcesFolder, this.task.getMacConfig().isRelocateJar() ? "Java" : "");
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
		this.jreDestinationFolder = new File(contentsFolder, "PlugIns/" + task.getJreDirectoryName() + "/Contents/Home");
		this.resourcesDestinationFolder = resourcesFolder;

	}

	/**
	 * Creates a native MacOS app bundle
	 */
	@Override
	public File doCreateApp() throws Exception {

		// copies jarfile to Java folder
		FileUtils.copyFileToFolder(jarFile, javaFolder);

		processStartupScript();

		processClasspath();

		processInfoPlistFile();

		processProvisionProfileFile();

		codesign();

		return appFile;
	}

	private void processStartupScript() throws Exception {
		
		if (task.getAdministratorRequired()) {

			// We need a helper script ("startup") in this case,
			// which invokes the launcher script/ executable with administrator rights.
			// TODO: admin script depends on launcher file name 'universalJavaApplicationStub'

			// sets startup file
			this.executable = new File(macOSFolder, "startup");

			// creates startup file to boot java app
			VelocityUtils.render("mac/startup.vtl", executable, this);
			
		} else {

			File launcher = task.getMacConfig().getCustomLauncher();
			if(launcher != null && launcher.canRead() && launcher.isFile()){
				FileUtils.copyFileToFolder(launcher, macOSFolder);
				this.executable = new File(macOSFolder, launcher.getName());
			} else {
				this.executable = preparePrecompiledStartupStub();
			}
		}
		
		executable.setExecutable(true, false);
		Logger.info("Startup script file created in " + executable.getAbsolutePath());
	}

	private void processClasspath() {
		// TODO: Why are we doing this here? I do not see any usage of 'classpath' or 'classpaths' here.
		task.classpath((task.getMacConfig().isRelocateJar() ? "Java/" : "") + this.jarFile.getName() + (task.getClasspath() != null ? ":" + task.getClasspath() : ""));
		classpaths = Arrays.asList(task.getClasspath().split("[:;]"));
		if (!task.isUseResourcesAsWorkingDir()) {
			classpaths = classpaths
					.stream()
					.map(cp -> new File(cp).isAbsolute() ? cp : "$ResourcesFolder/" + cp)
					.collect(Collectors.toList());
		}
		task.classpath(StringUtils.join(classpaths, ":"));
	}

	/**
	 * Creates and writes the Info.plist file if no custom file is specified.
	 * @throws Exception if anything goes wrong
	 */
	private void processInfoPlistFile() throws Exception {
		File infoPlistFile = new File(contentsFolder, "Info.plist");
		if(task.getMacConfig().getCustomInfoPlist() != null && task.getMacConfig().getCustomInfoPlist().isFile() && task.getMacConfig().getCustomInfoPlist().canRead()){
			FileUtils.copyFileToFile(task.getMacConfig().getCustomInfoPlist(), infoPlistFile);
		} else {
			VelocityUtils.render("mac/Info.plist.vtl", infoPlistFile, this);
			XMLUtils.prettify(infoPlistFile);
		}
		Logger.info("Info.plist file created in " + infoPlistFile.getAbsolutePath());
	}

	private void codesign() throws Exception {
		if (!Platform.mac.isCurrentPlatform()) {
			Logger.warn("Generated app could not be signed due to current platform is " + Platform.getCurrentPlatform());
		} else if (!task.getMacConfig().isCodesignApp()) {
			Logger.warn("App codesigning disabled");
		} else {
			codesign(task.getMacConfig().getDeveloperId(), task.getMacConfig().getEntitlements(), this.appFile);
		}
	}

	private void processProvisionProfileFile() throws Exception {
		if (task.getMacConfig().getProvisionProfile() != null && task.getMacConfig().getProvisionProfile().isFile() && task.getMacConfig().getProvisionProfile().canRead()) {
			// file name must be 'embedded.provisionprofile'
			File provisionProfile = new File(contentsFolder, "embedded.provisionprofile");
			FileUtils.copyFileToFile(task.getMacConfig().getProvisionProfile(), provisionProfile);
			Logger.info("Provision profile file created from " + "\n" +
					task.getMacConfig().getProvisionProfile() + " to \n" +
					provisionProfile.getAbsolutePath());
		}
	}

	private File preparePrecompiledStartupStub() throws Exception {
		// sets startup file
		File appStubFile = new File(macOSFolder, "universalJavaApplicationStub");
		String universalJavaApplicationStubResource = null;
		switch (task.getMacConfig().getMacStartup()) {
			case UNIVERSAL:	universalJavaApplicationStubResource = "universalJavaApplicationStub"; break;
			case X86_64:	universalJavaApplicationStubResource = "universalJavaApplicationStub.x86_64"; break;
			case ARM64: 	universalJavaApplicationStubResource = "universalJavaApplicationStub.arm64"; break;
			case SCRIPT: 	universalJavaApplicationStubResource = "universalJavaApplicationStub.sh"; break;
		}
		// unixStyleNewLinux=true if startup is a script (this will replace '\r\n' with '\n')
		FileUtils.copyResourceToFile("/mac/" + universalJavaApplicationStubResource, appStubFile, task.getMacConfig().getMacStartup() == MacStartup.SCRIPT);
		return appStubFile;
	}

	private void codesign(String developerId, File entitlements, File appFile) throws Exception {

		// checks --option flags
		List<String> flags = new ArrayList<>();
		if (task.getMacConfig().isHardenedCodesign()) {
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
