package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.AbstractCreateWindowsExe;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.FileUtils;

/**
 * Creates Windows native executable on Gradle context
 */
public class CreateWindowsExeLaunch4j extends AbstractCreateWindowsExe {
	
	public CreateWindowsExeLaunch4j() {
		super("launch4j");
	}
	
	@Override
	protected File doApply(WindowsPackager packager) throws Exception {

		List<String> vmArgs = packager.getVmArgs();
		WindowsConfig winConfig = packager.getWinConfig();
		File executable = packager.getExecutable();
		String mainClass = packager.getMainClass();
		boolean useResourcesAsWorkingDir = packager.isUseResourcesAsWorkingDir();
		boolean bundleJre = packager.getBundleJre();
		String jreDirectoryName = packager.getJreDirectoryName();
		String jreMinVersion = packager.getJreMinVersion();
		File jarFile = packager.getJarFile();
		File appFolder = packager.getAppFolder();

		createAssets(packager); // creates a folder only for launch4j assets

		// copies JAR to app folder
		String jarPath;
		if (winConfig.isWrapJar()) {
			jarPath = getGenericJar().getAbsolutePath();
		} else {
			FileUtils.copyFileToFolder(jarFile, appFolder);
			jarPath = jarFile.getName();
		}

		Launch4jLibraryTask l4jTask = Context.getGradleContext().getLibraryTask();
		l4jTask.getOutputs().upToDateWhen(task -> false);
		l4jTask.setHeaderType(winConfig.getHeaderType().toString());
		l4jTask.setJar(jarPath);
		l4jTask.setDontWrapJar(!winConfig.isWrapJar());
		l4jTask.setOutfile(getGenericExe().getName());
		l4jTask.setIcon(getGenericIcon().getAbsolutePath());
		l4jTask.setManifest(getGenericManifest().getAbsolutePath());
		l4jTask.setMainClassName(mainClass);
		l4jTask.setClasspath(new HashSet<>(packager.getClasspaths()));
		l4jTask.setChdir(useResourcesAsWorkingDir ? "." : "");
		if (bundleJre) {
			l4jTask.setBundledJrePath(jreDirectoryName);
		}
		if (!StringUtils.isBlank(jreMinVersion)) {
			l4jTask.setJreMinVersion(jreMinVersion);
		}
		l4jTask.getJvmOptions().addAll(vmArgs);
		l4jTask.setVersion(winConfig.getProductVersion());
		l4jTask.setTextVersion(winConfig.getTxtProductVersion());
		l4jTask.setCopyright(winConfig.getCopyright());
		l4jTask.setCompanyName(winConfig.getCompanyName());
		l4jTask.setFileDescription(winConfig.getFileDescription());
		l4jTask.setProductName(winConfig.getProductName());
		l4jTask.setInternalName(winConfig.getInternalName());
		l4jTask.setTrademarks(winConfig.getTrademarks());
		l4jTask.setLanguage(winConfig.getLanguage());
		l4jTask.getActions().forEach(action -> action.execute(l4jTask));

		sign(getGenericExe(), packager);

		FileUtils.copyFileToFile(getGenericExe(), executable);

		return createBootstrapScript(packager);
	}

}
