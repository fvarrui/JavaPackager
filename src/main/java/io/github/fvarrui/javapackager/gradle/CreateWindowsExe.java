package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.List;

import org.gradle.api.Project;

import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.FileUtils;

/**
 * Creates Windows native executable on Gradle context
 */
public class CreateWindowsExe extends ArtifactGenerator {
	
	public CreateWindowsExe() {
		super("Windows EXE");
	}

	@Override
	public File apply(Packager packager) throws Exception {
		
		WindowsPackager WindowsPackager = (WindowsPackager) packager;
		
		Project project = Context.getGradleContext().getProject();
		List<String> vmArgs = WindowsPackager.getVmArgs();
		WindowsConfig winConfig = WindowsPackager.getWinConfig();
		String jarPath = WindowsPackager.getJarPath();
		File executable = WindowsPackager.getExecutable();
		File iconFile = WindowsPackager.getIconFile();
		File manifestFile = WindowsPackager.getManifestFile();
		String mainClass = WindowsPackager.getMainClass();
		boolean useResourcesAsWorkingDir = WindowsPackager.isUseResourcesAsWorkingDir();
		boolean bundleJre = WindowsPackager.getBundleJre();
		String jreDirectoryName = WindowsPackager.getJreDirectoryName();
		
		Launch4jLibraryTask l4jTask = project.getTasks().create("jplaunch4j", Launch4jLibraryTask.class);
		l4jTask.setHeaderType(winConfig.getHeaderType().toString());
		l4jTask.setJar(jarPath);
		l4jTask.setDontWrapJar(!winConfig.isWrapJar());
		l4jTask.setOutfile(executable.getName());
		l4jTask.setIcon(iconFile.getAbsolutePath());
		l4jTask.setManifest(manifestFile.getAbsolutePath());
		l4jTask.setMainClassName(mainClass);
		l4jTask.setChdir(useResourcesAsWorkingDir ? "." : "");
		l4jTask.setBundledJrePath(bundleJre ? jreDirectoryName : "%JAVA_HOME%");
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
		l4jTask.setLibraryDir("");
		l4jTask.getActions().forEach(action -> action.execute(l4jTask));

		File generatedExe = new File(project.getBuildDir(), "launch4j/" + executable.getName());
		
		FileUtils.copyFileToFile(generatedExe, executable);
		
		return executable;
	}

}
