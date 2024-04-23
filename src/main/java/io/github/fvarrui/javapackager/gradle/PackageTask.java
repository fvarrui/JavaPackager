package io.github.fvarrui.javapackager.gradle;

import static io.github.fvarrui.javapackager.utils.ObjectUtils.defaultIfBlank;
import static io.github.fvarrui.javapackager.utils.ObjectUtils.defaultIfNull;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

import groovy.lang.Closure;
import io.github.fvarrui.javapackager.model.Arch;
import io.github.fvarrui.javapackager.model.FileAssociation;
import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.Manifest;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.Scripts;
import io.github.fvarrui.javapackager.model.Template;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFactory;

/**
 * Packaging task fro Gradle 
 */
public class PackageTask extends AbstractPackageTask {
		
	// ===============
	// task parameters
	// ===============
	
	@Input
	@Optional
	private Platform platform;
	
	public Platform getPlatform() {
		return platform;
	}
	
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	@Input
	@Optional
	private List<String> additionalModules;
	
	public List<String> getAdditionalModules() {
		return additionalModules;
	}
	
	public void setAdditionalModules(List<String> additionalModules) {
		this.additionalModules = additionalModules;
	}

	@Input
	@Optional
	private List<File> additionalResources;
	
	public List<File> getAdditionalResources() {
		return additionalResources;
	}
	
	public void setAdditionalResources(List<File> additionalResources) {
		this.additionalResources = additionalResources;
	}
	
	@Input
	@Optional
	private Boolean administratorRequired;
	
	public Boolean isAdministratorRequired() {
		return administratorRequired;
	}
	
	public void setAdministratorRequired(Boolean administratorRequired) {
		this.administratorRequired = administratorRequired;
	}

	@InputDirectory
	@Optional 
	private File assetsDir;

	public File getAssetsDir() {
		return assetsDir;
	}
	
	public void setAssetsDir(File assetsDir) {
		this.assetsDir = assetsDir;
	}

	@InputDirectory
	@Optional
	private File packagingJdk;

	public File getPackagingJdk() {
		return packagingJdk;
	}

	public void setPackagingJdk(File packagingJdk) {
		this.packagingJdk = packagingJdk;
	}
	
	@Input
	@Optional
	private Boolean bundleJre;
	
	public Boolean isBundleJre() {
		return bundleJre;
	}
	
	public void setBundleJre(Boolean bundleJre) {
		this.bundleJre = bundleJre;
	}
	
	@Input
	@Optional
	private Boolean copyDependencies;
	
	public Boolean isCopyDependencies() {
		return copyDependencies;
	}
	
	public void setCopyDependencies(Boolean copyDependencies) {
		this.copyDependencies = copyDependencies;
	}

	@Input
	@Optional
	private Boolean createTarball;
	
	public Boolean isCreateTarball() {
		return createTarball;
	}
	
	public void setCreateTarball(Boolean createTarball) {
		this.createTarball = createTarball;
	}

	@Input
	@Optional
	private Boolean createZipball;
	
	public Boolean isCreateZipball() {
		return createZipball;
	}
	
	public void setCreateZipball(Boolean createZipball) {
		this.createZipball = createZipball;
	}
	
	@Input
	@Optional
	private Boolean customizedJre;
	
	public Boolean isCustomizedJre() {
		return customizedJre;
	}
	
	public void setCustomizedJre(Boolean customizedJre) {
		this.customizedJre = customizedJre;
	}
	
	@Input
	@Optional
	private String appDescription;
	
