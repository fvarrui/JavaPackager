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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.SystemUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import fvarrui.maven.plugin.javapackager.utils.DebPackage;
import fvarrui.maven.plugin.javapackager.utils.VelocityUtils;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${session}", readonly = true)
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	private ExecutionEnvironment env;

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(defaultValue = "${project.build.directory}/${project.name}-${project.version}.jar", property = "jarFile", required = true)
	private File jarFile;

	@Parameter(defaultValue = "${project.build.directory}/app/${project.name}", property = "executable", required = true)
	private File executable;

	@Parameter(defaultValue = "assets/windows/${project.name}.iss", property = "issFile", required = true)
	private File issFile;

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

		copyAllDependencies();

		if (SystemUtils.IS_OS_MAC_OSX) {

			if (iconFile == null)
				iconFile = new File("assets/mac", mavenProject.getName() + ".icns");

			createLinuxExecutable();
		}

		if (SystemUtils.IS_OS_LINUX) {

			if (iconFile == null)
				iconFile = new File("assets/linux", mavenProject.getName() + ".png");

			createLinuxExecutable();
			generateDebPackage();

		}

		if (SystemUtils.IS_OS_WINDOWS) {

			if (iconFile == null)
				iconFile = new File("assets/windows", mavenProject.getName() + ".ico");

			createWindowsExecutable();
//			generateWindowsInstaller();
		}

	}

	private void createLinuxExecutable() throws MojoExecutionException {

		getLog().info("Creating GNU/Linux executable...");

		// concat startup.sh script + generated jar
		InputStream startup = null, jar = null;
		FileOutputStream binary = null;
		try {

			startup = getClass().getResourceAsStream("/linux/startup.sh");
			jar = new FileInputStream(jarFile);
			binary = new FileOutputStream(executable);

			IOUtil.copy(startup, binary);
			IOUtil.copy(jar, binary);
			binary.flush();

			binary.close();
			startup.close();
			jar.close();

		} catch (IOException e) {
			throw new MojoExecutionException("Error concatenating linux/startup.sh and runnable jar file", e);
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
						element(name("outfile"), executable.getAbsolutePath() + ".exe"),
						element(name("icon"), iconFile.getAbsolutePath()),
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
								element(name("productVersion"), "${project.version}"),
								element(name("txtProductVersion"), "${project.version}.0"),
								element(name("productName"), "${project.name}"),
								element(name("originalFilename"), "${project.name}.exe")
								)
						),
				env);
	}

	private void generateWindowsInstaller() throws MojoExecutionException {
		getLog().info("Generating Windows installer...");

		// TODO generate iss file from velocity template

		// generate windows installer with inno setup command line compiler
		try {
			String[] command = { "iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + executable.getAbsolutePath(), issFile.getAbsolutePath() };
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			getLog().warn("Could't generate Windows installer. Inno Setup isn't installed or 'iscc.exe' folder not declared in PATH");
		}

	}

	private void generateDebPackage() throws MojoExecutionException {
		getLog().info("Generating DEB package ...");

		File debDir = new File(outputDirectory, "linux");
		debDir.mkdir();

		File controlFile = new File(debDir, "control");

		DebPackage deb = new DebPackage();
		deb.setPackageName(mavenProject.getName());
		deb.setVersion(mavenProject.getVersion());
		deb.setDescription(mavenProject.getDescription());
		deb.setMaintainerName(mavenProject.getOrganization().getName());
		deb.setMaintainerEmail(mavenProject.getOrganization().getUrl());

		try {
			VelocityUtils.render("linux/control.vtl", controlFile, "deb", deb);
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
					element(name("controlDir"), debDir.getAbsolutePath()),
					element(name("deb"), outputDirectory.getAbsolutePath() + "/${project.name}_${project.version}.deb"),
					element(name("dataSet"),
							element(name("data"), 
									element(name("type"), "directory"),
									element(name("src"), outputDirectory.getAbsolutePath() + "/app"),
									element(name("mapper"), 
											element(name("type"), "perm"),
											element(name("prefix"), "/opt/${project.name}")
											),
									element(name("excludes"), executable.getName() + ",jre/bin/java")
									)
							),
							element(name("data"), 
									element(name("type"), "file"),
									element(name("src"), outputDirectory.getAbsolutePath() + "/app/${project.name}"),
									element(name("mapper"), 
											element(name("type"), "perm"), 
											element(name("filemode"), "755"),
											element(name("prefix"), "/opt/${project.name}")
											)
									)
							),
				env);
		/*
		 * <data> <type>file</type> <src>assets/linux/${project.name}.desktop</src>
		 * <mapper> <type>perm</type> <prefix>/usr/share/applications</prefix> </mapper>
		 * </data> <data> <type>file</type>
		 * <src>assets/linux/${project.name}.policy</src> <mapper> <type>perm</type>
		 * <prefix>/usr/share/polkit-1/actions</prefix> </mapper> </data> <data>
		 * <type>file</type> <src>${project.build.directory}/app/jre/bin/java</src>
		 * <mapper> <type>perm</type> <filemode>755</filemode>
		 * <prefix>/opt/${project.name}/jre/bin</prefix> </mapper> </data> <data>
		 * <type>link</type>
		 * <linkTarget>/opt/${project.name}/${project.name}</linkTarget>
		 * <linkName>/usr/local/bin/${project.name}</linkName> <symlink>true</symlink>
		 * <mapper> <type>perm</type> <filemode>777</filemode> </mapper> </data>
		 * </dataSet> </configuration> </execution> </executions> </plugin>
		 */
	}

	private void copyAllDependencies() throws MojoExecutionException {
		getLog().info("Copying all dependencies to app folder ...");
		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"), 
						artifactId("maven-dependency-plugin"), 
						version("3.1.1")
						),
				goal("copy-dependencies"),
				configuration(
						element(name("outputDirectory"), outputDirectory.getAbsoluteFile() + "/app/libs")
						), 
				env);
	}

}
