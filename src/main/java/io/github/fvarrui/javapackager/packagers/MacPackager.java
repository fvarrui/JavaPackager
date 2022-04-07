package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

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
			executable.setExecutable(true, false);
			Logger.info("Startup script file created in " + executable.getAbsolutePath());

		} else {

			// sets startup file
			this.executable = new File(macOSFolder, "universalJavaApplicationStub");
			Logger.info("Using " + executable.getAbsolutePath() + " as startup script");

		}

		// copies universalJavaApplicationStub startup file to boot java app
		File appStubFile = new File(macOSFolder, "universalJavaApplicationStub");
		File tempAppStubFile = new File(macOSFolder, "universalJavaApplicationStub.sh");
		FileUtils.copyResourceToFile("/mac/universalJavaApplicationStub", tempAppStubFile, true);
		FileUtils.processFileContent(tempAppStubFile, content -> {
			if (!macConfig.isRelocateJar()) {
				content = content.replaceAll("/Contents/Resources/Java", "/Contents/Resources");
			}
			content = content.replaceAll("\\$\\{info.name\\}", this.name);
			return content;
		});

		// we now need to convert to a binary using shc https://github.com/neurobin/shc
		// shc needs to be installed so it will be available on commandline
		// if the user needs this to be done, for example if he uses JFileChooser in his project.
		String[] env = new String[3];
		env[0] = "SHELL=/bin/bash";
		env[1] = "TERM=xterm";
		env[2] = "LC_ALL=.utf8";

		String[] compileStubCommandString = new String[6];
		compileStubCommandString[0] = "shc";
		compileStubCommandString[1] = "-r";
		compileStubCommandString[2] = "-f"; // let op is symbolic link naar juiste versie
		compileStubCommandString[3] = tempAppStubFile.getAbsolutePath();
		compileStubCommandString[4] = "-o";
		compileStubCommandString[5] = appStubFile.getAbsolutePath();

		String[] removemScriptCommandString = new String[2];
		removemScriptCommandString[0] = "rm";
		removemScriptCommandString[1] =  tempAppStubFile.getAbsolutePath();

		String[] removemBuildArtefactCommandString = new String[2];
		removemBuildArtefactCommandString[0] = "rm";
		removemBuildArtefactCommandString[1] =  tempAppStubFile.getAbsolutePath() + ".x.c";

		Runtime rt = Runtime.getRuntime();

		try {
			Process compileProcess = rt.exec(compileStubCommandString, env, macOSFolder);
			compileProcess.waitFor();

			Process removeScriptProcess = rt.exec(removemScriptCommandString, env);
			removeScriptProcess.waitFor();

			Process removeBuildArtefactProcess = rt.exec(removemBuildArtefactCommandString, env);
			removeBuildArtefactProcess.waitFor();

			Logger.info("Succesfully compiled universalApplicationStub.sh bash startup script");
		} catch (IOException e ) {
			Logger.info("could not compile universalApplicationStub IO exception: " + e.getMessage());
		} catch (InterruptedException e) {
			Logger.info("could not compile universalApplicationStub Interrupted exception: " + e.getMessage());
		}

		appStubFile.setExecutable(true, false);

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

		// codesigns app folder
		if (!Platform.mac.isCurrentPlatform()) {
			Logger.warn("Generated app could not be signed due to current platform is " + Platform.getCurrentPlatform());
		} else if (!getMacConfig().isCodesignApp()) {
			Logger.warn("App codesigning disabled");
		} else {
			codesign(this.macConfig.getDeveloperId(), this.macConfig.getEntitlements(), this.appFile);
		}

		return appFile;
	}

	private void codesign(String developerId, File entitlements, File appFile)
			throws IOException, CommandLineException {

		List<String> flags = new ArrayList<>();
		if (VersionUtils.compareVersions("10.13.6", SystemUtils.OS_VERSION) >= 0) {
			flags.add("runtime"); // enable hardened runtime if Mac OS version >= 10.13.6
		} else {
			Logger.warn("Mac OS version detected: " + SystemUtils.OS_VERSION + " ... hardened runtime disabled!");
		}

		List<Object> codesignArgs = new ArrayList<>();
		codesignArgs.add("--force");
		if (!flags.isEmpty()) {
			codesignArgs.add("--options");
			codesignArgs.add(StringUtils.join(flags, ","));
		}
		codesignArgs.add("--deep");
		if (entitlements == null) {
			Logger.warn("Entitlements file not specified");
		} else if (!entitlements.exists()) {
			Logger.warn("Entitlements file doesn't exist: " + entitlements);
		} else {
			codesignArgs.add("--entitlements");
			codesignArgs.add(entitlements);
		}
		codesignArgs.add("--sign");
		codesignArgs.add(developerId);
		codesignArgs.add(appFile);
		CommandUtils.execute("codesign", codesignArgs.toArray(new Object[codesignArgs.size()]));
	}

}
