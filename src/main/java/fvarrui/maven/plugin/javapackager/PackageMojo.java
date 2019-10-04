package fvarrui.maven.plugin.javapackager;

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

import fvarrui.maven.plugin.javapackager.utils.FileUtils;
import fvarrui.maven.plugin.javapackager.utils.JavaUtils;
import fvarrui.maven.plugin.javapackager.utils.Logger;
import fvarrui.maven.plugin.javapackager.utils.ProcessUtils;
import fvarrui.maven.plugin.javapackager.utils.VelocityUtils;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class PackageMojo extends AbstractMojo {

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

	private File debFile;
	private File appFolder;
	private File assetsFolder;
	private File jarFile;
	private File executable;

	// plugin configuration properties
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(property = "licenseFile", required = false)
	private File licenseFile;

	@Parameter(property = "iconFile")
	private File iconFile;

	@Parameter(defaultValue = "${java.version}", property = "jreMinVersion", required = true)
	private String jreMinVersion;

	@Parameter(defaultValue = "true", property = "generateInstaller", required = true)
	private Boolean generateInstaller;

	@Parameter(property = "mainClass", required = true)
	private String mainClass;

	@Parameter(defaultValue = "${project.name}", property = "name", required = true)
	private String name;

	@Parameter(defaultValue = "${project.name}", property = "displayName", required = false)
	private String displayName;

	@Parameter(defaultValue = "${project.version}", property = "version", required = true)
	private String version;

	@Parameter(defaultValue = "${project.description}", property = "description", required = false)
	private String description;

	@Parameter(defaultValue = "${project.url}", property = "url", required = false)
	private String url;

	@Parameter(defaultValue = "false", property = "administratorRequired", required = true)
	private Boolean administratorRequired;

	@Parameter(defaultValue = "${project.organization.name}", property = "organizationName", required = false)
	private String organizationName;

	@Parameter(defaultValue = "${project.organization.url}", property = "organizationUrl", required = false)
	private String organizationUrl;

	@Parameter(defaultValue = "", property = "organizationEmail", required = false)
	private String organizationEmail;

	@Parameter(defaultValue = "false", property = "bundleJre", required = true)
	private Boolean bundleJre;
	
	@Parameter(defaultValue = "false", property = "forceJreOptimization", required = true)
	private Boolean forceJreOptimization;
	
	@Parameter(property = "additionalResources", required = false)
	private List<File> additionalResources;

	public PackageMojo() {
		super();
		Logger.init(getLog());
	}

	public void execute() throws MojoExecutionException {
		
		appFolder = new File(outputDirectory, "app");
		if (!appFolder.exists()) {
			appFolder.mkdirs();
		}

		assetsFolder = new File(outputDirectory, "assets");
		if (!assetsFolder.exists()) {
			assetsFolder.mkdirs();
		}

		executable = new File(appFolder, name);

		// if default license file doesn't exist and there's a license specified in
		// pom.xml file, get this last one
		if (licenseFile != null && !licenseFile.exists()) {
			getLog().warn("Specified license file doesn't exist: " + licenseFile.getAbsolutePath());
			licenseFile = null;
		}
		// if license not specified, get from pom
		if (licenseFile == null && !mavenProject.getLicenses().isEmpty()) {
			licenseFile = new File(mavenProject.getLicenses().get(0).getUrl());
		}

		this.env = executionEnvironment(mavenProject, mavenSession, pluginManager);

		createRunnableJar();

		this.info = getInfo();

		if (SystemUtils.IS_OS_MAC_OSX) {

			if (iconFile == null) {
				iconFile = new File("assets/mac/", mavenProject.getName() + ".icns");
			}
			if (!iconFile.exists()) {
				iconFile = new File(assetsFolder, iconFile.getName());
				FileUtils.copyResourceToFile("/mac/default-icon.icns", iconFile);
			}

			createMacApp();
			generateDmgImage();

		} else if (SystemUtils.IS_OS_LINUX) {

			if (iconFile == null) {
				iconFile = new File("assets/linux", mavenProject.getName() + ".png");
			}
			if (!iconFile.exists()) {
				iconFile = new File(assetsFolder, iconFile.getName());
				FileUtils.copyResourceToFile("/linux/default-icon.png", iconFile);
			}

			FileUtils.copyFileToFolder(iconFile, appFolder);

			createLinuxExecutable();
			generateDebPackage();
			generateRpmPackage();

		} else if (SystemUtils.IS_OS_WINDOWS) {

			if (iconFile == null) {
				iconFile = new File("assets/windows", mavenProject.getName() + ".ico");
			}
			if (!iconFile.exists()) {
				iconFile = new File(assetsFolder, iconFile.getName());
				FileUtils.copyResourceToFile("/windows/default-icon.ico", iconFile);
			}

			createWindowsExecutable();
			generateWindowsInstaller();

		} else {

			throw new MojoExecutionException("Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);

		}

	}

	private void createRunnableJar() throws MojoExecutionException {
		getLog().info("Creating runnable JAR...");
		
		String classifier = "runnable";

		jarFile = new File(outputDirectory, mavenProject.getName() + "-" + mavenProject.getVersion() + "-" + classifier + "." + mavenProject.getPackaging());

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
						element("outputDirectory", jarFile.getParentFile().getAbsolutePath())
				),
				env);
	}

	private Map<String, Object> getInfo() throws MojoExecutionException {
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

		return info;
	}

	private void generateRpmPackage() throws MojoExecutionException {
		if (!generateInstaller) return;

		getLog().info("Generating RPM package...");

		if (!debFile.exists()) {
			getLog().warn("Cannot convert DEB to RPM because " + debFile.getAbsolutePath() + " doesn't exist");
			return;
		}

		try {
			// execute alien command to generate rpm package folder from deb file
			ProcessUtils.execute(assetsFolder, "alien", "-g", "--to-rpm", debFile);
		} catch (MojoExecutionException e) {
			getLog().warn("alien command execution failed", e);
			return;
		}

		File packageFolder = new File(assetsFolder, name.toLowerCase() + "-" + version);
		File specFile = new File(packageFolder, name + "-" + version + "-2.spec");

		try {
			// rebuild rpm package
			ProcessUtils.execute(assetsFolder, "rpmbuild", "--buildroot", packageFolder, "--nodeps", "-bb", specFile);
		} catch (MojoExecutionException e) {
			getLog().warn("rpmbuild command execution failed", e);
			return;
		}

		// rename generated rpm package
		File rpmFile = new File(outputDirectory, name + "-" + version + "-2.x86_64.rpm");
		String newName = name + "_" + version + ".rpm";
		FileUtils.rename(rpmFile, newName);

	}

	private void createMacApp() throws MojoExecutionException {
		getLog().info("Creating Mac OS X app bundle...");

		// create and set up directories
		getLog().info("Creating and setting up the bundle directories");
		
		File appFile = new File(appFolder, name + ".app");
		appFile.mkdirs();

		File contentsFolder = new File(appFile, "Contents");
		contentsFolder.mkdirs();

		File resourcesFolder = new File(contentsFolder, "Resources");
		resourcesFolder.mkdirs();

		File javaFolder = new File(resourcesFolder, "Java");
		javaFolder.mkdirs();

		File macOSFolder = new File(contentsFolder, "MacOS");
		macOSFolder.mkdirs();

		// create startup file to boot java app
		getLog().info("Creating startup file");
		File startupFile = new File(macOSFolder, "startup");
		VelocityUtils.render("mac/startup.vtl", startupFile, info);
		startupFile.setExecutable(true, false);

		// copy icon file to resources folder
		getLog().info("Copying icon file to Resources folder");
		FileUtils.copyFileToFolder(iconFile.getAbsoluteFile(), resourcesFolder);

		// copy all dependencies to Java folder
		getLog().info("Copying dependencies to Java folder");
		File libsFolder = new File(javaFolder, "libs");
		copyAllDependencies(libsFolder);

		// copy jarfile to Java folder
		FileUtils.copyFileToFolder(jarFile, javaFolder);

		// check if JRE should be embedded
		if (bundleJre) {
			getLog().info("Bundling JRE");
			File jreFolder = new File(contentsFolder, "PlugIns/jre/Contents/Home");
			createCustomizedJre(jreFolder, libsFolder);
		}

		// create and write the Info.plist file
		getLog().info("Writing the Info.plist file");
		File infoPlistFile = new File(contentsFolder, "Info.plist");
		VelocityUtils.render("mac/Info.plist.vtl", infoPlistFile, info);

		// copy specified additional resources into the top level directory (include license file)
		if (licenseFile != null) additionalResources.add(licenseFile);
		copyAdditionalResources(additionalResources, resourcesFolder);

		// codesign app folder
		ProcessUtils.execute("codesign", "--force", "--deep", "--sign", "-", appFile);

		getLog().info("App Bundle generation finished");

	}

	private void createLinuxExecutable() throws MojoExecutionException {
		getLog().info("Creating GNU/Linux executable...");

		// generate startup.sh script to boot java app
		File startupFile = new File(assetsFolder, "startup.sh");
		VelocityUtils.render("linux/startup.sh.vtl", startupFile, info);

		// concat linux startup.sh script + generated jar in executable (binary)
		FileUtils.concat(executable, startupFile, jarFile);

		// set execution permissions
		executable.setExecutable(true, false);
		
		// copy all dependencies
		File libsFolder = new File(appFolder, "libs");
		copyAllDependencies(libsFolder);

		// copy additional resources
		if (licenseFile != null) additionalResources.add(licenseFile);
		copyAdditionalResources(additionalResources, appFolder);

		// check if JRE should be embedded
		if (bundleJre) {
			getLog().info("Bundling JRE");
			File jreFolder = new File(appFolder, "jre");
			createCustomizedJre(jreFolder, libsFolder);
		}

	}

	private void createWindowsExecutable() throws MojoExecutionException {
		getLog().info("Creating Windows executable...");

		// generate manifest file to require administrator privileges from velocity template
		File manifestFile = new File(assetsFolder, name + ".exe.manifest");
		VelocityUtils.render("windows/exe.manifest.vtl", manifestFile, info);
		
		// copy all dependencies
		File libsFolder = new File(appFolder, "libs");
		copyAllDependencies(libsFolder);
		
		// copy additional resources
		if (licenseFile != null) additionalResources.add(licenseFile);		
		copyAdditionalResources(additionalResources, appFolder);
		
		// check if JRE should be embedded
		if (bundleJre) {
			getLog().info("Bundling JRE");
			File jreFolder = new File(appFolder, "jre");
			createCustomizedJre(jreFolder, libsFolder);
		}
		
		// prepare launch4j plugin configuration
		
		List<Element> config = new ArrayList<>();
		
		config.add(element("headerType", "gui"));
		config.add(element("jar", jarFile.getAbsolutePath()));
		config.add(element("outfile", executable.getAbsolutePath() + ".exe"));
		config.add(element("icon", iconFile.getAbsolutePath()));
		config.add(element("manifest", manifestFile.getAbsolutePath()));
		config.add(element("classPath",  element("mainClass", mainClass)));
		
		if (bundleJre) {
			config.add(element("jre", 
					element("path", "jre")
			));
		} else {
			config.add(element("jre", 
					element("path", "%JAVA_HOME%")
			));
		}
		
		config.add(element("versionInfo", 
				element("fileVersion", "1.0.0.0"),
				element("txtFileVersion", "1.0.0.0"),
				element("copyright", organizationName),
				element("fileDescription", description),
				element("productVersion", version + ".0"),
				element("txtProductVersion", version + ".0"),
				element("productName", name),
				element("internalName", name),
				element("originalFilename", name + ".exe")
		));

		// invoke launch4j plugin to generate windows executable
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

	private void generateWindowsInstaller() throws MojoExecutionException {
		if (!generateInstaller) return;

		getLog().info("Generating Windows installer...");

		// copy ico file to assets folder
		FileUtils.copyFileToFolder(iconFile, assetsFolder);
		
		// generate iss file from velocity template
		File issFile = new File(assetsFolder, name + ".iss");
		VelocityUtils.render("windows/iss.vtl", issFile, info);

		// generate windows installer with inno setup command line compiler
		ProcessUtils.execute("iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + name + "_" + version, issFile);
	}

	private void generateDebPackage() throws MojoExecutionException {
		if (!generateInstaller) return;

		getLog().info("Generating DEB package ...");

		// generate desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, info);

		// generate deb control file from velocity template
		File controlFile = new File(assetsFolder, "control");
		VelocityUtils.render("linux/control.vtl", controlFile, info);

		debFile = new File(outputDirectory, name + "_" + version + ".deb");

		// invoke plugin to generate deb package
		executeMojo(
				plugin(
						groupId("org.vafer"), 
						artifactId("jdeb"), 
						version("1.7")
				), 
				goal("jdeb"), 
				configuration(
						element("controlDir", controlFile.getParentFile().getAbsolutePath()),
						element("deb", outputDirectory.getAbsolutePath() + "/${project.name}_${project.version}.deb"),
						element("dataSet",
								/* app folder files, except executable file and jre/bin/java */
								element("data", 
										element("type", "directory"),
										element("src", appFolder.getAbsolutePath()),
										element("mapper", 
												element("type", "perm"),
												element("prefix", "/opt/${project.name}")
										),
										element("excludes", executable.getName() + "," + "jre/bin/java")
								),
								/* executable */
								element("data", 
										element("type", "file"),
										element("src", appFolder.getAbsolutePath() + "/${project.name}"),
										element("mapper", 
												element("type", "perm"), 
												element("filemode", "755"),
												element("prefix", "/opt/${project.name}")
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
												element("prefix", "/opt/${project.name}/jre/bin")
										)
								),
								/* symbolic link in /usr/local/bin to app binary */
								element("data", 
										element("type", "link"),
										element("linkTarget", "/opt/${project.name}/${project.name}"),
										element("linkName", "/usr/local/bin/${project.name}"),
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
	
	private void generateDmgImage() throws MojoExecutionException {
		if (!generateInstaller) return;
		
		getLog().info("Generating DMG disk image file");

		// create a symlink to Applications folder
		File targetFolder = new File("/Applications");
		File linkFile = new File(appFolder, "Applications");
		FileUtils.createSymlink(linkFile, targetFolder);

		// create the DMG file including app folder content
		getLog().info("Generating the Disk Image file");
		File diskImageFile = new File(outputDirectory, name + "_" + version + ".dmg");
		ProcessUtils.execute("hdiutil", "create", "-srcfolder", appFolder, "-volname", name, diskImageFile);
	}

	private void copyAllDependencies(File libsFolder) throws MojoExecutionException {
		getLog().info("Copying all dependencies to app folder ...");

		// invoke plugin to copy dependecies to app libs folder
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
	 * Create a customized Java Runtime Enrironment from the current JDK using jdeps
	 * and jlink tools.
	 *
	 * Next link explains the process:
	 * {@link https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4}
	 *
	 * @throws MojoExecutionException
	 */
	private void createCustomizedJre(File jreFolder, File libsFolder) throws MojoExecutionException {
		getLog().info("Creating customized JRE ... with " + System.getProperty("java.home"));

		String modules;

		// warn and generate a non optimized JRE
		if (JavaUtils.getJavaMajorVersion() <= 12 && !forceJreOptimization) {

			getLog().warn("We need JDK 12+ for correctly determining the dependencies. You run " + System.getProperty("java.home"));
			getLog().warn("All modules will be included in the generated JRE.");

			modules = "ALL-MODULE-PATH";

		} else { // generate an optimized JRE, including only required modules
			
			if (forceJreOptimization) {
				getLog().warn("JRE optimization has been forced. It can cause issues with some JDKs.");
			}

			File jdeps = new File(System.getProperty("java.home"), "/bin/jdeps");

			// determine required modules for libs and app jar
			modules = "java.scripting,jdk.unsupported,"; // add required modules by default
			
			Object [] additionalArguments = {};
			if (JavaUtils.getJavaMajorVersion() > 12) { 
				additionalArguments = new Object [] { "--ignore-missing-deps" };
			}
			
			String javaMajorVersion = SystemUtils.JAVA_VERSION.split("\\.")[0];
			
			modules += ProcessUtils.execute(
					jdeps.getAbsolutePath(), 
					"-q", 
					additionalArguments, 
					"--print-module-deps", 
					"--multi-release",
					javaMajorVersion,
					"--class-path", new File(libsFolder, "*"), 
					jarFile
				);

		}

		File modulesDir = new File(System.getProperty("java.home"), "jmods");

		File jlink = new File(System.getProperty("java.home"), "/bin/jlink");

		if (jreFolder.exists()) FileUtils.removeFolder(jreFolder);
		
		// generate customized jre using modules
		ProcessUtils.execute(jlink.getAbsolutePath(), "--module-path", modulesDir, "--add-modules", modules, "--output", jreFolder, "--no-header-files", "--no-man-pages", "--strip-debug", "--compress=2");

		// set execution permissions on executables in jre
		File binFolder = new File(jreFolder, "bin");
		Arrays.asList(binFolder.listFiles()).forEach(f -> f.setExecutable(true, false));

	}
	
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

}
