package io.github.fvarrui.javapackager.maven;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;

import java.io.File;
import java.util.List;
import java.util.Map;

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

import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.Manifest;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFactory;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class PackageMojo extends AbstractMojo {
	
	// maven components
	
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${session}", readonly = true)
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;
	
	// plugin parameters
	
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
	@Parameter(property = "jrePath", required = false)
	private File jrePath;

	/**
	 * Path to JDK folder. If specified, it will use this JDK modules to generate a customized JRE. Allows generating JREs for different platforms.
	 */
	@Parameter(property = "jdkPath", required = false)
	private File jdkPath;

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
    private File runnableJar;

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
	 * GNU/Linux specific config
	 */
	@Parameter(property = "linuxConfig", required = false)
	private LinuxConfig linuxConfig;
	
	/**
	 * Mac OS X specific config
	 */
	@Parameter(property = "macConfig", required = false)
	private MacConfig macConfig;
	
	/**
	 * Windows specific config
	 */
	@Parameter(property = "winConfig", required = false)
	private WindowsConfig winConfig;

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

	/**
	 * Extra properties for customized Velocity templates, accesible through '$this.extra' map. 
	 */
	@Parameter(required = false)
	private Map<String, String> extra;
	
	/**
	 * Uses app resources folder as default working directory.
	 */
	@Parameter(defaultValue = "true", property = "useResourcesAsWorkingDir", required = false)
	private boolean useResourcesAsWorkingDir;
	
	/**
	 * Assets directory
	 */
	@Parameter(defaultValue = "${project.basedir}/assets", property = "assetsDir", required = false)
	private File assetsDir;
	
	/**
	 * Classpath
	 */
	@Parameter(property = "classpath", required = false)
	private String classpath;

	/**
	 * JRE min version
	 */
	@Parameter(property = "jreMinVersion", required = false)
	private String jreMinVersion;
	
	/**
	 * Additional JAR manifest entries  
	 */
	@Parameter(required = false)
	private Manifest manifest;

	public void execute() throws MojoExecutionException {
		
		Context.setContext(
				new MavenContext(
						executionEnvironment(mavenProject, mavenSession, pluginManager), 
						getLog()
						)
				);

		try {

			Packager packager = 
				(Packager) PackagerFactory
					.createPackager(platform)
						.additionalModules(additionalModules)
						.additionalResources(additionalResources)
						.administratorRequired(administratorRequired)
						.version(version)
						.assetsDir(assetsDir)
						.bundleJre(bundleJre)
						.copyDependencies(copyDependencies)
						.createTarball(createTarball)
						.createZipball(createZipball)
						.customizedJre(customizedJre)
						.description(description)
						.displayName(displayName)
						.envPath(envPath)
						.extra(extra)
						.generateInstaller(generateInstaller)
						.iconFile(iconFile)
						.jdkPath(jdkPath)
						.jreDirectoryName(jreDirectoryName)
						.jreMinVersion(jreMinVersion)
						.jrePath(jrePath)
						.licenseFile(licenseFile)
						.linuxConfig(linuxConfig)
						.macConfig(macConfig)
						.mainClass(mainClass)
						.manifest(manifest)
						.modules(modules)
						.name(defaultIfBlank(name, Context.getMavenContext().getEnv().getMavenProject().getArtifactId()))
						.organizationEmail(organizationEmail)
						.organizationName(organizationName)
						.organizationUrl(organizationUrl)
						.outputDirectory(outputDirectory)
						.classpath(classpath)
						.runnableJar(runnableJar)
						.useResourcesAsWorkingDir(useResourcesAsWorkingDir)
						.url(url)
						.vmArgs(vmArgs)
						.winConfig(winConfig);
			
			// generate app, installers and bundles
			packager.createApp();
			packager.generateInstallers();
			packager.createBundles();
			
		} catch (Exception e) {

			throw new MojoExecutionException(e.getMessage(), e);
			
		}
		

	}

	
}
