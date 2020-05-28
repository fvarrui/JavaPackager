package io.github.fvarrui.javapackager.packagers;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public class WindowsPackager extends Packager {

	/**
	 * Creates a Windows app file structure with native executable
	 * 
	 * @throws MojoExecutionException
	 */
	@Override
	public File doCreateApp() throws MojoExecutionException {
		
		Logger.append("Creating windows EXE ...");		
		
		// generates manifest file to require administrator privileges from velocity template
		File manifestFile = new File(assetsFolder, name + ".exe.manifest");
		VelocityUtils.render("windows/exe.manifest.vtl", manifestFile, this);
		Logger.info("Exe manifest file generated in " + manifestFile.getAbsolutePath() + "!");

		// prepares launch4j plugin configuration
		
		executable = new File(executable.getParentFile(), executable.getName() + ".exe");
		
		List<Element> optsElements = vmArgs.stream().map(arg -> element("opt", arg)).collect(Collectors.toList());
	
		List<Element> pluginConfig = new ArrayList<>();
		pluginConfig.add(element("headerType", "" + winConfig.getHeaderType()));
		pluginConfig.add(element("jar", jarFile.getAbsolutePath()));
		pluginConfig.add(element("outfile", executable.getAbsolutePath()));
		pluginConfig.add(element("icon", iconFile.getAbsolutePath()));
		pluginConfig.add(element("manifest", manifestFile.getAbsolutePath()));
		pluginConfig.add(element("classPath",  element("mainClass", mainClass)));
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
				env
			);

		Logger.subtract("Windows EXE file created in " + executable + "!");		
		
		return appFolder;
	}

	/**
	 * Creates a Setup installer file including all app folder's content only for
	 * Windows so app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	@Override
	public void doGenerateInstallers(List<File> installers) throws MojoExecutionException {

		if (winConfig.isGenerateSetup()) {
			File setupFile = generateSetup();
			if (setupFile != null) installers.add(setupFile);
		}

		if (winConfig.isGenerateMsi()) {
			File msiFile = generateMsi();
			if (msiFile != null) installers.add(msiFile);
		}
		
	}

	/**
	 * Creates a MSI file including all app folder's content only for
	 * Windows so app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private File generateMsi() throws MojoExecutionException {

		Logger.append("Generating MSI file ...");
		
		// generates WXS file from velocity template
		File wxsFile = new File(assetsFolder, name + ".wxs");
		VelocityUtils.render("windows/wxs.vtl", wxsFile, this);
		Logger.info("WXS file generated in " + wxsFile + "!");
		
		// candle wxs file
		Logger.info("Candling file " + wxsFile);
		File wixobjFile = new File(assetsFolder, name + "wixobj");
		CommandUtils.execute("candle", "-out", wixobjFile, wxsFile);
		Logger.info("WIXOBJ file generated in " + wixobjFile +  "!");

		// lighting wxs file
		Logger.info("Lighting file " + wixobjFile);
		File msiFile = new File(outputDirectory, name + ".msi");
		CommandUtils.execute("light", "-out", msiFile, wixobjFile);

		Logger.subtract("MSI file generated!");
		
		return msiFile;
	}

	private File generateSetup() throws MojoExecutionException {
		
		Logger.append("Generating setup file ...");
		
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
			throw new MojoExecutionException("Windows setup file generation failed!");
		}
		
		Logger.subtract("Setup file generated!");
		
		return setupFile;
	}

	@Override
	protected void createSpecificAppStructure() throws MojoExecutionException {

		this.executableDestinationFolder = appFolder;
		this.jarFileDestinationFolder = appFolder;
		this.jreDestinationFolder = new File(appFolder, jreDirectoryName);
		this.resourcesDestinationFolder = appFolder;
		
	}

}
