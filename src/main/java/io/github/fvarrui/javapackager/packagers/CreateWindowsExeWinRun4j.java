package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.model.WindowsExeCreationTool;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.JarUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.RcEdit;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates Windows executable with WinRun4j
 */
public class CreateWindowsExeWinRun4j extends AbstractCreateWindowsExe {
	
	private static final String [] JVM_DLL_PATHS = { 
			"bin/client/jvm.dll", 
			"bin/server/jvm.dll" 
		};

	public CreateWindowsExeWinRun4j() {
		super(WindowsExeCreationTool.winrun4j);
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
		File jreDestinationFolder = packager.getJreDestinationFolder();
		boolean bundleJre = packager.getBundleJre();
		String vmLocation = packager.getWinConfig().getVmLocation();
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
		FileUtils.copyResourceToFile("/windows/WinRun4J64.exe", getGenericExe());

		// uses vmLocation only if a JRE is bundled
		if (bundleJre) {
			
			// checks if vmLocation property is specified in winConfig
			if (!StringUtils.isBlank(vmLocation)) {
	
				// checks if specified vmLocation exists
				if (!new File(jreDestinationFolder, vmLocation).exists()) {
					throw new Exception("VM location '" + vmLocation + "' does not exist");
				}
				
				vmLocation = vmLocation.replaceAll("/", "\\\\");
	
			} else {
				
				// searchs for   valid jvm.dll file in JRE 
				Optional<File> jvmDllFile = Arrays.asList(JVM_DLL_PATHS)
					.stream()
					.map(path -> new File(jreDestinationFolder, path))
					.filter(file -> file.exists())
					.findFirst();
				
				// checks if found jvm.dll  
				if (!jvmDllFile.isPresent()) {
					throw new Exception("jvm.dll not found!");
				}
			
				// relativize jvm.dll path to JRE, in order to use it a "vm.location" 
				Path jreDestinationPath = jreDestinationFolder.toPath();
				Path jvmDllPath = jvmDllFile.get().toPath();
				vmLocation = jreDestinationPath.relativize(jvmDllPath).toString();				
				
			}

			// sets vmLocation in winConfig, so it will be used when rendering INI file
			packager.getWinConfig().setVmLocation(vmLocation);
			
			Logger.info("Using 'vmLocation=" + vmLocation + "'!");
			
		}

		// generates ini file		
		File genericIni = new File(getOutputFolder(), "app.ini");
		VelocityUtils.render("windows/ini.vtl", genericIni, packager);
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

		// copies JAR to libs folder
		FileUtils.copyFileToFolder(jarFile, appFolder);

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
