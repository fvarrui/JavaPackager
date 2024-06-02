package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import net.jsign.WindowsSigner;
import org.apache.commons.lang3.StringUtils;

import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.model.WindowsExeCreationTool;
import io.github.fvarrui.javapackager.packagers.AbstractCreateWindowsExe;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.FileUtils;

/**
 * Creates Windows native executable on Gradle context
 */
public class CreateWindowsExeLaunch4j extends AbstractCreateWindowsExe {
	
	public CreateWindowsExeLaunch4j() {
		super(WindowsExeCreationTool.launch4j);
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
		l4jTask.getDuplicatesStrategy().set(Context.getGradleContext().getDuplicatesStrategy());
		l4jTask.getOutputs().upToDateWhen(task -> false);
		l4jTask.getHeaderType().set(winConfig.getHeaderType().toString());
		l4jTask.getJarFiles().set(Context.getGradleContext().getProject().files(jarPath));
		l4jTask.getDontWrapJar().set(!winConfig.isWrapJar());
		l4jTask.getOutfile().set(getGenericExe().getName());
		l4jTask.getIcon().set(getGenericIcon().getAbsolutePath());
		l4jTask.getManifest().set(getGenericManifest().getAbsolutePath());
		l4jTask.getMainClassName().set(mainClass);
		l4jTask.getClasspath().set(new HashSet<>(packager.getClasspaths()));
		l4jTask.getChdir().set(useResourcesAsWorkingDir ? "." : "");		
		if (bundleJre) {
			l4jTask.getBundledJrePath().set(jreDirectoryName);
		}
		if (!StringUtils.isBlank(jreMinVersion)) {
			l4jTask.getJreMinVersion().set(jreMinVersion);
		}
		l4jTask.getJvmOptions().addAll(vmArgs);
		l4jTask.getVersion().set(winConfig.getProductVersion());
		l4jTask.getTextVersion().set(winConfig.getTxtProductVersion());
		l4jTask.getCopyright().set(winConfig.getCopyright());
		l4jTask.getCompanyName().set(winConfig.getCompanyName());
		l4jTask.getFileDescription().set(winConfig.getFileDescription());
		l4jTask.getProductName().set(winConfig.getProductName());
		l4jTask.getInternalName().set(winConfig.getInternalName());
		l4jTask.getTrademarks().set(winConfig.getTrademarks());
		l4jTask.getLanguage().set(winConfig.getLanguage());		
		l4jTask.getActions().forEach(action -> action.execute(l4jTask));

		FileUtils.copyFileToFile(getGenericExe(), executable);

		return createBootstrapScript(packager);
	}

}
