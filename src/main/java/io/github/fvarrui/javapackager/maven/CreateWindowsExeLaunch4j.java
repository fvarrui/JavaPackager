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

import net.jsign.WindowsSigner;
import org.apache.commons.lang3.StringUtils;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.model.Arch;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.model.WindowsExeCreationTool;
import io.github.fvarrui.javapackager.packagers.AbstractCreateWindowsExe;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates Windows executable with Maven
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
		String classpath = packager.getClasspath();
		String jreMinVersion = packager.getJreMinVersion();
		File jarFile = packager.getJarFile();
		File appFolder = packager.getAppFolder();
		Arch arch = packager.getArch();
		
		createAssets(packager);
		
		// warns about architecture
		if (arch != Arch.x86) {
			Logger.warn("Launch4J only can generate 32-bit executable");
		}
		
		// copies JAR to app folder
		String jarPath;
		if (winConfig.isWrapJar()) {
			jarPath = getGenericJar().getAbsolutePath();
		} else {
			FileUtils.copyFileToFolder(jarFile, appFolder);
			jarPath = jarFile.getName();
		}

        List<Element> jreElements = new ArrayList<>();
		jreElements.add(element("opts", vmArgs.stream().map(arg -> element("opt", arg)).toArray(Element[]::new)));
		jreElements.add(element("path", bundleJre ? jreDirectoryName : "%JAVA_HOME%;%PATH%"));
		if (!StringUtils.isBlank(jreMinVersion)) {
			jreElements.add(element("minVersion", jreMinVersion));
		}
		
		List<Element> pluginConfig = new ArrayList<>();
		pluginConfig.add(element("headerType", "" + winConfig.getHeaderType()));
		pluginConfig.add(element("jar", jarPath));
		pluginConfig.add(element("dontWrapJar", "" + !winConfig.isWrapJar()));
		pluginConfig.add(element("outfile", getGenericExe().getAbsolutePath()));
		pluginConfig.add(element("icon", getGenericIcon().getAbsolutePath()));
		pluginConfig.add(element("manifest", getGenericManifest().getAbsolutePath()));
		pluginConfig.add(
				element("classPath",
						element("mainClass", mainClass),
						element("preCp", classpath),
						element("addDependencies", "false")
					)
				);
		pluginConfig.add(element("chdir", useResourcesAsWorkingDir ? "." : ""));		
		pluginConfig.add(element("jre", jreElements.toArray(new Element[0])));
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
							version("2.4.1")
					),
					goal("launch4j"),
					configuration(pluginConfig.toArray(new Element[0])),
					Context.getMavenContext().getEnv()
				);

			FileUtils.copyFileToFile(getGenericExe(), executable);
						
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		return createBootstrapScript(packager);
	}

}