	public String getAppDescription() {
		return appDescription;
	}
	
	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}
	
	@Input
	@Optional
	private String displayName;

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
	private Map<String, String> extra;
	
	public Map<String, String> getExtra() {
		return extra;
	}
	
	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}
	
	@Input
	@Optional
	private Boolean generateInstaller;
	
	public Boolean isGenerateInstaller() {
		return generateInstaller;
	}
	
	public void setGenerateInstaller(Boolean generateInstaller) {
		this.generateInstaller = generateInstaller;
	}
	
	@Input
	@Optional
	private Boolean forceInstaller;
	
	public Boolean isForceInstaller() {
		return forceInstaller;
	}
	
	public void setForceInstaller(Boolean forceInstaller) {
		this.forceInstaller = forceInstaller;
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
	private String jreDirectoryName;
	
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
	private LinuxConfig linuxConfig;
	
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
	private MacConfig macConfig;
	
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
	@Optional	
	private String mainClass;
	
	public String getMainClass() {
		return mainClass;
	}
	
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
	@Input
	@Optional
	private List<String> modules;
	
	public List<String> getModules() {
		return modules;
	}
	
	public void setModules(List<String> modules) {
		this.modules = modules;
	}
	
	@Input 
	@Optional
	private String appName;
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Input 
	@Optional
	private String organizationEmail;
	
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
	private Boolean useResourcesAsWorkingDir;
	
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
	private List<String> vmArgs;

	public List<String> getVmArgs() {
		return vmArgs;
	}
	
	public void setVmArgs(List<String> vmArgs) {
		this.vmArgs = vmArgs;
	}

	@Input
	@Optional
	private WindowsConfig winConfig;
	
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
	private String version;
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@OutputDirectory
	@Optional
	private File outputDirectory;
	
	public File getOutputDirectory() {
		return outputDirectory;
	}
	
	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	@Input
	@Optional	
	private String classpath;

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	
	@Input
	@Optional	
	private String jreMinVersion;

	public String getJreMinVersion() {
		return jreMinVersion;
	}

	public void setJreMinVersion(String jreMinVersion) {
		this.jreMinVersion = jreMinVersion;
	}
	
	@Input
	@Optional
	private Manifest manifest;
	
	public Manifest getManifest() {
		return manifest;
	}
	
    public Manifest manifest(Closure<Manifest> closure) {
        manifest = new Manifest();
        getProject().configure(manifest, closure);
        return manifest;
    }
    
	@Input
	@Optional
	private List<File> additionalModulePaths;
	
	public List<File> getAdditionalModulePaths() {
		return additionalModulePaths;
	}
	
	public void setAdditionalModulePaths(List<File> additionalModulePaths) {
		this.additionalModulePaths = additionalModulePaths;
	}

	@Input
	@Optional
	private List<FileAssociation> fileAssociations;
	
	public List<FileAssociation> getFileAssociations() {
		return fileAssociations;
	}
	
	public void setFileAssociations(List<FileAssociation> fileAssociations) {
		this.fileAssociations = fileAssociations;
	}
	
	@Input
	@Optional
	private Scripts scripts;
	
	public Scripts getScripts() {
		return scripts;
	}
	
	public void setScripts(Scripts scripts) {
		this.scripts = scripts;
	}
	
    public Scripts scripts(Closure<Scripts> closure) {
    	scripts = new Scripts();
        getProject().configure(scripts, closure);
        return scripts;
    }
    
	@Input
	@Optional	
	private Arch arch;

	public Arch getArch() {
		return arch;
	}

	public void setArch(Arch arch) {
		this.arch = arch;
	}
	
	@Input
	@Optional	
	private List<Template> templates;

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}
	
	@Input
	@Optional
	private DuplicatesStrategy duplicatesStrategy;
	
	public DuplicatesStrategy getDuplicatesStrategy() {
		return duplicatesStrategy;
	}
	
	public void setDuplicatesStrategy(DuplicatesStrategy duplicatesStrategy) {
		this.duplicatesStrategy = duplicatesStrategy;
	}

	// ===============
	// create packager
	// ===============
	
	@SuppressWarnings("unchecked")
	@Override
	protected Packager createPackager() throws Exception {

		PackagePluginExtension extension = getProject().getExtensions().findByType(PackagePluginExtension.class);
		
		Context.getGradleContext().setDuplicatesStrategy(defaultIfNull(duplicatesStrategy, extension.getDuplicatesStrategy()));
		
		return
			(Packager) PackagerFactory
				.createPackager(defaultIfNull(platform, extension.getPlatform()))
					.additionalModules(defaultIfNull(additionalModules, extension.getAdditionalModules()))
					.additionalModulePaths(defaultIfNull(additionalModulePaths, extension.getAdditionalModulePaths()))
					.additionalResources(defaultIfNull(additionalResources, extension.getAdditionalResources()))
					.administratorRequired(defaultIfNull(administratorRequired, extension.getAdministratorRequired()))
					.arch(defaultIfNull(arch, extension.getArch()))
					.assetsDir(defaultIfNull(assetsDir, extension.getAssetsDir()))
					.bundleJre(defaultIfNull(bundleJre, extension.getBundleJre()))
					.classpath(defaultIfNull(classpath, extension.getClasspath()))
					.copyDependencies(defaultIfNull(copyDependencies, extension.getCopyDependencies()))
					.createTarball(defaultIfNull(createTarball, extension.getCreateTarball()))
					.createZipball(defaultIfNull(createZipball, extension.getCreateZipball()))
					.customizedJre(defaultIfNull(customizedJre, extension.getCustomizedJre()))
					.description(defaultIfNull(appDescription, extension.getDescription()))
					.displayName(defaultIfNull(displayName, extension.getDisplayName()))
					.envPath(defaultIfNull(envPath, extension.getEnvPath()))
					.extra(defaultIfNull(extra, extension.getExtra()))
					.fileAssociations(defaultIfNull(fileAssociations, extension.getFileAssociations()))
					.forceInstaller(defaultIfNull(forceInstaller, extension.isForceInstaller()))					
					.generateInstaller(defaultIfNull(generateInstaller, extension.getGenerateInstaller()))
					.jdkPath(defaultIfNull(jdkPath, extension.getJdkPath()))
					.jreDirectoryName(defaultIfBlank(jreDirectoryName, extension.getJreDirectoryName()))
					.jreMinVersion(defaultIfBlank(jreMinVersion, extension.getJreMinVersion()))
					.jrePath(defaultIfNull(jrePath, extension.getJrePath()))
					.licenseFile(defaultIfNull(licenseFile, extension.getLicenseFile()))
					.linuxConfig(defaultIfNull(linuxConfig, extension.getLinuxConfig()))
					.macConfig(defaultIfNull(macConfig, extension.getMacConfig()))
					.mainClass(defaultIfNull(mainClass, extension.getMainClass()))
					.manifest(defaultIfNull(manifest, extension.getManifest()))
					.modules(defaultIfNull(modules, extension.getModules()))
					.name(defaultIfNull(appName, extension.getName()))
					.organizationEmail(defaultIfNull(organizationEmail, extension.getOrganizationEmail()))
					.organizationName(defaultIfNull(organizationName, extension.getOrganizationName()))
					.organizationUrl(defaultIfNull(organizationUrl, extension.getOrganizationUrl()))
					.outputDirectory(defaultIfNull(outputDirectory, extension.getOutputDirectory()))
					.packagingJdk(defaultIfNull(packagingJdk, extension.getPackagingJdk(), Context.getGradleContext().getDefaultToolchain()))
					.runnableJar(defaultIfNull(runnableJar, extension.getRunnableJar()))
					.scripts(defaultIfNull(scripts, extension.getScripts()))
					.templates(defaultIfNull(templates, extension.getTemplates()))
					.useResourcesAsWorkingDir(defaultIfNull(useResourcesAsWorkingDir, extension.isUseResourcesAsWorkingDir()))
					.url(defaultIfNull(url, extension.getUrl()))
					.version(defaultIfNull(version, extension.getVersion(), getProject().getVersion().toString()))
					.vmArgs(defaultIfNull(vmArgs, extension.getVmArgs()))
					.winConfig(defaultIfNull(winConfig, extension.getWinConfig()));

	}
	
}
