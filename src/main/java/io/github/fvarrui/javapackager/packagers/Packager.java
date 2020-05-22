package io.github.fvarrui.javapackager.packagers;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.model.License;
import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.IconUtils;
import io.github.fvarrui.javapackager.utils.JavaUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public abstract class Packager {
	
	private static final String DEFAULT_ORGANIZATION_NAME = "ACME";

	// internal generic properties (setted in "createAppStructure")
	protected File appFolder;
	protected File assetsFolder;
	protected File executable;
	protected File jarFile;
	
	// internal packager specific properties (setted in "createSpecificAppStructure")
	protected File executableDestinationFolder;
	protected File jarFileDestinationFolder;
	protected File jreDestinationFolder;
	protected File resourcesDestinationFolder;

	// external properties
	protected ExecutionEnvironment env;
	protected File outputDirectory;
	protected File licenseFile;
	protected File iconFile;
	protected Boolean generateInstaller;
	protected String mainClass;
	protected String name;
	protected String displayName;
	protected String version;
	protected String description;
	protected String url;
	protected Boolean administratorRequired;
	protected String organizationName;
	protected String organizationUrl;
	protected String organizationEmail;
	protected Boolean bundleJre;
	protected Boolean customizedJre;
	protected File jrePath;
	protected List<File> additionalResources;
	protected List<String> modules;
	protected List<String> additionalModules;
	protected Platform platform;
	protected String envPath;
	protected List<String> vmArgs;
	protected File runnableJar;
	protected Boolean copyDependencies;
	protected String jreDirectoryName;
	protected WindowsConfig winConfig;
	protected LinuxConfig linuxConfig;
	protected MacConfig macConfig;
	protected Boolean createTarball;
	protected Boolean createZipball;
	protected Map<String, String> extra;
	protected boolean useResourcesAsWorkingDir;
		
	public File getAppFolder() {
		return appFolder;
	}

	public File getAssetsFolder() {
		return assetsFolder;
	}

	public File getExecutable() {
		return executable;
	}

	public File getJarFile() {
		return jarFile;
	}

	public ExecutionEnvironment getEnv() {
		return env;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public File getLicenseFile() {
		return licenseFile;
	}

	public File getIconFile() {
		return iconFile;
	}

	public Boolean getGenerateInstaller() {
		return generateInstaller;
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public Boolean getAdministratorRequired() {
		return administratorRequired;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public String getOrganizationUrl() {
		return organizationUrl;
	}

	public String getOrganizationEmail() {
		return organizationEmail;
	}

	public Boolean getBundleJre() {
		return bundleJre;
	}

	public Boolean getCustomizedJre() {
		return customizedJre;
	}

	public File getJrePath() {
		return jrePath;
	}

	public List<File> getAdditionalResources() {
		return additionalResources;
	}

	public List<String> getModules() {
		return modules;
	}

	public List<String> getAdditionalModules() {
		return additionalModules;
	}

	public Platform getPlatform() {
		return platform;
	}

	public String getEnvPath() {
		return envPath;
	}
	
	public List<String> getVmArgs() {
		return vmArgs;
	}
	
	public File getRunnableJar() {
		return runnableJar;
	}

	public Boolean getCopyDependencies() {
		return copyDependencies;
	}

	public String getJreDirectoryName() {
		return jreDirectoryName;
	}

	public WindowsConfig getWinConfig() {
		return winConfig;
	}

	public LinuxConfig getLinuxConfig() {
		return linuxConfig;
	}

	public MacConfig getMacConfig() {
		return macConfig;
	}

	public Boolean getCreateTarball() {
		return createTarball;
	}

	public Boolean getCreateZipball() {
		return createZipball;
	}
	
	public Map<String, String> getExtra() {
		return extra;
	}
	
	public boolean isUseResourcesAsWorkingDir() {
		return useResourcesAsWorkingDir;
	}
	
	// fluent api

	public Packager env(ExecutionEnvironment env) {
		this.env = env;
		return this;
	}

	public Packager outputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
		return this;
	}

	public Packager licenseFile(File licenseFile) {
		this.licenseFile = licenseFile;
		return this;
	}

	public Packager iconFile(File iconFile) {
		this.iconFile = iconFile;
		return this;
	}

	public Packager generateInstaller(Boolean generateInstaller) {
		this.generateInstaller = generateInstaller;
		return this;
	}

	public Packager mainClass(String mainClass) {
		this.mainClass = mainClass;
		return this;
	}

	public Packager name(String name) {
		this.name = name;
		return this;
	}

	public Packager displayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public Packager appVersion(String version) {
		this.version = version;
		return this;
	}

	public Packager description(String description) {
		this.description = description;
		return this;
	}

	public Packager url(String url) {
		this.url = url;
		return this;
	}

	public Packager administratorRequired(Boolean administratorRequired) {
		this.administratorRequired = administratorRequired;
		return this;
	}

	public Packager organizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	public Packager organizationUrl(String organizationUrl) {
		this.organizationUrl = organizationUrl;
		return this;
	}

	public Packager organizationEmail(String organizationEmail) {
		this.organizationEmail = organizationEmail;
		return this;
	}

	public Packager bundleJre(Boolean bundleJre) {
		this.bundleJre = bundleJre;
		return this;
	}

	public Packager customizedJre(Boolean customizedJre) {
		this.customizedJre = customizedJre;
		return this;
	}

	public Packager jrePath(File jrePath) {
		this.jrePath = jrePath;
		return this;
	}

	public Packager additionalResources(List<File> additionalResources) {
		this.additionalResources = additionalResources;
		return this;
	}

	public Packager modules(List<String> modules) {
		this.modules = modules;
		return this;
	}

	public Packager additionalModules(List<String> additionalModules) {
		this.additionalModules = additionalModules;
		return this;
	}

	public Packager platform(Platform platform) {
		this.platform = platform;
		return this;
	}

	public Packager envPath(String envPath) {
		this.envPath = envPath;
		return this;
	}

	public Packager vmArgs(List<String> vmArgs) {
		this.vmArgs = vmArgs;
		return this;
	}

	public Packager runnableJar(File runnableJar) {
		this.runnableJar = runnableJar;
		return this;
	}

	public Packager copyDependencies(Boolean copyDependencies) {
		this.copyDependencies = copyDependencies;
		return this;
	}

	public Packager jreDirectoryName(String jreDirectoryName) {
		this.jreDirectoryName = jreDirectoryName;
		return this;
	}

	public Packager winConfig(WindowsConfig winConfig) {
		this.winConfig = winConfig;
		return this;
	}

	public Packager linuxConfig(LinuxConfig linuxConfig) {
		this.linuxConfig = linuxConfig;
		return this;
	}

	public Packager macConfig(MacConfig macConfig) {
		this.macConfig = macConfig;
		return this;
	}

	public Packager createTarball(Boolean createTarball) {
		this.createTarball = createTarball;
		return this;
	}

	public Packager createZipball(Boolean createZipball) {
		this.createZipball = createZipball;
		return this;
	}
	
	public Packager extra(Map<String, String> extra) {
		this.extra = extra;
		return this;
	}

	public Packager useResourcesAsWorkingDir(boolean useResourcesAsWorkingDir) {
		this.useResourcesAsWorkingDir = useResourcesAsWorkingDir;
		return this;
	}

	// ===============================================
	
	public Packager() {
		super();
		Logger.info("Using packager " + this.getClass().getName());
	}
	
	private void init() throws MojoExecutionException {
		
		Logger.append("Initializing packager ...");

		// using artifactId as name, if it's not specified
		name = defaultIfBlank(name, env.getMavenProject().getArtifactId());
		
		// using name as displayName, if it's not specified
		displayName = defaultIfBlank(displayName, name);
		
		// using displayName as description, if it's not specified
		description = defaultIfBlank(description, displayName);
		
		// using "ACME" as organizationName, if it's not specified
		organizationName = defaultIfBlank(organizationName, DEFAULT_ORGANIZATION_NAME);

		// using empty string as organizationUrl, if it's not specified
		organizationUrl = defaultIfBlank(organizationUrl, "");

		// determines target platform if not specified 
		if (platform == Platform.auto) {
			platform = Platform.getCurrentPlatform();
		}
		
		switch (platform) {
		case windows: 
			macConfig = null; 
			linuxConfig = null;
			winConfig.setDefaults(this);
			break;
		case linux:
			macConfig = null;
			linuxConfig.setDefaults(this);
			winConfig = null;
			break;
		case mac: 
			macConfig.setDefaults(this);
			linuxConfig = null; 
			winConfig = null; 
			break;
		default:
		}
		
		Logger.info("Effective packager configuration " + this);		
				
		Logger.subtract("Packager initialized!");

	}

	private void resolveResources() throws MojoExecutionException {
		
		Logger.append("Resolving resources ...");
		
		// locates license file
		licenseFile = resolveLicense(licenseFile, env.getMavenProject().getLicenses());
		if (licenseFile != null) additionalResources.add(licenseFile);
		
		// locates icon file
		iconFile = resolveIcon(iconFile, name, assetsFolder);
		additionalResources.add(iconFile);
		
		Logger.info("Effective additional resources " + additionalResources);
		
		Logger.subtract("Resources resolved!");
		
	}
	
	protected String getLicenseName() {
		List<License> licenses = env.getMavenProject().getLicenses();
		return licenses != null && !licenses.isEmpty() && licenses.get(0) != null ? licenses.get(0).getName() : "";
	}

	/**
	 * Copies all dependencies to app folder
	 * 
	 * @param libsFolder folder containing all dependencies
	 * @throws MojoExecutionException
	 */
	protected void copyAllDependencies(File libsFolder) throws MojoExecutionException {
		if (!copyDependencies) return;

		Logger.append("Copying all dependencies to " + libsFolder.getName() + " folder ...");		
		
		// invokes plugin to copy dependecies to app libs folder
		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"), 
						artifactId("maven-dependency-plugin"), 
						version("3.1.1")
				),
				goal("copy-dependencies"),
				configuration(
						element("outputDirectory", libsFolder.getAbsolutePath())
				),
				env);

		Logger.subtract("All dependencies copied!");		
		
	}
	
	
	/**
	 * Copy a list of resources to a folder
	 * 
	 * @param resources   List of files and folders to be copied
	 * @param destination Destination folder. All specified resources will be copied
	 *                    here
	 */
	protected void copyAdditionalResources(List<File> resources, File destination) {

		Logger.append("Copying additional resources");
		
		resources.stream().forEach(r -> {
			if (!r.exists()) {
				Logger.warn("Additional resource " + r + " doesn't exist");
				return;
			}
			try {
				if (r.isDirectory()) {
					FileUtils.copyFolderToFolder(r, destination);
				} else if (r.isFile()) {
					FileUtils.copyFileToFolder(r, destination);
				}
			} catch (MojoExecutionException e) {
				e.printStackTrace();
			}
		});
		
		Logger.subtract("All additional resources copied!");
		
	}
	
	/**
	 * Bundle a Java Runtime Enrironment with the app.
	 *
	 * Next link explains the process:
	 * {@link https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4}
	 *
	 * @throws MojoExecutionException
	 */
	protected void bundleJre(File destinationFolder, File jarFile, File libsFolder, File specificJreFolder, boolean customizedJre, List<String> defaultModules, List<String> additionalModules, Platform platform) throws MojoExecutionException {
		if (!bundleJre) {
			Logger.warn("Bundling JRE disabled by property 'bundleJre'!\n");
			return;
		}
		
		Logger.append("Bundling JRE ... with " + System.getProperty("java.home"));
		
		if (specificJreFolder != null) {
			
			Logger.info("Embedding JRE from " + specificJreFolder);
			
			if (!specificJreFolder.exists()) {
				throw new MojoExecutionException("JRE path specified does not exist: " + specificJreFolder.getAbsolutePath());
			} else if (!specificJreFolder.isDirectory()) {
				throw new MojoExecutionException("JRE path specified is not a folder: " + specificJreFolder.getAbsolutePath());
			}
			
			// removes old jre folder from bundle
			if (destinationFolder.exists()) FileUtils.removeFolder(destinationFolder);

			// copies JRE folder to bundle
			FileUtils.copyFolderContentToFolder(specificJreFolder, destinationFolder);

			// sets execution permissions on executables in jre
			File binFolder = new File(destinationFolder, "bin");
			if (!binFolder.exists()) {
				throw new MojoExecutionException("Could not embed JRE from " + specificJreFolder.getAbsolutePath() + ": " + binFolder.getAbsolutePath() + " doesn't exist");
			}
			Arrays.asList(binFolder.listFiles()).forEach(f -> f.setExecutable(true, false));

		} else if (JavaUtils.getJavaMajorVersion() <= 8) {
			
			throw new MojoExecutionException("Could not create a customized JRE due to JDK version is " + SystemUtils.JAVA_VERSION + ". Must use jrePath property to specify JRE location to be embedded");
			
		} else if (!platform.isCurrentPlatform()) {
			
			Logger.warn("Cannot create a customized JRE ... target platform (" + platform + ") is different than execution platform (" + Platform.getCurrentPlatform() + ")");
			
			bundleJre = false;
			
		} else {

			String modules = getRequiredModules(libsFolder, customizedJre, jarFile, defaultModules, additionalModules);

			Logger.info("Creating JRE with next modules included: " + modules);

			File modulesDir = new File(System.getProperty("java.home"), "jmods");
	
			File jlink = new File(System.getProperty("java.home"), "/bin/jlink");
	
			if (destinationFolder.exists()) FileUtils.removeFolder(destinationFolder);
			
			// generates customized jre using modules
			CommandUtils.execute(jlink.getAbsolutePath(), "--module-path", modulesDir, "--add-modules", modules, "--output", destinationFolder, "--no-header-files", "--no-man-pages", "--strip-debug", "--compress=2");
	
			// sets execution permissions on executables in jre
			File binFolder = new File(destinationFolder, "bin");
			Arrays.asList(binFolder.listFiles()).forEach(f -> f.setExecutable(true, false));

		}
		
		// removes jre/legal folder
		File legalFolder = new File(destinationFolder, "legal");
		if (legalFolder.exists()) {
			FileUtils.removeFolder(legalFolder);
		}
	
		if (bundleJre) {
			Logger.subtract("JRE bundled in " + destinationFolder.getAbsolutePath() + "!");
		} else {
			Logger.subtract("JRE bundling skipped!");
		}
		
	}
	
	/**
	 * Creates a runnable jar file from sources
	 * 
	 * @throws MojoExecutionException
	 */
	public File createRunnableJar(String name, String version, String mainClass, File outputDirectory) throws MojoExecutionException {
		Logger.append("Creating runnable JAR...");
		
		String classifier = "runnable";

		File jarFile = new File(outputDirectory, name + "-" + version + "-" + classifier + ".jar");

		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"),
						artifactId("maven-jar-plugin"), 
						version("3.1.1")
				),
				goal("jar"),
				configuration(
						element("classifier", classifier),
						element("archive", 
								element("manifest", 
										element("addClasspath", "true"),
										element("classpathPrefix", "libs/"),
										element("mainClass", mainClass)
								)
						),
						element("outputDirectory", jarFile.getParentFile().getAbsolutePath()),
						element("finalName", name + "-" + version)
				),
				env);
		
		Logger.subtract("Runnable jar created in " + jarFile.getAbsolutePath() + "!");
		
		return jarFile;
	}
	
	/**
	 * Uses jdeps command tool to determine which modules all used jar files depend on
	 * 
	 * @param libsFolder folder containing all needed libraries
	 * @param customizedJre if true generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included.
	 * @param jarFile Runnable jar file reference
	 * @param defaultModules Additional files and folders to include in the bundled app.
	 * @param additionalModules Defines modules to customize the bundled JRE. Don't use jdeps to get module dependencies.
	 * @return strign containing a comma separated list with all needed modules
	 * @throws MojoExecutionException
	 */
	protected String getRequiredModules(File libsFolder, boolean customizedJre, File jarFile, List<String> defaultModules, List<String> additionalModules) throws MojoExecutionException {
		
		Logger.append("Getting required modules ... ");
		
		File jdeps = new File(System.getProperty("java.home"), "/bin/jdeps");

		File jarLibs = null;
		if (libsFolder.exists()) 
			jarLibs = new File(libsFolder, "*.jar");
		else
			Logger.warn("No dependencies found!");
		
		List<String> modulesList;
		
		if (customizedJre && defaultModules != null && !defaultModules.isEmpty()) {
			
			modulesList = 
				defaultModules
					.stream()
					.map(module -> module.trim())
					.collect(Collectors.toList());
		
		} else if (customizedJre && JavaUtils.getJavaMajorVersion() >= 13) { 
			
			String modules = 
				CommandUtils.execute(
					jdeps.getAbsolutePath(), 
					"-q",
					"--multi-release", JavaUtils.getJavaMajorVersion(),
					"--ignore-missing-deps", 
					"--print-module-deps", 
					jarLibs,
					jarFile
				);
			
			modulesList = 
				Arrays.asList(modules.split(","))
					.stream()
					.map(module -> module.trim())
					.filter(module -> !module.isEmpty())
					.collect(Collectors.toList());
			
		} else if (customizedJre && JavaUtils.getJavaMajorVersion() >= 9) { 
		
			String modules = 
				CommandUtils.execute(
					jdeps.getAbsolutePath(), 
					"-q",
					"--multi-release", JavaUtils.getJavaMajorVersion(),
					"--list-deps", 
					jarLibs,
					jarFile
				);

			modulesList = 
				Arrays.asList(modules.split("\n"))
					.stream()
					.map(module -> module.trim())
					.map(module -> (module.contains("/") ? module.split("/")[0] : module))
					.filter(module -> !module.isEmpty())
					.filter(module -> !module.startsWith("JDK removed internal"))
					.distinct()
					.collect(Collectors.toList());

		} else {
			
			modulesList = Arrays.asList("ALL-MODULE-PATH");
			
		}
				
		modulesList.addAll(additionalModules);
		
		if (modulesList.isEmpty()) {
			Logger.warn("It was not possible to determine the necessary modules. All modules will be included");
			modulesList.add("ALL-MODULE-PATH");
		}
		
		Logger.subtract("Required modules found: " + modulesList);
		
		return StringUtils.join(modulesList, ",");
	}

	/**
	 * Locates license file
	 */
	protected File resolveLicense(File licenseFile, List<License> licenses) {
		
		// if default license file doesn't exist and there's a license specified in
		// pom.xml file, gets this last one
		if (licenseFile != null && !licenseFile.exists()) {
			Logger.warn("Specified license file doesn't exist: " + licenseFile.getAbsolutePath());
			licenseFile = null;
		}
		// if license not specified, gets from pom
		if (licenseFile == null && !licenses.isEmpty()) {
			
			String urlStr = null; 
			try {
				urlStr = licenses.get(0).getUrl(); 
				URL licenseUrl = new URL(urlStr);
				licenseFile = new File(assetsFolder, "LICENSE");
				FileUtils.downloadFromUrl(licenseUrl, licenseFile);
			} catch (MalformedURLException e) {
				Logger.error("Invalid license URL specified: " + urlStr);
				licenseFile = null;
			} catch (IOException e) {
				Logger.error("Cannot download license from " + urlStr);
				licenseFile = null;
			}
			
		}
		// if license is still null, looks for LICENSE file
		if (licenseFile == null || !licenseFile.exists()) {
			licenseFile = new File("LICENSE");
			if (!licenseFile.exists()) licenseFile = null;
		}
		
		if (licenseFile != null) {
			Logger.info("License file found: " + licenseFile.getAbsolutePath());
		} else {
			Logger.warn("No license file specified");
		}
		
		return licenseFile;
	}
	
	/**
	 * Locates assets or default icon file if the specified one doesn't exist or
	 * isn't specified
	 * 
	 * @throws MojoExecutionException
	 */
	protected File resolveIcon(File iconFile, String name, File assetsFolder) throws MojoExecutionException {
		
		String iconExtension = IconUtils.getIconFileExtensionByPlatform(platform);
		
		if (iconFile == null) {
			iconFile = new File("assets/" + platform + "/", name + iconExtension);
		}
		
		if (!iconFile.exists()) {
			iconFile = new File(assetsFolder, iconFile.getName());
			FileUtils.copyResourceToFile("/" + platform + "/default-icon" + iconExtension, iconFile);
		}
		
		Logger.info("Icon file resolved: " + iconFile.getAbsolutePath());
		
		return iconFile;
	}
	
	
	/**
	 * Bundling app folder in tarball and/or zipball 
	 * @param appFolder Folder to be bundled
	 * @throws MojoExecutionException 
	 */
	public void createBundles() throws MojoExecutionException {
		if (!createTarball && !createZipball) return;

		Logger.append("Bundling app in tarball/zipball ...");
		
		// generate assembly.xml file 
		File assemblyFile = new File(assetsFolder, "assembly.xml");
		VelocityUtils.render("assembly.xml.vtl", assemblyFile, this);
		
		// invokes plugin to assemble zipball and/or tarball
		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"), 
						artifactId("maven-assembly-plugin"), 
						version("3.1.1")
				),
				goal("single"),
				configuration(
						element("descriptors", element("descriptor", assemblyFile.getAbsolutePath())),
						element("finalName", name + "-" + version + "-" + platform),
						element("appendAssemblyId", "false")
				),
				env);
		
		Logger.subtract("Bundles created!");
		
	}
	
	private void createAppStructure() throws MojoExecutionException {
		
		Logger.append("Creating app structure ...");
		
		// creates output directory if it doesn't exist
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		// creates app destination folder
		appFolder = new File(outputDirectory, name);
		if (appFolder.exists()) {
			FileUtils.removeFolder(appFolder);
			Logger.info("Old app folder removed " + appFolder.getAbsolutePath());
		} 
		appFolder = FileUtils.mkdir(outputDirectory, name);
		Logger.info("App folder created: " + appFolder.getAbsolutePath());

		// creates folder for intermmediate assets 
		assetsFolder = FileUtils.mkdir(outputDirectory, "assets");
		Logger.info("Assets folder created: " + assetsFolder.getAbsolutePath());

		// sets app's main executable file
		executable = new File(appFolder, name);
		
		// create the rest of the structure
		createSpecificAppStructure();

		Logger.subtract("App structure created!");
		
	}

	public File createApp() throws MojoExecutionException {
		
		Logger.append("Creating app ...");

		init();

		// creates app folders structure
		createAppStructure();
		
		// resolve resources
		resolveResources();

		// copies additional resources
		copyAdditionalResources(additionalResources, resourcesDestinationFolder);
				
		// creates a runnable jar file
        if (runnableJar != null && runnableJar.exists()) {
        	Logger.info("Using runnable JAR: " + runnableJar);
            jarFile = runnableJar;
        } else {
            jarFile = createRunnableJar(name, version, mainClass, outputDirectory);
        }
        
		// copies all dependencies to Java folder
		File libsFolder = new File(jarFileDestinationFolder, "libs");
		copyAllDependencies(libsFolder);

		// checks if JRE should be embedded
		bundleJre(jreDestinationFolder, jarFile, libsFolder, jrePath, customizedJre, modules, additionalModules, platform);
        
        File appFile = doCreateApp();

		Logger.subtract("App created in " + appFolder.getAbsolutePath() + "!");
		        
		return appFile;
	}

	public List<File> generateInstallers() throws MojoExecutionException {
		List<File> installers = new ArrayList<>();
		
		if (!generateInstaller) {
			Logger.warn("Installer generation is disabled by 'generateInstaller' property!");
			return installers;
		}
		if (!platform.isCurrentPlatform()) {
			Logger.warn("Installers cannot be generated due to the target platform (" + platform + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return installers;
		}
		
		Logger.append("Generating installers ...");

		init();
		
		doGenerateInstallers(installers);
		
		Logger.subtract("Installers generated! " + installers);
		
		return installers;		
	}

	@Override
	public String toString() {
		return "[appFolder=" + appFolder + ", assetsFolder=" + assetsFolder + ", executable=" + executable
				+ ", jarFile=" + jarFile + ", outputDirectory=" + outputDirectory + ", licenseFile=" + licenseFile
				+ ", iconFile=" + iconFile + ", generateInstaller=" + generateInstaller + ", mainClass=" + mainClass
				+ ", name=" + name + ", displayName=" + displayName + ", version=" + version + ", description="
				+ description + ", url=" + url + ", administratorRequired=" + administratorRequired
				+ ", organizationName=" + organizationName + ", organizationUrl=" + organizationUrl
				+ ", organizationEmail=" + organizationEmail + ", bundleJre=" + bundleJre + ", customizedJre="
				+ customizedJre + ", jrePath=" + jrePath + ", additionalResources=" + additionalResources + ", modules="
				+ modules + ", additionalModules=" + additionalModules + ", platform=" + platform + ", envPath="
				+ envPath + ", vmArgs=" + vmArgs + ", runnableJar=" + runnableJar + ", copyDependencies="
				+ copyDependencies + ", jreDirectoryName=" + jreDirectoryName + ", winConfig=" + winConfig
				+ ", linuxConfig=" + linuxConfig + ", macConfig=" + macConfig + ", createTarball=" + createTarball
				+ ", createZipball=" + createZipball + ", extra=" + extra + "]";
	}

	protected abstract void createSpecificAppStructure() throws MojoExecutionException; 

	public abstract File doCreateApp() throws MojoExecutionException;
	
	public abstract void doGenerateInstallers(List<File> installers) throws MojoExecutionException;
	
}
