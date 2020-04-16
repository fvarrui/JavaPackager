package io.github.fvarrui.javapackager;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.IconUtils;
import io.github.fvarrui.javapackager.utils.JavaUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class PackageMojo extends AbstractMojo {
	
	private static final String DEFAULT_ORGANIZATION_NAME = "ACME";

	// maven components
	
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${session}", readonly = true)
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Component
	private MavenProjectHelper projectHelper;

	// private variables
	
	private ExecutionEnvironment env;
	private Map<String, Object> info;
	private Platform hostPlatform;
	private File debFile, appFolder, assetsFolder, jarFile, executable;

	// plugin configuration properties

	/**
	 * Output directory.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = false)
	private File outputDirectory;

	/**
	 * Path to project license file.
	 */
	@Parameter(property = "licenseFile", required = false)
	private File licenseFile;

	/**
	 * Path to the app icon file (PNG, ICO or ICNS).
	 */
	@Parameter(property = "iconFile", required = false)
	private File iconFile;

	/**
	 * Generates an installer for the app.
	 */
	@Parameter(defaultValue = "true", property = "generateInstaller", required = false)
	private Boolean generateInstaller;

	/**
	 * Full path to your app main class.
	 */
	@Parameter(defaultValue = "${exec.mainClass}", property = "mainClass", required = true)
	private String mainClass;

	/**
	 * App name.
	 */
	@Parameter(defaultValue = "${project.name}", property = "name", required = false)
	private String name;

	/**
	 * App name to show.
	 */
	@Parameter(defaultValue = "${project.name}", property = "displayName", required = false)
	private String displayName;

	/**
	 * Project version.
	 */
	@Parameter(defaultValue = "${project.version}", property = "version", required = false)
	private String version;

	/**
	 * Project description.
	 */
	@Parameter(defaultValue = "${project.description}", property = "description", required = false)
	private String description;

	/**
	 * App website URL.
	 */
	@Parameter(defaultValue = "${project.url}", property = "url", required = false)
	private String url;

	/**
	 * App will run as administrator (with elevated privileges).
	 */
	@Parameter(defaultValue = "false", property = "administratorRequired", required = false)
	private Boolean administratorRequired;

	/**
	 * Organization name.
	 */
	@Parameter(defaultValue = "${project.organization.name}", property = "organizationName", required = false)
	private String organizationName;

	/**
	 * Organization website URL.
	 */
	@Parameter(defaultValue = "${project.organization.url}", property = "organizationUrl", required = false)
	private String organizationUrl;

	/**
	 * Organization email.
	 */
	@Parameter(defaultValue = "", property = "organizationEmail", required = false)
	private String organizationEmail;

	/**
	 * Embeds a customized JRE with the app.
	 */
	@Parameter(defaultValue = "false", property = "bundleJre", required = false)
	private Boolean bundleJre;
	
	/**
	 * Generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included.
	 */
	@Parameter(defaultValue = "true", property = "customizedJre", required = false)
	private Boolean customizedJre;

	/**
	 * Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least.
	 */
	@Parameter(defaultValue = "", property = "jrePath", required = false)
	private String jrePath;

	/**
	 * Additional files and folders to include in the bundled app.
	 */
	@Parameter(property = "additionalResources", required = false)
	private List<File> additionalResources;

	/**
	 * Defines modules to customize the bundled JRE. Don't use jdeps to get module dependencies.
	 */
	@Parameter(property = "modules", required = false)
	private List<String> modules;

	/**
	 * Additional modules to the ones identified by jdeps or the specified with modules property.
	 */
	@Parameter(property = "additionalModules", required = false)
	private List<String> additionalModules;

    /**
     * Which platform to build, one of:
     * <ul>
     * <li><tt>auto</tt> - automatically detect based on the host OS (the default)</li>
     * <li><tt>mac</tt></li>
     * <li><tt>linux</tt></li>
     * <li><tt>windows</tt></li>
     * </ul>
     * To build for multiple platforms at once, add multiple executions to the plugin's configuration.
     */	
	@Parameter(defaultValue = "auto", property = "platform", required = true)
	private Platform platform;

	/**
	 * Defines PATH environment variable in GNU/Linux and Mac OS X startup scripts.
	 */
	@Parameter(property = "envPath", required = false)
	private String envPath;

    /**
	 * Additional arguments to provide to the JVM (for example <tt>-Xmx2G</tt>).
	 */	
	@Parameter(property = "vmArgs", required = false)
	private List<String> vmArgs;
	
	/**
	 * Provide your own runnable .jar (for example, a shaded .jar) instead of letting this plugin create one via
	 * the <tt>maven-jar-plugin</tt>.
	 */
    @Parameter(property = "runnableJar", required = false)
    private String runnableJar;

    /**
     * Whether or not to copy dependencies into the bundle. Generally, you will only disable this if you specified
     * a <tt>runnableJar</tt> with all dependencies shaded into the .jar itself. 
     */
    @Parameter(defaultValue = "true", property = "copyDependencies", required = true)
    private Boolean copyDependencies;
    
	/**
	 * Bundled JRE directory name
	 */
	@Parameter(defaultValue = "jre", property = "jreDirectoryName", required = false)
	private String jreDirectoryName;

	/**
	 * Launch4j version info
	 */
	@Parameter(property = "versionInfo", required = false)
	private VersionInfo versionInfo;
	
	/**
	 * Bundles app in a tarball file
	 */
	@Parameter(defaultValue = "false", property = "createTarball", required = false)
	private Boolean createTarball;

	/**
	 * Bundles app in a zipball file
	 */
	@Parameter(defaultValue = "false", property = "createZipball", required = false)
	private Boolean createZipball;


	public PackageMojo() {
		super();
		Logger.init(getLog()); // sets Mojo's logger to Logger class, so it could be used from static methods
	}

	public void execute() throws MojoExecutionException {
		
		// gets plugin execution environment 
		this.env = executionEnvironment(mavenProject, mavenSession, pluginManager);
		
		// using artifactId as name, if it's not specified
		name = defaultIfBlank(name, mavenProject.getArtifactId());
		
		// using name as displayName, if it's not specified
		displayName = defaultIfBlank(displayName, name);
		
		// using displayName as description, if it's not specified
		description = defaultIfBlank(description, displayName);
		
		// using "ACME" as organizationName, if it's not specified
		organizationName = defaultIfBlank(organizationName, DEFAULT_ORGANIZATION_NAME);

		// determines current platform
		hostPlatform = getCurrentPlatform();
		
		// determines target platform if not specified 
		if (platform == null || platform == Platform.auto) {
			platform = hostPlatform;
		}
		
		getLog().info("Packaging app for " + platform);
		
		// creates output directory if 
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		// creates app destination folder
		appFolder = FileUtils.mkdir(outputDirectory, name);
		if (appFolder.exists()) {
			FileUtils.removeFolder(appFolder);
		}

		// creates folder for intermmediate assets 
		assetsFolder = FileUtils.mkdir(outputDirectory, "assets");

		// sets app's main executable file 
		executable = new File(appFolder, name);

		// locates license file
		resolveLicense();
		
		// locates icon file
		resolveIcon();

		// creates a runnable jar file
        if (runnableJar == null || runnableJar.isBlank()) {
            jarFile = createRunnableJar(name, version, mavenProject.getPackaging());
        } else {
        	getLog().info("Using runnable JAR: " + runnableJar);
            jarFile = new File(runnableJar);
        }
        
		// collects app info 
		this.info = getInfo();

		// generates bundle depending on the specified target platform  
		switch (platform) {
		case mac:
			createMacApp();
			generateDmgImage();
			break;
		case linux:
			createLinuxApp();
			generateDebPackage();
			generateRpmPackage();
			break;
		case windows:
			createWindowsApp();
			generateWindowsInstaller();
			break;
		default:
			throw new MojoExecutionException("Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);
		}
		
		// bundles app in tarball/zipball
		createBundle(appFolder);

	}

	/**
	 * Locates license file
	 */
	private void resolveLicense() {
		// if default license file doesn't exist and there's a license specified in
		// pom.xml file, gets this last one
		if (licenseFile != null && !licenseFile.exists()) {
			getLog().warn("Specified license file doesn't exist: " + licenseFile.getAbsolutePath());
			licenseFile = null;
		}
		// if license not specified, gets from pom
		if (licenseFile == null && !mavenProject.getLicenses().isEmpty()) {
			licenseFile = new File(mavenProject.getLicenses().get(0).getUrl());
			if (!licenseFile.exists()) licenseFile = null;
		}
		// if license is still null, looks for LICENSE file
		if (licenseFile == null || !licenseFile.exists()) {
			licenseFile = new File("LICENSE");
			if (!licenseFile.exists()) licenseFile = null;
		}
		
		if (licenseFile != null) {
			getLog().info("License file found: " + licenseFile.getAbsolutePath());
		} else {
			getLog().warn("No license file specified");
		}
	}
	
	/**
	 * Locates assets or default icon file if the specified one doesn't exist or
	 * isn't specified
	 * 
	 * @throws MojoExecutionException
	 */
	private void resolveIcon() throws MojoExecutionException {
		String iconExtension = IconUtils.getIconFileExtensionByPlatform(platform);
		if (iconFile == null) {
			iconFile = new File("assets/" + platform + "/", name + iconExtension);
		}
		if (!iconFile.exists()) {
			iconFile = new File(assetsFolder, iconFile.getName());
			FileUtils.copyResourceToFile("/" + platform + "/default-icon" + iconExtension, iconFile);
		}
	}

	/**
	 * Creates a runnable jar file from sources
	 * 
	 * @throws MojoExecutionException
	 */
	private File createRunnableJar(String name, String version, String packaging) throws MojoExecutionException {
		getLog().info("Creating runnable JAR...");
		
		String classifier = "runnable";

		File jarFile = new File(outputDirectory, name + "-" + version + "-" + classifier + "." + packaging);

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
		
		return jarFile;
	}

	/**
	 * Collects info needed for Velocity templates and populates a map with it
	 * 
	 * @return Map with collected properties
	 * @throws MojoExecutionException
	 */
	private Map<String, Object> getInfo() {
		HashMap<String, Object> info = new HashMap<>();
		info.put("name", name);
		info.put("displayName", displayName);
		info.put("version", version);
		info.put("description", description);
		info.put("url", url);
		info.put("organizationName", organizationName);
		info.put("organizationUrl", organizationUrl == null ? "" : organizationUrl);
		info.put("organizationEmail", organizationEmail);
		info.put("administratorRequired", administratorRequired);
		info.put("bundleJre", bundleJre);
		info.put("mainClass", mainClass);
		info.put("jarFile", jarFile.getName());
		info.put("license", licenseFile != null ? licenseFile.getAbsolutePath() : "");
		info.put("envPath", envPath);
		info.put("vmArgs", StringUtils.join(vmArgs, " "));
		info.put("jreDirectoryName", jreDirectoryName);
		info.put("createTarball", createTarball);
		info.put("createZipball", createZipball);
		return info;
	}

	/**
	 * Creates a RPM package file including all app folder's content only for 
	 * GNU/Linux so app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private void generateRpmPackage() throws MojoExecutionException {
		if (!generateInstaller || hostPlatform != Platform.linux) return;

		getLog().info("Generating RPM package...");

//		if (!debFile.exists()) {
//			getLog().warn("Cannot convert DEB to RPM because " + debFile.getAbsolutePath() + " doesn't exist");
//			return;
//		}
//
//		try {
//			// executes alien command to generate rpm package folder from deb file
//			CommandUtils.execute(assetsFolder, "alien", "-g", "--to-rpm", debFile);
//		} catch (MojoExecutionException e) {
//			getLog().warn("alien command execution failed", e);
//			return;
//		}
//
//		File packageFolder = new File(assetsFolder, name.toLowerCase() + "-" + version);
//		File specFile = FileUtils.findFirstFile(packageFolder, ".*\\.spec");
//
//		try {
//			// rebuilds rpm package
//			CommandUtils.execute(assetsFolder, "rpmbuild", "--buildroot", packageFolder, "--nodeps", "-bb", specFile);
//		} catch (MojoExecutionException e) {
//			getLog().warn("rpmbuild command execution failed", e);
//			return;
//		}
//
//		// renames generated rpm package
//		File rpmFile = FileUtils.findFirstFile(outputDirectory, ".*\\.rpm");
//		String newName = name + "_" + version + ".rpm";
//		FileUtils.rename(rpmFile, newName);
		
		File xpmIcon = new File(iconFile.getParentFile(), FilenameUtils.removeExtension(iconFile.getName()) + ".xpm");
		if (!xpmIcon.exists()) {
			FileUtils.copyResourceToFile("/linux/default-icon.xpm", xpmIcon);
		}

		File rpmFile = new File(outputDirectory, name + "_" + version + ".rpm");
		
		// invokes plugin to generate deb package
		executeMojo(
				plugin(
						groupId("org.codehaus.mojo"), 
						artifactId("rpm-maven-plugin"), 
						version("2.2.0")
				),
				goal("rpm"), 
				configuration(
						element("license", mavenProject.getLicenses() != null && !mavenProject.getLicenses().isEmpty() && mavenProject.getLicenses().get(0) != null ? mavenProject.getLicenses().get(0).getName() : ""),
						element("packager", organizationName),
						element("group", "Application"),
						element("icon", xpmIcon.getAbsolutePath()),
						element("autoRequires", "false"),
						element("needarch", "true"),
						element("copyTo", rpmFile.getAbsolutePath()),
						element("mappings",
								/* app folder files, except executable file and jre/bin/java */
								element("mapping", 
										element("directory", "/opt/" + name),
										element("filemode", "755"),
										element("sources", 
												element("source", 
														element("location", appFolder.getAbsolutePath())
														)
												)
										)
								)
//								/* executable */
//								element("mapping", 
//										element("type", "file"),
//										element("src", appFolder.getAbsolutePath() + "/" + name),
//										element("mapper", 
//												element("type", "perm"), 
//												element("filemode", "755"),
//												element("prefix", "/opt/" + name)
//										)
//								),
//								/* desktop file */
//								element("data", 
//										element("type", "file"),
//										element("src", desktopFile.getAbsolutePath()),
//										element("mapper", 
//												element("type", "perm"),
//												element("prefix", "/usr/share/applications")
//										)
//								),
//								/* java binary file */
//								element("data", 
//										element("type", "file"),
//										element("src", appFolder.getAbsolutePath() + "/jre/bin/java"),
//										element("mapper", 
//												element("type", "perm"), 
//												element("filemode", "755"),
//												element("prefix", "/opt/" + name + "/jre/bin")
//										)
//								),
//								/* symbolic link in /usr/local/bin to app binary */
//								element("data", 
//										element("type", "link"),
//										element("linkTarget", "/opt/" + name + "/" + name),
//										element("linkName", "/usr/local/bin/" + name),
//										element("symlink", "true"), 
//										element("mapper", 
//												element("type", "perm"),
//												element("filemode", "777")
//										)
//								)
//						)
				),
				env);

	}

	/**
	 * Creates a native MacOS app bundle
	 * 
	 * @throws MojoExecutionException
	 */
	private void createMacApp() throws MojoExecutionException {
		getLog().info("Creating Mac OS X app bundle...");

		// creates and set up directories
		getLog().info("Creating and setting up the bundle directories");
		File appFile 			= FileUtils.mkdir(appFolder, name + ".app");
		File contentsFolder 	= FileUtils.mkdir(appFile, "Contents");
		File resourcesFolder 	= FileUtils.mkdir(contentsFolder, "Resources");
		File javaFolder 		= FileUtils.mkdir(resourcesFolder, "Java");
		File macOSFolder 		= FileUtils.mkdir(contentsFolder, "MacOS");

		// copies all dependencies to Java folder
		getLog().info("Copying dependencies to Java folder");
		File libsFolder = new File(javaFolder, "libs");
		copyAllDependencies(libsFolder);

		// copies jarfile to Java folder
		FileUtils.copyFileToFolder(jarFile, javaFolder);

		// checks if JRE should be embedded
		if (bundleJre) {
			File jreFolder = new File(contentsFolder, "PlugIns/" + jreDirectoryName + "/Contents/Home");
			bundleJre(jreFolder, libsFolder);
		}

		// creates startup file to boot java app
		getLog().info("Creating startup file");
		File startupFile = new File(macOSFolder, "startup");
		VelocityUtils.render("mac/startup.vtl", startupFile, info);
		startupFile.setExecutable(true, false);

		// determines icon file location and copies it to resources folder
		getLog().info("Copying icon file to Resources folder");
		FileUtils.copyFileToFolder(iconFile.getAbsoluteFile(), resourcesFolder);

		// creates and write the Info.plist file
		getLog().info("Writing the Info.plist file");
		File infoPlistFile = new File(contentsFolder, "Info.plist");
		VelocityUtils.render("mac/Info.plist.vtl", infoPlistFile, info);

		// copies specified additional resources into the top level directory (include license file)
		if (licenseFile != null) additionalResources.add(licenseFile);
		copyAdditionalResources(additionalResources, resourcesFolder);

		// codesigns app folder
		if (hostPlatform == Platform.mac) {
			CommandUtils.execute("codesign", "--force", "--deep", "--sign", "-", appFile);
		}

	}

	/**
	 * Creates a GNU/Linux app file structure with native executable
	 * 
	 * @throws MojoExecutionException
	 */
	private void createLinuxApp() throws MojoExecutionException {
		getLog().info("Creating GNU/Linux app bundle...");

		// copies icon file to app folder
		FileUtils.copyFileToFolder(iconFile, appFolder);

		// copies all dependencies
		File libsFolder = new File(appFolder, "libs");
		copyAllDependencies(libsFolder);

		// copies additional resources
		if (licenseFile != null) additionalResources.add(licenseFile);
		copyAdditionalResources(additionalResources, appFolder);

		// checks if JRE should be embedded
		if (bundleJre) {
			File jreFolder = new File(appFolder, jreDirectoryName);
			bundleJre(jreFolder, libsFolder);
		}

		// generates startup.sh script to boot java app
		File startupFile = new File(assetsFolder, "startup.sh");
		VelocityUtils.render("linux/startup.sh.vtl", startupFile, info);

		// concats linux startup.sh script + generated jar in executable (binary)
		FileUtils.concat(executable, startupFile, jarFile);

		// sets execution permissions
		executable.setExecutable(true, false);
		
	}

	/**
	 * Creates a Windows app file structure with native executable
	 * 
	 * @throws MojoExecutionException
	 */
	private void createWindowsApp() throws MojoExecutionException {
		getLog().info("Creating Windows app bundle...");
		
		// generates manifest file to require administrator privileges from velocity template
		File manifestFile = new File(assetsFolder, name + ".exe.manifest");
		VelocityUtils.render("windows/exe.manifest.vtl", manifestFile, info);
		
		// copies all dependencies
		File libsFolder = new File(appFolder, "libs");
		copyAllDependencies(libsFolder);
		
		// copies additional resources
		if (licenseFile != null) additionalResources.add(licenseFile);		
		copyAdditionalResources(additionalResources, appFolder);
		
		// checks if JRE should be embedded
		if (bundleJre) {
			File jreFolder = new File(appFolder, jreDirectoryName);
			bundleJre(jreFolder, libsFolder);
		}
		
		// test version info
		
		if (versionInfo == null) {
			getLog().warn("Version info not specified. Using defaults.");
			versionInfo = new VersionInfo();
		}
		versionInfo.setDefaults(info);
		getLog().info(versionInfo.toString());
		
		// prepares launch4j plugin configuration
		
		List<Element> optsElements = vmArgs.stream().map(arg -> element("opt", arg)).collect(Collectors.toList()); 
		
		List<Element> config = new ArrayList<>();
		config.add(element("headerType", "gui"));
		config.add(element("jar", jarFile.getAbsolutePath()));
		config.add(element("outfile", executable.getAbsolutePath() + ".exe"));
		config.add(element("icon", iconFile.getAbsolutePath()));
		config.add(element("manifest", manifestFile.getAbsolutePath()));
		config.add(element("classPath",  element("mainClass", mainClass)));
		config.add(element("jre", 
						element("path", bundleJre ? jreDirectoryName : "%JAVA_HOME%"),
						element("opts", optsElements.toArray(new Element[optsElements.size()]))
					)
				);
		config.add(element("versionInfo", 
						element("fileVersion", versionInfo.getFileVersion()),
						element("txtFileVersion", versionInfo.getTxtFileVersion()),
						element("productVersion", versionInfo.getProductVersion()),
						element("txtProductVersion", versionInfo.getTxtProductVersion()),
						element("copyright", versionInfo.getCopyright()),
						element("companyName", versionInfo.getCompanyName()),
						element("fileDescription", versionInfo.getFileDescription()),
						element("productName", versionInfo.getProductName()),
						element("internalName", versionInfo.getInternalName()),
						element("originalFilename", versionInfo.getOriginalFilename()),
						element("trademarks", versionInfo.getTrademarks()),
						element("language", versionInfo.getLanguage())
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
				configuration(config.toArray(new Element[config.size()])),
				env
			);
	}

	/**
	 * Creates a EXE installer file including all app folder's content only for
	 * Windows so app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private void generateWindowsInstaller() throws MojoExecutionException {
		if (!generateInstaller || hostPlatform != Platform.windows) return;

		getLog().info("Generating Windows installer...");

		// copies ico file to assets folder
		FileUtils.copyFileToFolder(iconFile, assetsFolder);
		
		// generates iss file from velocity template
		File issFile = new File(assetsFolder, name + ".iss");
		VelocityUtils.render("windows/iss.vtl", issFile, info);

		// generates windows installer with inno setup command line compiler
		CommandUtils.execute("iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + name + "_" + version, issFile);
		
	}

	/**
	 * Creates a DEB package file including all app folder's content only for 
	 * GNU/Linux so app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private void generateDebPackage() throws MojoExecutionException {
		if (!generateInstaller || hostPlatform != Platform.linux) return;

		getLog().info("Generating DEB package ...");

		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, info);

		// generates deb control file from velocity template
		File controlFile = new File(assetsFolder, "control");
		VelocityUtils.render("linux/control.vtl", controlFile, info);

		debFile = new File(outputDirectory, name + "_" + version + ".deb");

		// invokes plugin to generate deb package
		executeMojo(
				plugin(
						groupId("org.vafer"), 
						artifactId("jdeb"), 
						version("1.7")
				), 
				goal("jdeb"), 
				configuration(
						element("controlDir", controlFile.getParentFile().getAbsolutePath()),
						element("deb", outputDirectory.getAbsolutePath() + "/" + debFile.getName()),
						element("dataSet",
								/* app folder files, except executable file and jre/bin/java */
								element("data", 
										element("type", "directory"),
										element("src", appFolder.getAbsolutePath()),
										element("mapper", 
												element("type", "perm"),
												element("prefix", "/opt/" + name)
										),
										element("excludes", executable.getName() + "," + "jre/bin/java")
								),
								/* executable */
								element("data", 
										element("type", "file"),
										element("src", appFolder.getAbsolutePath() + "/" + name),
										element("mapper", 
												element("type", "perm"), 
												element("filemode", "755"),
												element("prefix", "/opt/" + name)
										)
								),
								/* desktop file */
								element("data", 
										element("type", "file"),
										element("src", desktopFile.getAbsolutePath()),
										element("mapper", 
												element("type", "perm"),
												element("prefix", "/usr/share/applications")
										)
								),
								/* java binary file */
								element("data", 
										element("type", "file"),
										element("src", appFolder.getAbsolutePath() + "/jre/bin/java"),
										element("mapper", 
												element("type", "perm"), 
												element("filemode", "755"),
												element("prefix", "/opt/" + name + "/jre/bin")
										)
								),
								/* symbolic link in /usr/local/bin to app binary */
								element("data", 
										element("type", "link"),
										element("linkTarget", "/opt/" + name + "/" + name),
										element("linkName", "/usr/local/bin/" + name),
										element("symlink", "true"), 
										element("mapper", 
												element("type", "perm"),
												element("filemode", "777")
										)
								)
						)
				),
				env);
	}
	
	/**
	 * Creates a DMG image file including all app folder's content only for MacOS so
	 * app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private void generateDmgImage() throws MojoExecutionException {
		if (!generateInstaller || hostPlatform != Platform.mac) return;
		
		getLog().info("Generating DMG disk image file");

		// creates a symlink to Applications folder
		File targetFolder = new File("/Applications");
		File linkFile = new File(appFolder, "Applications");
		FileUtils.createSymlink(linkFile, targetFolder);

		// creates the DMG file including app folder's content
		getLog().info("Generating the Disk Image file");
		File diskImageFile = new File(outputDirectory, name + "_" + version + ".dmg");
		CommandUtils.execute("hdiutil", "create", "-srcfolder", appFolder, "-volname", name, diskImageFile);
		
	}

	/**
	 * Copies all dependencies to app folder
	 * 
	 * @param libsFolder folder containing all dependencies
	 * @throws MojoExecutionException
	 */
	private void copyAllDependencies(File libsFolder) throws MojoExecutionException {
		if (copyDependencies != null && !copyDependencies) return;

		getLog().info("Copying all dependencies to " + appFolder.getName() + " folder ...");		
		
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
		
	}
	
	/**
	 * Bundle a Java Runtime Enrironment with the app.
	 *
	 * Next link explains the process:
	 * {@link https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4}
	 *
	 * @throws MojoExecutionException
	 */
	private boolean bundleJre(File jreFolder, File libsFolder) throws MojoExecutionException {
		getLog().info("Bundling JRE ... with " + System.getProperty("java.home"));
		
		if (jrePath != null && !jrePath.isEmpty()) {
			
			getLog().info("Embedding JRE from " + jrePath);
			
			File jrePathFolder = new File(jrePath);

			if (!jrePathFolder.exists()) {
				throw new MojoExecutionException("JRE path specified does not exist: " + jrePath);
			} else if (!jrePathFolder.isDirectory()) {
				throw new MojoExecutionException("JRE path specified is not a folder: " + jrePath);
			}
			
			// removes old jre folder from bundle
			if (jreFolder.exists()) FileUtils.removeFolder(jreFolder);

			// copies JRE folder to bundle
			FileUtils.copyFolderContentToFolder(jrePathFolder, jreFolder);

			// sets execution permissions on executables in jre
			File binFolder = new File(jreFolder, "bin");
			if (!binFolder.exists()) {
				throw new MojoExecutionException("Could not embed JRE from " + jrePath + ": " + binFolder.getAbsolutePath() + " doesn't exist");
			}
			Arrays.asList(binFolder.listFiles()).forEach(f -> f.setExecutable(true, false));

		} else if (JavaUtils.getJavaMajorVersion() <= 8) {
			
			throw new MojoExecutionException("Could not create a customized JRE due to JDK version is " + SystemUtils.JAVA_VERSION + ". Must use jrePath property to specify JRE location to be embedded");
			
		} else if (platform != hostPlatform) {
			
			getLog().warn("Cannot create a customized JRE ... target platform (" + platform + ") is different than execution platform (" + hostPlatform + ")");
			
			info.put("bundleJre", false);
			
			return false;
			
		} else {

			String modules = getRequiredModules(libsFolder);

			getLog().info("Creating JRE with next modules included: " + modules);

			File modulesDir = new File(System.getProperty("java.home"), "jmods");
	
			File jlink = new File(System.getProperty("java.home"), "/bin/jlink");
	
			if (jreFolder.exists()) FileUtils.removeFolder(jreFolder);
			
			// generates customized jre using modules
			CommandUtils.execute(jlink.getAbsolutePath(), "--module-path", modulesDir, "--add-modules", modules, "--output", jreFolder, "--no-header-files", "--no-man-pages", "--strip-debug", "--compress=2");
	
			// sets execution permissions on executables in jre
			File binFolder = new File(jreFolder, "bin");
			Arrays.asList(binFolder.listFiles()).forEach(f -> f.setExecutable(true, false));

		}
		
		// removes jre/legal folder (needed to codesign command not to fail on macos)
		if (SystemUtils.IS_OS_MAC) {
			File legalFolder = new File(jreFolder, "legal");
			getLog().info("Removing " + legalFolder.getAbsolutePath() + " folder so app could be code signed");
			FileUtils.removeFolder(legalFolder);
		}
		
		return true;
			
	}
	
	/**
	 * Uses jdeps command to determine on which modules depends all used jar files
	 * 
	 * @param libsFolder folder containing all needed libraries
	 * @return strign containing a comma separated list with all needed modules
	 * @throws MojoExecutionException
	 */
	private String getRequiredModules(File libsFolder) throws MojoExecutionException {
		
		getLog().info("Getting required modules ... ");
		
		File jdeps = new File(System.getProperty("java.home"), "/bin/jdeps");

		File jarLibs = null;
		if (libsFolder.exists()) 
			jarLibs = new File(libsFolder, "*.jar");
		else
			getLog().warn("No dependencies found!");
		
		List<String> modulesList;
		
		if (customizedJre && modules != null && !modules.isEmpty()) {
			
			modulesList = modules
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
			
			modulesList = Arrays.asList(modules.split(","))
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

			modulesList = Arrays.asList(modules.split("\n"))
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
			getLog().warn("It was not possible to determine the necessary modules. All modules will be included");
			modulesList.add("ALL-MODULE-PATH");
		}
		
		getLog().info("- Modules: " + modulesList);
		
		return StringUtils.join(modulesList, ",");
	}
	
	/**
	 * Copy a list of resources to a folder
	 * 
	 * @param resources   List of files and folders to be copied
	 * @param destination Destination folder. All specified resources will be copied
	 *                    here
	 */
	private void copyAdditionalResources(List<File> resources, File destination) {
		getLog().info("Copying additional resources");
		resources.stream().forEach(r -> {
			if (!r.exists()) {
				getLog().warn("Additional resource " + r + " doesn't exist");
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
	}
	
	/**
	 * Returns current platform (Windows, MacOs, GNU/Linux)
	 * 
	 * @return current platform or null if it's not known
	 */
	private Platform getCurrentPlatform() {
		if (SystemUtils.IS_OS_WINDOWS) return Platform.windows;
		if (SystemUtils.IS_OS_LINUX) return Platform.linux;
		if (SystemUtils.IS_OS_MAC_OSX) return Platform.mac;
		return null;
	}
	
	/**
	 * Bundling app folder in tarball and/or zipball 
	 * @param appFolder Folder to be bundled
	 * @throws MojoExecutionException 
	 */
	private void createBundle(File appFolder) throws MojoExecutionException {
		if (!createTarball && !createZipball) return;

		getLog().info("Bundling app in tarball/zipball ...");
		
		// generate assembly.xml file 
		File assemblyFile = new File(assetsFolder, "assembly.xml");
		VelocityUtils.render("assembly.xml.vtl", assemblyFile, info);
		
		// invokes plugin to assemble zipball and/or tarball
		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"), 
						artifactId("maven-assembly-plugin"), 
						version("3.1.1")
				),
				goal("single"),
				configuration(
						element("descriptors", element("descriptor", assemblyFile.getAbsolutePath()))
				),
				env);
	}

}
