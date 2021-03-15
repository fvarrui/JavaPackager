package io.github.fvarrui.javapackager.maven;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.WindowsArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.FileUtils;

/**
 * Copies all dependencies to app folder on Maven context
 */
public class CreateWindowsExe extends WindowsArtifactGenerator {
	
	private File launch4jFolder;
	private File genericManifest;
	private File genericIcon;
	private File genericJar;
	private File genericExe;
	
	public CreateWindowsExe() {
		super("Windows EXE");
	}

	@Override
	protected File doApply(Packager packager) {
		
		WindowsPackager windowsPackager = (WindowsPackager) packager;
		
		List<String> vmArgs = windowsPackager.getVmArgs();
		WindowsConfig winConfig = windowsPackager.getWinConfig();
		File executable = windowsPackager.getExecutable();
		String mainClass = windowsPackager.getMainClass();
		boolean useResourcesAsWorkingDir = windowsPackager.isUseResourcesAsWorkingDir();
		boolean bundleJre = windowsPackager.getBundleJre();
		String jreDirectoryName = windowsPackager.getJreDirectoryName(); 
		String classpath = windowsPackager.getClasspath();
		String jreMinVersion = windowsPackager.getJreMinVersion();
		File jarFile = windowsPackager.getJarFile();
		
		try {
			// creates a folder only for launch4j assets
			createAssets(windowsPackager);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} 
		
		String jarPath = winConfig.isWrapJar() ? genericJar.getAbsolutePath() : jarFile.getName();
	
		List<Element> optsElements = vmArgs.stream().map(arg -> element("opt", arg)).collect(Collectors.toList());
		
		List<Element> jreElements = new ArrayList<>();
		jreElements.add(element("opts", optsElements.toArray(new Element[optsElements.size()])));
		jreElements.add(element("path", bundleJre ? jreDirectoryName : "%JAVA_HOME%"));
		if (!StringUtils.isBlank(jreMinVersion)) {
			jreElements.add(element("minVersion", jreMinVersion));
		}
		
		List<Element> pluginConfig = new ArrayList<>();
		pluginConfig.add(element("headerType", "" + winConfig.getHeaderType()));
		pluginConfig.add(element("jar", jarPath));
		pluginConfig.add(element("dontWrapJar", "" + !winConfig.isWrapJar()));
		pluginConfig.add(element("outfile", genericExe.getAbsolutePath()));
		pluginConfig.add(element("icon", genericIcon.getAbsolutePath()));
		pluginConfig.add(element("manifest", genericManifest.getAbsolutePath()));
		pluginConfig.add(
				element("classPath",
						element("mainClass", mainClass),
						element("preCp", classpath),
						element("addDependencies", "false")
					)
				);
		pluginConfig.add(element("chdir", useResourcesAsWorkingDir ? "." : ""));		
		pluginConfig.add(element("jre", jreElements.toArray(new Element[jreElements.size()])));
		pluginConfig.add(
					element("versionInfo", 
						element("fileVersion", winConfig.getFileVersion()),
						element("txtFileVersion", winConfig.getTxtFileVersion()),
						element("productVersion", winConfig.getProductVersion()),
						element("txtProductVersion", winConfig.getTxtProductVersion()),
						element("copyright", winConfig.getCopyright()),
						element("companyName", winConfig.getCompanyName()),
						element("fileDescription", winConfig.getFileDescription()),
						element("productName", winConfig.getProductName()),
						element("internalName", winConfig.getInternalName()),
						element("originalFilename", winConfig.getOriginalFilename()),
						element("trademarks", winConfig.getTrademarks()),
						element("language", winConfig.getLanguage())
					)
				);

		// invokes launch4j plugin to generate windows executable
		try {
			
			executeMojo(
					plugin(
							groupId("com.akathist.maven.plugins.launch4j"), 
							artifactId("launch4j-maven-plugin"),
							version("1.7.25")
					),
					goal("launch4j"),
					configuration(pluginConfig.toArray(new Element[pluginConfig.size()])),
					Context.getMavenContext().getEnv()
				);
			
			sign(genericExe, windowsPackager);
			
			FileUtils.copyFileToFile(genericExe, executable);
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}	

		return executable;
	}

	private void createAssets(WindowsPackager packager) throws Exception {
		
		File assetsFolder = packager.getAssetsFolder();
		File manifestFile = packager.getManifestFile();
		File iconFile = packager.getIconFile();
		File jarFile = packager.getJarFile();

		launch4jFolder = new File(assetsFolder, "launch4j");
		if (!launch4jFolder.exists()) launch4jFolder.mkdirs();
		
		genericManifest = new File(launch4jFolder, "app.exe.manifest");
		genericIcon = new File(launch4jFolder, "app.ico");
		genericJar = new File(launch4jFolder, "app.jar");
		genericExe = new File(launch4jFolder, "app.exe");
		
		FileUtils.copyFileToFile(manifestFile, genericManifest);
		FileUtils.copyFileToFile(iconFile, genericIcon);
		FileUtils.copyFileToFile(jarFile, genericJar);
		
	}

}
