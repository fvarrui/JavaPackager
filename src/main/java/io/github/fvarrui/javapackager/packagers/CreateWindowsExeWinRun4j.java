package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import net.jsign.WindowsSigner;
import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.model.Arch;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.model.WindowsExeCreationTool;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.JDKUtils;
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
		File appFolder = packager.getAppFolder();
		File jreDestinationFolder = packager.getJreDestinationFolder();
		boolean bundleJre = packager.getBundleJre();
		String vmLocation = packager.getWinConfig().getVmLocation();
		WindowsConfig winConfig = packager.getWinConfig();
		Arch arch = packager.getArch();
		
		if (winConfig.isWrapJar()) {
			Logger.warn("'wrapJar' property ignored when building EXE with " + getArtifactName());
		}
		
		createAssets(packager);

		// creates generic manifest
		FileUtils.copyFileToFile(manifestFile, getGenericManifest());

		// creates generic manifest
		FileUtils.copyFileToFile(iconFile, getGenericIcon());

		// checks if target architecture matches JRE arch
		if (bundleJre && !JDKUtils.isValidJRE(Platform.windows, arch, packager.getJreDestinationFolder())) {
			throw new Exception("Bundled JRE must match " + Platform.windows + " " + arch);
		}

		// creates generic exe
		if (arch == Arch.x86) {
			FileUtils.copyResourceToFile("/windows/WinRun4J.exe", getGenericExe());
		} else {
			FileUtils.copyResourceToFile("/windows/WinRun4J64.exe", getGenericExe());
		}

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
				
				// searchs for a valid jvm.dll file in JRE 
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

		// generates ini file		
		File genericIni = new File(getOutputFolder(), "app.ini");
		VelocityUtils.render("windows/ini.vtl", genericIni, packager);
		Logger.info("INI file generated in " + genericIni.getAbsolutePath() + "!");
		
		// copies ini file to app folder
		File iniFile = new File(appFolder, name + ".ini");
		FileUtils.copyFileToFile(genericIni, iniFile);

		// copies exe file to app folder with apps name
		FileUtils.copyFileToFile(getGenericExe(), executable);

		return createBootstrapScript(packager);
	}

}
