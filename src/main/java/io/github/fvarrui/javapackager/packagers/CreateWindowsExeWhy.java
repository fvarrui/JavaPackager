package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.model.WindowsExeCreationTool;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.RcEdit;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import net.jsign.WindowsSigner;

/**
 * Creates Windows executable with WinRun4j
 */
public class CreateWindowsExeWhy extends AbstractCreateWindowsExe {

	public CreateWindowsExeWhy() {
		super(WindowsExeCreationTool.why);
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
		FileUtils.copyResourceToFile("/windows/JavaLauncher.exe", getGenericExe(), packager.getAssetsDir());

		// generates ini file
		File genericIni = new File(getOutputFolder(), "launcher.ini");
		VelocityUtils.render("windows/why-ini.vtl", genericIni, packager);
		Logger.info("INI file generated in " + genericIni.getAbsolutePath() + "!");

		// set exe metadata with rcedit
		RcEdit rcedit = new RcEdit(getOutputFolder());
		rcedit.setIcon(getGenericExe(), getGenericIcon());
		rcedit.setManifest(getGenericExe(), getGenericManifest());
		rcedit.setFileVersion(getGenericExe(), winConfig.getFileVersion());
		rcedit.setProductVersion(getGenericExe(), winConfig.getProductVersion());
		rcedit.setVersionString(getGenericExe(), "FileDescription", winConfig.getFileDescription());
		rcedit.setVersionString(getGenericExe(), "CompanyName", winConfig.getCompanyName());
		rcedit.setVersionString(getGenericExe(), "InternalName", winConfig.getInternalName());
		rcedit.setVersionString(getGenericExe(), "OriginalFilename", winConfig.getOriginalFilename());
		rcedit.setVersionString(getGenericExe(), "ProductName", winConfig.getProductName());

		// copies JAR to app folder
		FileUtils.copyFileToFolder(jarFile, appFolder);
		
		// copies ini file to app folder
		FileUtils.copyFileToFolder(genericIni, appFolder);

		// copies exe file to app folder with apps name
		FileUtils.copyFileToFile(getGenericExe(), executable);

		return createBootstrapScript(packager);
	}

}
