package io.github.fvarrui.javapackager.packagers;

import static org.apache.commons.collections4.CollectionUtils.addIgnoreNull;
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

import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.maven.MavenContext;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import io.github.fvarrui.javapackager.utils.XMLUtils;

public class WindowsPackager extends Packager {
	
	@Override
	public void doInit() throws Exception {
		
		// sets windows config default values
		this.winConfig.setDefaults(this);
		
	}

	@Override
	protected void doCreateAppStructure() throws Exception {

		// sets common folders
		this.executableDestinationFolder = appFolder;
		this.jarFileDestinationFolder = appFolder;
		this.jreDestinationFolder = new File(appFolder, jreDirectoryName);
		this.resourcesDestinationFolder = appFolder;

	}
	
	/**
	 * Creates a Windows app file structure with native executable
	 */
	@Override
	public File doCreateApp() throws Exception {
		
		Logger.infoIndent("Creating windows EXE ...");

		// copies JAR to app folder
		String jarPath = jarFile.getAbsolutePath();
		if (!winConfig.isWrapJar()) {
			FileUtils.copyFileToFolder(jarFile, appFolder);
			jarPath = jarFile.getName();
		}
		
		// generates manifest file to require administrator privileges from velocity template
		File manifestFile = new File(assetsFolder, name + ".exe.manifest");
		VelocityUtils.render("windows/exe.manifest.vtl", manifestFile, this);
		Logger.info("Exe manifest file generated in " + manifestFile.getAbsolutePath() + "!");

		// sets executable file
		this.executable = new File(appFolder, name + ".exe");

		// prepares launch4j plugin configuration
		
		List<Element> optsElements = vmArgs.stream().map(arg -> element("opt", arg)).collect(Collectors.toList());
	
		List<Element> pluginConfig = new ArrayList<>();
		pluginConfig.add(element("headerType", "" + winConfig.getHeaderType()));
		pluginConfig.add(element("jar", jarPath));
		pluginConfig.add(element("dontWrapJar", "" + !winConfig.isWrapJar()));
		pluginConfig.add(element("outfile", executable.getAbsolutePath()));
		pluginConfig.add(element("icon", iconFile.getAbsolutePath()));
		pluginConfig.add(element("manifest", manifestFile.getAbsolutePath()));
		pluginConfig.add(
				element("classPath",
						element("mainClass", mainClass)
					)
				);
		pluginConfig.add(element("chdir", useResourcesAsWorkingDir ? "." : ""));		
		pluginConfig.add(
					element("jre",
						element("path", bundleJre ? jreDirectoryName : "%JAVA_HOME%"),
						element("opts", optsElements.toArray(new Element[optsElements.size()]))
					)
				);
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
		executeMojo(
				plugin(
						groupId("com.akathist.maven.plugins.launch4j"), 
						artifactId("launch4j-maven-plugin"),
						version("1.7.25")
				),
				goal("launch4j"),
				configuration(pluginConfig.toArray(new Element[pluginConfig.size()])),
				MavenContext.getEnv()
			);

		Logger.infoUnindent("Windows EXE file created in " + executable + "!");		
		
		return appFolder;
	}

	/**
	 * Creates a Setup installer file including all app folder's content only for
	 * Windows so app could be easily distributed
	 */
	@Override
	public void doGenerateInstallers(List<File> installers) throws Exception {

		addIgnoreNull(installers, generateSetup());

		addIgnoreNull(installers, generateMsi());
		
	}

	/**
	 * Creates a MSI file including all app folder's content only for
	 * Windows so app could be easily distributed
	 */
	private File generateMsi() throws Exception {
		if (!winConfig.isGenerateMsi()) return null;

		Logger.infoIndent("Generating MSI file ...");
		
		// generates WXS file from velocity template
		File wxsFile = new File(assetsFolder, name + ".wxs");
		VelocityUtils.render("windows/wxs.vtl", wxsFile, this);
		Logger.info("WXS file generated in " + wxsFile + "!");

		// pretiffy wxs
		XMLUtils.prettify(wxsFile);
	
		// candle wxs file
		Logger.info("Compiling file " + wxsFile);
		File wixobjFile = new File(assetsFolder, name + ".wixobj");
		CommandUtils.execute("candle", "-out", wixobjFile, wxsFile);
		Logger.info("WIXOBJ file generated in " + wixobjFile +  "!");

		// lighting wxs file
		Logger.info("Linking file " + wixobjFile);
		File msiFile = new File(outputDirectory, name + "_" + version + ".msi");
		CommandUtils.execute("light", "-spdb", "-out", msiFile, wixobjFile);

		// setup file
		if (!msiFile.exists()) {
			throw new Exception("MSI installer file generation failed!");
		}
		
		Logger.infoUnindent("MSI file generated!");
		
		return msiFile;
	}

	private File generateSetup() throws Exception {
		if (!winConfig.isGenerateSetup()) return null;
		
		Logger.infoIndent("Generating setup file ...");
		
		// copies ico file to assets folder
		FileUtils.copyFileToFolder(iconFile, assetsFolder);
		
		// generates iss file from velocity template
		File issFile = new File(assetsFolder, name + ".iss");
		VelocityUtils.render("windows/iss.vtl", issFile, this);

		// generates windows installer with inno setup command line compiler
		CommandUtils.execute("iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + name + "_" + version, issFile);
		
		// setup file
		File setupFile = new File(outputDirectory, name + "_" + version + ".exe");
		if (!setupFile.exists()) {
			throw new Exception("Windows setup file generation failed!");
		}
		
		Logger.infoUnindent("Setup file generated!");
		
		return setupFile;
	}
	
}
