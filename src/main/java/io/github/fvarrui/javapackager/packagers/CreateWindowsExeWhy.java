package io.github.fvarrui.javapackager.packagers;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Creates Windows executable with WinRun4j
 */
public class CreateWindowsExeWhy extends AbstractCreateWindowsExe {

	public CreateWindowsExeWhy() {
		super("why");
	}

	@Override
	public boolean skip(WindowsPackager packager) {

		if (!packager.getPlatform().isCurrentPlatform()) {
			Logger.error(getArtifactName() + " cannot be generated with Why due to the target platform (" + packager.getPlatform() + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return true;
		}
		
		return false;
	}

	@Override
	protected File doApply(WindowsPackager packager) throws Exception {

		String name = packager.getName();
		File executable = packager.getExecutable();
		File manifestFile = packager.getManifestFile();
		File iconFile = packager.getIconFile();
		File appFolder = packager.getAppFolder();
		
		createAssets(packager);

		// creates generic manifest
		FileUtils.copyFileToFile(manifestFile, getGenericManifest());

		// creates generic manifest
		FileUtils.copyFileToFile(iconFile, getGenericIcon());

		// creates generic exe
		FileUtils.copyResourceToFile("/windows/JavaLauncher.exe", getGenericExe());

		// copies rcedit command line tool (needed to manipulate exe)
		File rcedit = new File(getOutputFolder(), "rcedit.exe");
		FileUtils.copyResourceToFile("/windows/rcedit-x64.exe", rcedit);

		// generates ini file
		File genericIni = new File(getOutputFolder(), "launcher.ini");
		VelocityUtils.render("windows/why-ini.vtl", genericIni, packager);
		Logger.info("INI file generated in " + genericIni.getAbsolutePath() + "!");

		// process EXE with rcedit-x64.exe
		CommandUtils.execute(rcedit.getAbsolutePath(), getGenericExe(), "--set-icon", getGenericIcon());
		CommandUtils.execute(rcedit.getAbsolutePath(), getGenericExe(), "--application-manifest", getGenericManifest());
		CommandUtils.execute(rcedit.getAbsolutePath(), getGenericExe(), "--set-version-string", "FileDescription", name);

		// generates why properties
		/*File propertiesFile = new File(getOutputFolder(), "launcher.ini");
		Properties properties = new Properties();
		properties.setProperty("mainclass", mainClass);
		properties.store(new FileOutputStream(propertiesFile), "Why Java Launcher Properties");*/

		// copies ini file to app folder
		File iniFile = new File(appFolder, "launcher.ini");
		FileUtils.copyFileToFile(genericIni, iniFile);

		// signs generated exe file
		sign(getGenericExe(), packager);

		// copies exe file to app folder with apps name
		FileUtils.copyFileToFile(getGenericExe(), executable);

		return createBootstrapScript(packager);
	}

}
