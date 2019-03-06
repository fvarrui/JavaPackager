package fvarrui.maven.plugin.javapackager;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang.SystemUtils;
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
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import fvarrui.maven.plugin.javapackager.utils.AdoptOpenJDKUtils;
import fvarrui.maven.plugin.javapackager.utils.App;
import fvarrui.maven.plugin.javapackager.utils.FileUtils;
import fvarrui.maven.plugin.javapackager.utils.ProcessUtils;
import fvarrui.maven.plugin.javapackager.utils.VelocityUtils;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class PackageMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${session}", readonly = true)
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	private ExecutionEnvironment env;

	private App app;
	
	private File debFile;

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(defaultValue = "${project.build.directory}/app", property = "appFolder", required = true)
	private File appFolder;

	@Parameter(defaultValue = "${project.build.directory}/assets", property = "assetsFolder", required = true)
	private File assetsFolder;

	@Parameter(defaultValue = "${project.build.directory}/${project.name}-${project.version}.jar", property = "jarFile", required = true)
	private File jarFile;
	
	@Parameter(defaultValue = "${user.dir}/LICENSE", property = "licenseFile", required = true)
	private File licenseFile;

	@Parameter(defaultValue = "${project.build.directory}/app/${project.name}", property = "executable", required = true)
	private File executable;
	
	@Parameter(property = "iconFile")
	private File iconFile;

	@Parameter(defaultValue = "11.0.2", property = "jreMinVersion", required = true)
	private String jreMinVersion;

	@Parameter(property = "mainClass", required = true)
	private String mainClass;
	
	@Parameter(defaultValue = "false", property = "administratorRequired", required = true)
	private Boolean administratorRequired;
	
	@Parameter(defaultValue = "", property = "organizationEmail", required = false)
	private String organizationEmail;

	@Parameter(defaultValue = "true", property = "bundleJre", required = true)
	private Boolean bundleJre;

	public void execute() throws MojoExecutionException {
		System.out.println(outputDirectory);

		if (!appFolder.exists()) appFolder.mkdirs();

		if (!assetsFolder.exists()) assetsFolder.mkdirs();
		
		// if default license file doesn't exist and there's a license specified in pom.xml file, get this last one
		if (!licenseFile.exists() && !mavenProject.getLicenses().isEmpty()) {
			licenseFile = new File(mavenProject.getLicenses().get(0).getUrl());
		}
		
		// copy license file to app folder
		if (licenseFile.exists()) FileUtils.copyToFolder(licenseFile, appFolder);
		
		this.app = getApp();

		this.env = executionEnvironment(mavenProject, mavenSession, pluginManager);

		copyAllDependencies();
		
		createCustomizedJre();

		if (SystemUtils.IS_OS_MAC_OSX) {

			if (iconFile == null) iconFile = new File("assets/mac", mavenProject.getName() + ".icns");

			createMacAppBundle();
			
		} else if (SystemUtils.IS_OS_LINUX) {

			if (iconFile == null) iconFile = new File("assets/linux", mavenProject.getName() + ".png");

			FileUtils.copyToFolder(iconFile, appFolder);

			createLinuxExecutable();
			generateDebPackage();
			generateRpmPackage();

		} else if (SystemUtils.IS_OS_WINDOWS) {

			if (iconFile == null) iconFile = new File("assets/windows", mavenProject.getName() + ".ico");

			createWindowsExecutable();
			generateWindowsInstaller();
			
		} else {

			throw new MojoExecutionException("Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);
		
		}

	}

	private void generateRpmPackage() throws MojoExecutionException  {
		getLog().info("Generate RPM package...");
		
		if (!debFile.exists()) {
			getLog().warn("Cannot convert DEB to RPM because " + debFile.getAbsolutePath() + " doesn't exist");
			return;
		}
		
		try {
			// execute alien command to generate rpm package folder from deb file
			ProcessUtils.exec(getLog(), assetsFolder, "alien", "-g", "--to-rpm", debFile.getAbsolutePath());
		} catch (MojoExecutionException e) {
			getLog().warn("alien command execution failed", e);
			return;
		}
		
		File packageFolder = new File(assetsFolder, app.getName() + "-" + app.getVersion());
		File specFile = new File(packageFolder, app.getName() + "-" + app.getVersion() + "-2.spec");
		
		try {
			// rebuild rpm package
			ProcessUtils.exec(getLog(), assetsFolder, "rpmbuild", "--buildroot", packageFolder.getAbsolutePath(), "--nodeps", "-bb", specFile.getAbsolutePath());
		} catch (MojoExecutionException e) {
			getLog().warn("rpmbuild command execution failed", e);
			return;
		}

		// FIXME rename generated rpm package
		File rpmFile = new File(assetsFolder, app.getName() + "-" + app.getVersion() + "-2.x86_64.rpm");
		rpmFile.renameTo(new File(app.getName() + "_" + app.getVersion() + ".rpm"));
		
	}

	private App getApp() throws MojoExecutionException {
		App app = new App();
		
		app.setName(mavenProject.getName());
		app.setVersion(mavenProject.getVersion());
		app.setDescription(mavenProject.getDescription());
		app.setOrganizationName(mavenProject.getOrganization().getName());
		app.setAdministratorRequired(administratorRequired);
		app.setOrganizationEmail(organizationEmail);
		
		try {
			app.setUrl(new URL(mavenProject.getOrganization().getUrl()));
		} catch (MalformedURLException e) {
			getLog().warn(e.getMessage(), e);
		}
		
		// if license file exists
		if (licenseFile != null) app.setLicense(licenseFile.getAbsolutePath());

		return app;
	}
	
	private void createMacAppBundle() throws MojoExecutionException {
		getLog().info("Creating Mac OS X app bundle...");
		
//		File dictionaryFile = new File(".");

		// invoke appbundle plugin to generate mac bundle
		executeMojo(
				plugin(
						groupId("sh.tak.appbundler"), 
						artifactId("appbundle-maven-plugin"),
						version("1.2.0")
						),
				goal("bundle"),
				configuration(
//						element(name("dictionaryFile"), dictionaryFile.getAbsolutePath()),
//						element(name("iconFile"), iconFile.getAbsolutePath()),
						element(name("mainClass"), mainClass)
						),
				env);


	}

	private void createLinuxExecutable() throws MojoExecutionException {
		getLog().info("Creating GNU/Linux executable...");

		// concat linux startup.sh script + generated jar
		try {
			
			getLog().info("Administrator required: " + app.isAdministratorRequired());
			
			// generate startup.sh script to boot java app
			File startupFile = new File(assetsFolder, "startup.sh");
			VelocityUtils.render("linux/startup.sh.vtl", startupFile, "app", app);

			// open stream to files
			InputStream startup = new FileInputStream(startupFile);
			InputStream jar = new FileInputStream(jarFile);
			FileOutputStream binary = new FileOutputStream(executable);
			
			// concat files in binary
			FileUtils.concat(binary, startup, jar);
			
			// set execution permissions
			executable.setExecutable(true, false);
			
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

	private void createWindowsExecutable() throws MojoExecutionException {
		getLog().info("Creating Windows executable...");
		
		// generate manifest file to require administrator privileges
		File manifestFile = null;
		if (administratorRequired) {
			// generate manifest file from velocity template
			manifestFile = new File(assetsFolder, app.getName() + ".exe.manifest");
			VelocityUtils.render("windows/exe.manifest.vtl", manifestFile, "app", app);
		}
		
		// invoke launch4j plugin to generate windows executable
		executeMojo(
				plugin(
						groupId("com.akathist.maven.plugins.launch4j"), 
						artifactId("launch4j-maven-plugin"),
						version("1.7.25")
						),
				goal("launch4j"),
				configuration(
						element(name("headerType"), "gui"), 
						element(name("jar"), jarFile.getAbsolutePath()),
						element(name("manifest"), ""),
						element(name("outfile"), executable.getAbsolutePath() + ".exe"),
						element(name("icon"), iconFile.getAbsolutePath()),
						element(name("manifest"), administratorRequired ? manifestFile.getAbsolutePath() : ""),
						element(name("classPath"), 
								element(name("mainClass"), mainClass)
								),
						element(name("jre"), 
								element(name("bundledJre64Bit"), bundleJre.toString()),
								element(name("minVersion"), jreMinVersion), 
								element(name("runtimeBits"), "64"), 
								element(name("path"), "jre")
								),
						element(name("versionInfo"), 
								element(name("fileVersion"), "1.0.0.0"),
								element(name("txtFileVersion"), "1.0.0.0"),
								element(name("copyright"), "${project.organization.name}"),
								element(name("fileDescription"), "${project.description}"),
								element(name("productVersion"), "${project.version}.0"),
								element(name("txtProductVersion"), "${project.version}.0"),
								element(name("productName"), "${project.name}"),
								element(name("internalName"), "${project.name}"),
								element(name("originalFilename"), "${project.name}.exe")
								)
						),
				env);
	}

	private void generateWindowsInstaller() throws MojoExecutionException {
		getLog().info("Generating Windows installer...");

		// copy ico file to assets folder
		FileUtils.copy(iconFile, new File(assetsFolder, app.getName() + ".ico"));

		// generate iss file from velocity template
		File issFile = new File(assetsFolder, app.getName() + ".iss");
		VelocityUtils.render("windows/iss.vtl", issFile, "app", app);

		// generate windows installer with inno setup command line compiler
		ProcessUtils.exec(getLog(), "iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + app.getName() + "_" + app.getVersion(), issFile.getAbsolutePath());
	}

	private void generateDebPackage() throws MojoExecutionException {
		getLog().info("Generating DEB package ...");
		
		// generate desktop file from velocity template
		File desktopFile = new File(assetsFolder, app.getName() + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, "app", app);

		// generate policy file from velocity template
		File policyFile = new File(assetsFolder, app.getName() + ".policy");
		VelocityUtils.render("linux/policy.vtl", policyFile, "app", app);

		// generate deb control file from velocity template
		File controlFile = new File(assetsFolder, "control");
		VelocityUtils.render("linux/control.vtl", controlFile, "app", app);
		
		debFile = new File(outputDirectory.getAbsolutePath(), app.getName() + "_" + app.getVersion() + ".deb");

		// invoke plugin to generate deb package
		executeMojo(
				plugin(
						groupId("org.vafer"), 
						artifactId("jdeb"), 
						version("1.7")
						), 
				goal("jdeb"), 
				configuration(
						element(name("controlDir"), controlFile.getParentFile().getAbsolutePath()),
						element(name("deb"), outputDirectory.getAbsolutePath() + "/${project.name}_${project.version}.deb"),
						element(name("dataSet"),
							/* app folder files, except executable file and jre/bin/java */
							element(name("data"), 
									element(name("type"), "directory"),
									element(name("src"), appFolder.getAbsolutePath()),
									element(name("mapper"), 
											element(name("type"), "perm"),
											element(name("prefix"), "/opt/${project.name}")
											),
									element(name("excludes"), executable.getName() + "," + "jre/bin/java")
									),
							/* executable */
							element(name("data"), 
									element(name("type"), "file"),
									element(name("src"), appFolder.getAbsolutePath() + "/${project.name}"),
									element(name("mapper"), 
											element(name("type"), "perm"), 
											element(name("filemode"), "755"),
											element(name("prefix"), "/opt/${project.name}")
											)
									),
							/* desktop file */
							element(name("data"), 
									element(name("type"), "file"),
									element(name("src"), desktopFile.getAbsolutePath()),
									element(name("mapper"), 
											element(name("type"), "perm"),
											element(name("prefix"), "/usr/share/applications")
											)
									),
							/* polkit policy file (run as root) */
							element(name("data"), 
									element(name("type"), "file"),
									element(name("src"), policyFile.getAbsolutePath()),
									element(name("mapper"), 
											element(name("type"), "perm"),
											element(name("prefix"), "/usr/share/polkit-1/actions")
											)
									),
							/* java binary file */
							element(name("data"), 
									element(name("type"), "file"),
									element(name("src"), appFolder.getAbsolutePath() + "/jre/bin/java"),
									element(name("mapper"), 
											element(name("type"), "perm"), 
											element(name("filemode"), "755"),
											element(name("prefix"), "/opt/${project.name}/jre/bin")
											)
									),
							/* symbolic link in /usr/local/bin to app binary */
							element(name("data"), 
									element(name("type"), "link"),
									element(name("linkTarget"), "/opt/${project.name}/${project.name}"),
									element(name("linkName"), "/usr/local/bin/${project.name}"),
									element(name("symlink"), "true"), 
									element(name("mapper"), 
											element(name("type"), "perm"),
											element(name("filemode"), "777")
											)
									)
							)
				),
				env);
	}

	private void copyAllDependencies() throws MojoExecutionException {
		getLog().info("Copying all dependencies to app folder ...");

		File libsFolder = new File(appFolder, "libs");

		// invoke plugin to copy dependecies to app libs folder
		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"), 
						artifactId("maven-dependency-plugin"), 
						version("3.1.1")
						),
				goal("copy-dependencies"),
				configuration(
						element(name("outputDirectory"), libsFolder.getAbsolutePath())
						), 
				env);
	}
	
	private void createCustomizedJre() throws MojoExecutionException {
		if (!bundleJre) return;
		
		getLog().info("Create customized JRE ...");
		
		File libsFolder = new File(appFolder, "libs");
		File jreFolder = new File(appFolder, "jre");
		
		// determine required modules for libs and app jar
		String modules = ProcessUtils.exec(getLog(), "jdeps", "--print-module-deps", "--class-path", new File(libsFolder, "*").getAbsolutePath(), jarFile.getAbsolutePath());
		modules += ",java.scripting,jdk.unsupported"; // add required modules
		
		// generate customized jre using modules
		File modulesDir = new File(System.getProperty("java.home"), "jmods");
		jreFolder.delete();
		ProcessUtils.exec(getLog(), "jlink", "--module-path", modulesDir.getAbsolutePath(), "--add-modules", modules, "--output", jreFolder.getAbsolutePath());
		
	}
	
}
