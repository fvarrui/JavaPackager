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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipFile;

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

import com.google.common.io.Files;

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

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(defaultValue = "${project.build.directory}/app", property = "appFolder", required = true)
	private File appFolder;

	@Parameter(defaultValue = "${project.build.directory}/assets", property = "assetsFolder", required = true)
	private File assetsFolder;

	@Parameter(defaultValue = "${project.build.directory}/${project.name}-${project.version}.jar", property = "jarFile", required = true)
	private File jarFile;

	@Parameter(defaultValue = "${project.build.directory}/app/${project.name}", property = "executable", required = true)
	private File executable;
	
	@Parameter(property = "jreUrl", required = true)
	private URL jreUrl;

	@Parameter(property = "iconFile")
	private File iconFile;

	@Parameter(defaultValue = "11.0.2", property = "jreMinVersion", required = true)
	private String jreMinVersion;

	@Parameter(property = "mainClass", required = true)
	private String mainClass;

	@Parameter(defaultValue = "true", property = "bundleJre", required = true)
	private Boolean bundleJre;

	public void execute() throws MojoExecutionException {
		System.out.println(outputDirectory);

		this.env = executionEnvironment(mavenProject, mavenSession, pluginManager);

		if (!appFolder.exists()) appFolder.mkdirs();

		if (!assetsFolder.exists()) assetsFolder.mkdirs();

		app = getApp();

		copyAllDependencies();
		downloadJre();

		if (SystemUtils.IS_OS_MAC_OSX) {

			if (iconFile == null) iconFile = new File("assets/mac", mavenProject.getName() + ".icns");

			createLinuxExecutable();
		}

		if (SystemUtils.IS_OS_LINUX) {

			if (iconFile == null) iconFile = new File("assets/linux", mavenProject.getName() + ".png");

			FileUtils.copyToFolder(iconFile, appFolder);

			createLinuxExecutable();
			generateDebPackage();

		}

		if (SystemUtils.IS_OS_WINDOWS) {

			if (iconFile == null) iconFile = new File("assets/windows", mavenProject.getName() + ".ico");

			createWindowsExecutable();
			generateWindowsInstaller();
		}

	}

	private App getApp() throws MojoExecutionException {
		App app = new App();
		
		app.setName(mavenProject.getName());
		app.setVersion(mavenProject.getVersion());
		app.setDescription(mavenProject.getDescription());
		app.setOrganizationName(mavenProject.getOrganization().getName());
		
		try {
			app.setUrl(new URL(mavenProject.getOrganization().getUrl()));
		} catch (MalformedURLException e) {
			getLog().warn(e.getMessage(), e);
		}
		
		// if license file exists
		if (!mavenProject.getLicenses().isEmpty()) {
			File licenseFile = new File(mavenProject.getLicenses().get(0).getUrl());
			FileUtils.copyToFolder(licenseFile, appFolder);
			app.setLicense(licenseFile.getAbsolutePath());
		}
		return app;
	}

	private void createLinuxExecutable() throws MojoExecutionException {

		getLog().info("Creating GNU/Linux executable...");

		// concat startup.sh script + generated jar
		try {
			
			InputStream startup = getClass().getResourceAsStream("/linux/startup.sh");
			InputStream jar = new FileInputStream(jarFile);
			FileOutputStream binary = new FileOutputStream(executable);
			
			FileUtils.concat(binary, startup, jar);
			
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

	private void createWindowsExecutable() throws MojoExecutionException {
		getLog().info("Creating Windows executable...");

		
		
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
						element(name("classPath"), 
								element(name("mainClass"), mainClass)
								),
						element(name("jre"), 
								element(name("bundledJre64Bit"), "false"),
								element(name("minVersion"), jreMinVersion) 

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
		try {
			Files.copy(iconFile, new File(assetsFolder, app.getName() + ".ico"));
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

		// generate iss file from velocity template
		File issFile = new File(assetsFolder, app.getName() + ".iss");
		try {
			VelocityUtils.render("windows/iss.vtl", issFile, "app", app);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

		// generate windows installer with inno setup command line compiler
		try {
			ProcessUtils.exec(getLog(), "iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + executable.getName(), issFile.getAbsolutePath());
		} catch (IOException | InterruptedException e) {
			getLog().warn(e.getMessage(), e);
		}

	}

	private void generateDebPackage() throws MojoExecutionException {
		getLog().info("Generating DEB package ...");

		File controlFile = new File(assetsFolder, "control");

		try {
			VelocityUtils.render("linux/control.vtl", controlFile, "app", app);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

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
									element(name("src"), outputDirectory.getAbsolutePath() + "/assets/${project.name}.desktop"),
									element(name("mapper"), 
											element(name("type"), "perm"),
											element(name("prefix"), "/usr/share/applications")
											)
									),
							/* polkit policy file (run as root) */
							element(name("data"), 
									element(name("type"), "file"),
									element(name("src"), outputDirectory.getAbsolutePath() + "/assets/${project.name}.policy"),
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
							/* symbolic link to app binary */
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
	
	private void downloadJre() throws MojoExecutionException {
		if (!bundleJre) return;
		
		getLog().info("Downloading JRE ...");

		String downloadUrl = AdoptOpenJDKUtils.getDownloadUrl(jreUrl);
		
		File jreFolder = new File(assetsFolder.getAbsolutePath(), "jre");
		File zipFile = new File(jreFolder, new File(downloadUrl).getName());
		
		// download jre
		executeMojo(
				plugin(
						groupId("com.googlecode.maven-download-plugin"), 
						artifactId("download-maven-plugin"), 
						version("1.4.1")
						),
				goal("wget"),
				configuration(
						element(name("uri"), downloadUrl),
						element(name("outputDirectory"), jreFolder.getAbsolutePath())
						),
				env);
		
		// get jre folder name into zip file
		String zippedJreFolder = FileUtils.getZipContent(zipFile);
		
		// unzip jre into app folder and rename it as jre
		executeMojo(
				plugin(
						groupId("org.codehaus.mojo"), 
						artifactId("truezip-maven-plugin"), 
						version("1.2")
						),
				goal("copy"),
				configuration(
						element(name("files"), 
								element(name("file"), 
										element(name("source"), zipFile.getAbsolutePath() + "/" + zippedJreFolder),
										element(name("outputDirectory"), appFolder.getAbsolutePath() ),
										element(name("destName"), "jre")
										)
								)
						),
				env);

	}

}
