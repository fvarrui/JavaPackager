package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.JarUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates Windows executable with WinRun4j
 */
public class CreateWindowsExeWinRun4j extends AbstractCreateWindowsExe {

	public CreateWindowsExeWinRun4j() {
		super("winrun4j");
	}

	@Override
	public boolean skip(WindowsPackager packager) {

		if (!packager.getPlatform().isCurrentPlatform()) {
			Logger.error(getArtifactName() + " cannot be generated with WinRun4J due to the target platform (" + packager.getPlatform() + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return true;
		}
		
		return false;
	}

	@Override
	protected File doApply(WindowsPackager packager) throws Exception {

		String name = packager.getName();
		File executable = packager.getExecutable();
		File jarFile = packager.getJarFile();
		File manifestFile = packager.getManifestFile();
		File iconFile = packager.getIconFile();
		File libsFolder = packager.getLibsFolder();
		File appFolder = packager.getAppFolder();
		String mainClass = packager.getMainClass();
		
		createAssets(packager);

		// creates generic manifest
		FileUtils.copyFileToFile(manifestFile, getGenericManifest());

		// creates generic manifest
		FileUtils.copyFileToFile(iconFile, getGenericIcon());

		// creates generic exe
		FileUtils.copyResourceToFile("/windows/WinRun4J64.exe", getGenericExe());

		// copies rcedit command line tool (needed to manipulate exe)
		File rcedit = new File(getOutputFolder(), "rcedit.exe");
		FileUtils.copyResourceToFile("/windows/rcedit-x64.exe", rcedit);

		// generates ini file
		File genericIni = new File(getOutputFolder(), "app.ini");
		VelocityUtils.render("windows/ini.vtl", genericIni, packager);
		Logger.info("INI file generated in " + genericIni.getAbsolutePath() + "!");

		// process EXE with rcedit-x64.exe
		CommandUtils.execute(rcedit, getGenericExe(), "--set-icon", getGenericIcon());
		CommandUtils.execute(rcedit, getGenericExe(), "--application-manifest", getGenericManifest());

		// creates libs folder if it doesn't exist
		if (libsFolder == null) {
			libsFolder = FileUtils.mkdir(appFolder, "libs");
		}

		// copies JAR to libs folder
		FileUtils.copyFileToFolder(jarFile, libsFolder);

		// copies winrun4j launcher helper library (needed to work around
		File winrun4jJar = new File(libsFolder, "winrun4j-launcher.jar");
		FileUtils.copyResourceToFile("/windows/winrun4j-launcher.jar", winrun4jJar);

		// generates winrun4j properties pointing to main class
		File propertiesFile = new File(getOutputFolder(), "winrun4j.properties");
		Properties properties = new Properties();
		properties.setProperty("main.class", mainClass);
		properties.store(new FileOutputStream(propertiesFile), "WinRun4J Helper Launcher Properties");

		// copies winrun4j properties to launcher jar
		JarUtils.addFileToJar(winrun4jJar, propertiesFile);

		// copies ini file to app folder
		File iniFile = new File(appFolder, name + ".ini");
		FileUtils.copyFileToFile(genericIni, iniFile);

		// signs generated exe file
		sign(getGenericExe(), packager);

		// copies exe file to app folder with apps name
		FileUtils.copyFileToFile(getGenericExe(), executable);

		return createBootstrapScript(packager);
	}

}
