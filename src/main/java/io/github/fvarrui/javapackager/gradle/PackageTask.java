package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import groovy.lang.Closure;
import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFactory;

public class PackageTask extends DefaultTask {
	
	public static final String PACKAGE_TASK_NAME = "package";	
	
	// ===============
	// task parameters
	// ===============
	
	@Input
	private Platform platform = Platform.auto;
	
	public Platform getPlatform() {
		return platform;
	}
	
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	@Input
	@Optional
	private List<String> additionalModules = new ArrayList<>();
	
	public List<String> getAdditionalModules() {
		return additionalModules;
	}
	
	public void setAdditionalModules(List<String> additionalModules) {
		this.additionalModules = additionalModules;
	}

	@Input
	@Optional
	private List<File> additionalResources = new ArrayList<>();
	
	public List<File> getAdditionalResources() {
		return additionalResources;
	}
	
	public void setAdditionalResources(List<File> additionalResources) {
		this.additionalResources = additionalResources;
	}
	
	@Input
	@Optional
	private Boolean administratorRequired = false;
	
	public Boolean isAdministratorRequired() {
		return administratorRequired;
	}
	
	public void setAdministratorRequired(Boolean administratorRequired) {
		this.administratorRequired = administratorRequired;
	}

	@InputDirectory
	@Optional 
	private File assetsDir = new File(getProject().getProjectDir(), "assets");

	public File getAssetsDir() {
		return assetsDir;
	}
	
	public void setAssetsDir(File assetsDir) {
		this.assetsDir = assetsDir;
	}
	
	@Input
	@Optional
	private Boolean bundleJre = true;
	
	public Boolean isBundleJre() {
		return bundleJre;
	}
	
	public void setBundleJre(Boolean bundleJre) {
		this.bundleJre = bundleJre;
	}
	
	@Input
	@Optional
	private Boolean copyDependencies = true;
	
	public Boolean isCopyDependencies() {
		return copyDependencies;
	}
	
	public void setCopyDependencies(Boolean copyDependencies) {
		this.copyDependencies = copyDependencies;
	}

	@Input
	@Optional
	private Boolean createTarball = false;
	
	public Boolean isCreateTarball() {
		return createTarball;
	}
	
	public void setCreateTarball(Boolean createTarball) {
		this.createTarball = createTarball;
	}

	@Input
	@Optional
	private Boolean createZipball = false;
	
	public Boolean isCreateZipball() {
		return createZipball;
	}
	
	public void setCreateZipball(Boolean createZipball) {
		this.createZipball = createZipball;
	}
	
	@Input
	@Optional
	private Boolean customizedJre = true;
	
	public Boolean isCustomizedJre() {
		return customizedJre;
	}
	
	public void setCustomizedJre(Boolean customizedJre) {
		this.customizedJre = customizedJre;
	}
	
	@Input
	@Optional
	private String appDescription = getProject().getDescription();
	
