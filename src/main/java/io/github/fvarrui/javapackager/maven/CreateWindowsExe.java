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
import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;

/**
 * Copies all dependencies to app folder on Maven context
 * 
 */
public class CreateWindowsExe extends ArtifactGenerator {
	
	public CreateWindowsExe() {
		super("Windows EXE");
	}

	@Override
	public File apply(Packager packager) {
		
		WindowsPackager windowsPackager = (WindowsPackager) packager;
		
		List<String> vmArgs = windowsPackager.getVmArgs();
		WindowsConfig winConfig = windowsPackager.getWinConfig();
		String jarPath = windowsPackager.getJarPath();
		File executable = windowsPackager.getExecutable();
		File iconFile = windowsPackager.getIconFile();
		File manifestFile = windowsPackager.getManifestFile();
		String mainClass = windowsPackager.getMainClass();
		boolean useResourcesAsWorkingDir = windowsPackager.isUseResourcesAsWorkingDir();
		boolean bundleJre = windowsPackager.getBundleJre();
		String jreDirectoryName = windowsPackager.getJreDirectoryName(); 
		String classpath = windowsPackager.getClasspath();
		String jreMinVersion = windowsPackager.getJreMinVersion();
	
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
		pluginConfig.add(element("outfile", executable.getAbsolutePath()));
		pluginConfig.add(element("icon", iconFile.getAbsolutePath()));
		pluginConfig.add(element("manifest", manifestFile.getAbsolutePath()));
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
			
		} catch (MojoExecutionException e) {

			throw new RuntimeException(e);
			
		}	

		return executable;
	}

}
