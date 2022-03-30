package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

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
		File jarFile = packager.getJarFile();
		WindowsConfig winConfig = packager.getWinConfig(); 
		
		if (winConfig.isWrapJar()) {
			Logger.warn("'wrapJar' property ignored when building EXE with " + getArtifactName());
		}
		
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
		CommandUtils.execute(rcedit, getGenericExe(), "--set-icon", getGenericIcon());
		CommandUtils.execute(rcedit, getGenericExe(), "--application-manifest", getGenericManifest());
		CommandUtils.execute(rcedit, getGenericExe(), "--set-version-string", "FileDescription", name);

		// copies JAR to app folder
		FileUtils.copyFileToFolder(jarFile, appFolder);
		
		// copies ini file to app folder
		FileUtils.copyFileToFolder(genericIni, appFolder);

		// signs generated exe file
		sign(getGenericExe(), packager);

		// copies exe file to app folder with apps name
		FileUtils.copyFileToFile(getGenericExe(), executable);

		return createBootstrapScript(packager);
	}

}