	public String getAppDescription() {
		return appDescription;
	}
	
	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}
	
	@Input
	@Optional
	private String displayName = getProject().getDisplayName();

	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Input
	@Optional
	private String envPath;
	
	public String getEnvPath() {
		return envPath;
	}
	
	public void setEnvPath(String envPath) {
		this.envPath = envPath;
	}
	
	@Input
	@Optional
	private Map<String, String> extra = new HashMap<>();
	
	public Map<String, String> getExtra() {
		return extra;
	}
	
	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}
	
	@Input
	@Optional
	private Boolean generateInstaller = true;
	
	public Boolean isGenerateInstaller() {
		return generateInstaller;
	}
	
	public void setGenerateInstaller(Boolean generateInstaller) {
		this.generateInstaller = generateInstaller;
	}
	
	@InputFile
	@Optional
	private File iconFile;
	
	public File getIconFile() {
		return iconFile;
	}
	
	public void setIconFile(File iconFile) {
		this.iconFile = iconFile;
	}
	
	@InputDirectory
	@Optional
	private File jdkPath;
	
	public File getJdkPath() {
		return jdkPath;
	}
	
	public void setJdkPath(File jdkPath) {
		this.jdkPath = jdkPath;
	}
	
	@Input
	@Optional
	private String jreDirectoryName = "jre";
	
	public String getJreDirectoryName() {
		return jreDirectoryName;
	}
	
	public void setJreDirectoryName(String jreDirectoryName) {
		this.jreDirectoryName = jreDirectoryName;
	}
	
	@InputDirectory
	@Optional
	private File jrePath;

	public File getJrePath() {
		return jrePath;
	}
	
	public void setJrePath(File jrePath) {
		this.jrePath = jrePath;
	}

	@InputFile
	@Optional
	private File licenseFile;
	
	public File getLicenseFile() {
		return licenseFile;
	}
	
	public void setLicenseFile(File licenseFile) {
		this.licenseFile = licenseFile;
	}
	
	@Input
	@Optional
	private LinuxConfig linuxConfig = new LinuxConfig();
	
	public LinuxConfig getLinuxConfig() {
		return linuxConfig;
	}
	
	public void setLinuxConfig(LinuxConfig linuxConfig) {
		this.linuxConfig = linuxConfig;
	}
	
    public LinuxConfig linuxConfig(Closure<LinuxConfig> closure) {
        linuxConfig = new LinuxConfig();
        getProject().configure(linuxConfig, closure);
        return linuxConfig;
    }
	
	@Input
	@Optional
	private MacConfig macConfig = new MacConfig();
	
	public MacConfig getMacConfig() {
		return macConfig;
	}
	
	public void setMacConfig(MacConfig macConfig) {
		this.macConfig = macConfig;
	}

    public MacConfig macConfig(Closure<MacConfig> closure) {
        macConfig = new MacConfig();
        getProject().configure(macConfig, closure);
        return macConfig;
    }
	
	@Input
	private String mainClass;
	
	public String getMainClass() {
		return mainClass;
	}
	
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
	@Input
	@Optional
	private List<String> modules = new ArrayList<>();
	
	public List<String> getModules() {
		return modules;
	}
	
	public void setModules(List<String> modules) {
		this.modules = modules;
	}
	
	@Input 
	@Optional
	private String appName = getProject().getName();
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Input 
	@Optional
	private String organizationEmail = "";
	
	public String getOrganizationEmail() {
		return organizationEmail;
	}
	
	public void setOrganizationEmail(String organizationEmail) {
		this.organizationEmail = organizationEmail;
	}
	
	@Input 
	@Optional
	private String organizationName;
	
	public String getOrganizationName() {
		return organizationName;
	}
	
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Input 
	@Optional
	private String organizationUrl;
	
	public String getOrganizationUrl() {
		return organizationUrl;
	}
	
	public void setOrganizationUrl(String organizationUrl) {
		this.organizationUrl = organizationUrl;
	}

	@InputFile
	@Optional
	private File runnableJar;
	
	public File getRunnableJar() {
		return runnableJar;
	}
	
	public void setRunnableJar(File runnableJar) {
		this.runnableJar = runnableJar;
	}
	
	@Input
	@Optional
	private Boolean useResourcesAsWorkingDir = true;
	
	public Boolean isUseResourcesAsWorkingDir() {
		return useResourcesAsWorkingDir;
	}
	
	public void setUseResourcesAsWorkingDir(Boolean useResourcesAsWorkingDir) {
		this.useResourcesAsWorkingDir = useResourcesAsWorkingDir;
	}
	
	@Input 
	@Optional
	private String url;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Input
	@Optional
	private List<String> vmArgs = new ArrayList<>();

	public List<String> getVmArgs() {
		return vmArgs;
	}
	
	public void setVmArgs(List<String> vmArgs) {
		this.vmArgs = vmArgs;
	}

	@Input
	@Optional
	private WindowsConfig winConfig = new WindowsConfig();
	
	public WindowsConfig getWinConfig() {
		return winConfig;
	}

    public WindowsConfig winConfig(Closure<WindowsConfig> closure) {
        winConfig = new WindowsConfig();
        getProject().configure(winConfig, closure);
        return winConfig;
    }
	
	@Input
	@Optional
	private String version = getProject().getVersion().toString();
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@OutputDirectory
	@Optional
	private File outputDirectory = getProject().getBuildDir();
	
	public File getOutputDirectory() {
		return outputDirectory;
	}
	
	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	@OutputFiles
	private List<File> outputFiles;
	
	public List<File> getOutputFiles() {
		return outputFiles != null ? outputFiles : new ArrayList<>();
	}
	
	// ================
	// task constructor
	// ================
	
	public PackageTask() {
		super();
		setGroup(PackagePlugin.GROUP_NAME);
		setDescription("Packages the application as a native Windows, Mac OS X or GNU/Linux executable and creates an installer");
	}
	
	// ===========
	// task action
	// ===========
	
	@TaskAction
	public void doPackage() {
		
		try {
			
			Packager packager = 
				(Packager) PackagerFactory
					.createPackager(platform)
						.additionalModules(additionalModules)
						.additionalResources(additionalResources)
						.administratorRequired(administratorRequired)
						.appVersion(version)
						.assetsDir(assetsDir)
						.bundleJre(bundleJre)
						.copyDependencies(copyDependencies)
						.createTarball(createTarball)
						.createZipball(createZipball)
						.customizedJre(customizedJre)
						.description(appDescription)
						.displayName(displayName)
						.envPath(envPath)
						.extra(extra)
						.generateInstaller(generateInstaller)
						.iconFile(iconFile)
						.jdkPath(jdkPath)
						.jreDirectoryName(jreDirectoryName)
						.jrePath(jrePath)
						.licenseFile(licenseFile)
						.linuxConfig(linuxConfig)
						.macConfig(macConfig)
						.mainClass(mainClass)
						.modules(modules)
						.name(appName)
						.organizationEmail(organizationEmail)
						.organizationName(organizationName)
						.organizationUrl(organizationUrl)
						.outputDirectory(outputDirectory)
						.runnableJar(runnableJar)
						.useResourcesAsWorkingDir(useResourcesAsWorkingDir)
						.url(url)
						.vmArgs(vmArgs)
						.winConfig(winConfig);
			
			// generates app, installers and bundles
			File app = packager.createApp();
			List<File> installers = packager.generateInstallers();
			List<File> bundles = packager.createBundles();
			
			// sets generated files as output
			outputFiles = new ArrayList<>();
			outputFiles.add(app);
			outputFiles.addAll(installers);
			outputFiles.addAll(bundles);
			
		} catch (Exception e) {

			throw new RuntimeException(e.getMessage(), e);
			
		}


	}
	
}
