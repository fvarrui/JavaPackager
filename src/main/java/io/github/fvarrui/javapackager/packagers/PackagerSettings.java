package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.model.FileAssociation;
import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.Manifest;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.Scripts;
import io.github.fvarrui.javapackager.model.WindowsConfig;

/**
 * Common packagers' settings
 */
public class PackagerSettings {

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
	protected File jdkPath;
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
	protected File assetsDir;
	protected String classpath;
	protected String jreMinVersion;
	protected Manifest manifest;
	protected List<File> additionalModulePaths;
	protected List<FileAssociation> fileAssociations;
	protected File packagingJdk;
	protected Scripts scripts;

	/**
	 * Get packaging JDK
	 * @return Packaging JDK
	 */
	public File getPackagingJdk() {
		return packagingJdk;
	}

	/**
	 * Get output directory
	 * @return Output directory
	 */
	public File getOutputDirectory() {
		return outputDirectory;
	}

	/**
	 * Get license file 
	 * @return License file
	 */
	public File getLicenseFile() {
		return licenseFile;
	}

	/**
	 * Get icon file
	 * @return Icon file
	 */
	public File getIconFile() {
		return iconFile;
	}

	/**
	 * Get generate installer
	 * @return Generate installer
	 */
	public Boolean getGenerateInstaller() {
		return generateInstaller;
	}

	/**
	 * Get main class
	 * @return Main class
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * Get name
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get display name
	 * @return Display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Get version
	 * @return Version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Get description
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get URL
	 * @return URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Get administrator required
	 * @return Administrator required
	 */
	public Boolean getAdministratorRequired() {
		return administratorRequired;
	}

	/**
	 * Get organization name
	 * @return Organization name
	 */
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * Get organization URL
	 * @return Organization URL
	 */
	public String getOrganizationUrl() {
		return organizationUrl;
	}

	/**
	 * Get organization email
	 * @return Organization email
	 */
	public String getOrganizationEmail() {
		return organizationEmail;
	}

	/**
	 * Get bundle JRE
	 * @return Bundle JRE
	 */
	public Boolean getBundleJre() {
		return bundleJre;
	}

	/**
	 * Get customized JRE
	 * @return Customized JRE
	 */
	public Boolean getCustomizedJre() {
		return customizedJre;
	}

	/**
	 * Get JRE path
	 * @return JRE path
	 */
	public File getJrePath() {
		return jrePath;
	}

	/**
	 * Get JDK path
	 * @return JDK path
	 */
	public File getJdkPath() {
		return jdkPath;
	}

	/**
	 * Get additional resourcxes
	 * @return Additional resources
	 */
	public List<File> getAdditionalResources() {
		return additionalResources;
	}

	/**
	 * Get Modules
	 * @return Modules
	 */
	public List<String> getModules() {
		return modules;
	}

	/**
	 * Get additional modules
	 * @return Additional modules
	 */
	public List<String> getAdditionalModules() {
		return additionalModules;
	}

	/**
	 * Get platform
	 * @return Platform
	 */
	public Platform getPlatform() {
		return platform;
	}

	/**
	 * Get env path
	 * @return Env path
	 */
	public String getEnvPath() {
		return envPath;
	}

	/**
	 * Get VM args
	 * @return VM args
	 */
	public List<String> getVmArgs() {
		return vmArgs;
	}

	/**
	 * Get runnable JAR
	 * @return Runnable JAR
	 */
	public File getRunnableJar() {
		return runnableJar;
	}

	/**
	 * Get copy dependencies 
	 * @return Copy dependencies
	 */
	public Boolean getCopyDependencies() {
		return copyDependencies;
	}

	/**
	 * Get JRE directory name
	 * @return JRE directory name
	 */
	public String getJreDirectoryName() {
		return jreDirectoryName;
	}

	/**
	 * Get Windows config
	 * @return Windows config
	 */
	public WindowsConfig getWinConfig() {
		return winConfig;
	}

	/**
	 * Get Linux config
	 * @return Linux config
	 */
	public LinuxConfig getLinuxConfig() {
		return linuxConfig;
	}

	/**
	 * Get Mac OS config
	 * @return Mac OS config
	 */
	public MacConfig getMacConfig() {
		return macConfig;
	}

	/** 
	 * Get create tarball
	 * @return Create tarball
	 */
	public Boolean getCreateTarball() {
		return createTarball;
	}

	/**
	 * Get create zipball
	 * @return Create zipball
	 */
	public Boolean getCreateZipball() {
		return createZipball;
	}

	/**
	 * Get extra parameters
	 * @return Extra parameters
	 */
	public Map<String, String> getExtra() {
		return extra;
	}

	/**
	 * Get if it has to use resources folder as working directory
	 * @return Use resources folder as working directory
	 */
	public boolean isUseResourcesAsWorkingDir() {
		return useResourcesAsWorkingDir;
	}

	/**
	 * Get assets dir
	 * @return Assets dir
	 */
	public File getAssetsDir() {
		return assetsDir;
	}

	/**
	 * Get classpath
	 * @return Classpath
	 */
	public String getClasspath() {
		return classpath;
	}

	/**
	 * Get JRE min version
	 * @return JRE min version
	 */
	public String getJreMinVersion() {
		return jreMinVersion;
	}

	/**
	 * Get Manifest
	 * @return manifest
	 */
	public Manifest getManifest() {
		return manifest;
	}

	/**
	 * Get additional modules paths
	 * @return Additional module paths
	 */
	public List<File> getAdditionalModulePaths() {
		return additionalModulePaths;
	}

	/**
	 * Get file associations
	 * @return File associations
	 */
	public List<FileAssociation> getFileAssociations() {
		return fileAssociations;
	}

	/**
	 * Get scripts
	 * @return Scripts
	 */
	public Scripts getScripts() {
		return scripts;
	}

	// fluent api

	/**
	 * Set output directory
	 * @param outputDirectory Output directory
	 * @return Packager settings
	 */
	public PackagerSettings outputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
		return this;
	}

	/**
	 * Set packaging JDK
	 * @param packagingJdk Packaging JDK
	 * @return Packager settings
	 */
	public PackagerSettings packagingJdk(File packagingJdk) {
		this.packagingJdk = packagingJdk;
		return this;
	}

	/**
	 * Set license file
	 * @param licenseFile License file
	 * @return Packager settings
	 */
	public PackagerSettings licenseFile(File licenseFile) {
		this.licenseFile = licenseFile;
		return this;
	}

	/**
	 * Set icon file
	 * @param iconFile Icon file
	 * @return Packager settings
	 */
	public PackagerSettings iconFile(File iconFile) {
		this.iconFile = iconFile;
		return this;
	}

	/**
	 * Set generate installer
	 * @param generateInstaller Generate installer
	 * @return Packager settings
	 */
	public PackagerSettings generateInstaller(Boolean generateInstaller) {
		this.generateInstaller = generateInstaller;
		return this;
	}

	public PackagerSettings mainClass(String mainClass) {
		this.mainClass = mainClass;
		return this;
	}

	public PackagerSettings name(String name) {
		this.name = name;
		return this;
	}

	public PackagerSettings displayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public PackagerSettings version(String version) {
		this.version = version;
		return this;
	}

	public PackagerSettings description(String description) {
		this.description = description;
		return this;
	}

	public PackagerSettings url(String url) {
		this.url = url;
		return this;
	}

	public PackagerSettings administratorRequired(Boolean administratorRequired) {
		this.administratorRequired = administratorRequired;
		return this;
	}

	public PackagerSettings organizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	public PackagerSettings organizationUrl(String organizationUrl) {
		this.organizationUrl = organizationUrl;
		return this;
	}

	public PackagerSettings organizationEmail(String organizationEmail) {
		this.organizationEmail = organizationEmail;
		return this;
	}

	public PackagerSettings bundleJre(Boolean bundleJre) {
		this.bundleJre = bundleJre;
		return this;
	}

	public PackagerSettings customizedJre(Boolean customizedJre) {
		this.customizedJre = customizedJre;
		return this;
	}

	public PackagerSettings jrePath(File jrePath) {
		this.jrePath = jrePath;
		return this;
	}

	public PackagerSettings jdkPath(File jdkPath) {
		this.jdkPath = jdkPath;
		return this;
	}

	public PackagerSettings additionalResources(List<File> additionalResources) {
		this.additionalResources = new ArrayList<>(additionalResources);
		return this;
	}

	public PackagerSettings modules(List<String> modules) {
		this.modules = new ArrayList<>(modules);
		return this;
	}

	public PackagerSettings additionalModules(List<String> additionalModules) {
		this.additionalModules = new ArrayList<>(additionalModules);
		return this;
	}

	public PackagerSettings platform(Platform platform) {
		this.platform = platform;
		return this;
	}

	public PackagerSettings envPath(String envPath) {
		this.envPath = envPath;
		return this;
	}

	public PackagerSettings vmArgs(List<String> vmArgs) {
		this.vmArgs = new ArrayList<>(vmArgs);
		return this;
	}

	public PackagerSettings runnableJar(File runnableJar) {
		this.runnableJar = runnableJar;
		return this;
	}

	public PackagerSettings copyDependencies(Boolean copyDependencies) {
		this.copyDependencies = copyDependencies;
		return this;
	}

	public PackagerSettings jreDirectoryName(String jreDirectoryName) {
		this.jreDirectoryName = jreDirectoryName;
		return this;
	}

	public PackagerSettings winConfig(WindowsConfig winConfig) {
		this.winConfig = winConfig;
		return this;
	}

	public PackagerSettings linuxConfig(LinuxConfig linuxConfig) {
		this.linuxConfig = linuxConfig;
		return this;
	}

	public PackagerSettings macConfig(MacConfig macConfig) {
		this.macConfig = macConfig;
		return this;
	}

	public PackagerSettings createTarball(Boolean createTarball) {
		this.createTarball = createTarball;
		return this;
	}

	public PackagerSettings createZipball(Boolean createZipball) {
		this.createZipball = createZipball;
		return this;
	}

	public PackagerSettings extra(Map<String, String> extra) {
		this.extra = extra;
		return this;
	}

	public PackagerSettings useResourcesAsWorkingDir(boolean useResourcesAsWorkingDir) {
		this.useResourcesAsWorkingDir = useResourcesAsWorkingDir;
		return this;
	}

	public PackagerSettings assetsDir(File assetsDir) {
		this.assetsDir = assetsDir;
		return this;
	}

	public PackagerSettings classpath(String classpath) {
		this.classpath = classpath;
		return this;
	}

	public PackagerSettings jreMinVersion(String jreMinVersion) {
		this.jreMinVersion = jreMinVersion;
		return this;
	}

	public PackagerSettings manifest(Manifest manifest) {
		this.manifest = manifest;
		return this;
	}

	public PackagerSettings additionalModulePaths(List<File> additionalModulePaths) {
		this.additionalModulePaths = additionalModulePaths;
		return this;
	}

	public PackagerSettings fileAssociations(List<FileAssociation> fileAssociations) {
		this.fileAssociations = fileAssociations;
		return this;
	}

	public PackagerSettings scripts(Scripts scripts) {
		this.scripts = scripts;
		return this;
	}

	// some helpful methods

	public boolean isThereFileAssociations() {
		return fileAssociations != null && !fileAssociations.isEmpty();
	}

	public String getMimeTypesListAsString(String separator) {
		return StringUtils.join(fileAssociations.stream().map(fa -> fa.getMimeType()).collect(Collectors.toList()),
				separator);
	}

	@Override
	public String toString() {
		return "PackagerSettings [outputDirectory=" + outputDirectory + ", licenseFile=" + licenseFile + ", iconFile="
				+ iconFile + ", generateInstaller=" + generateInstaller + ", mainClass=" + mainClass + ", name=" + name
				+ ", displayName=" + displayName + ", version=" + version + ", description=" + description + ", url="
				+ url + ", administratorRequired=" + administratorRequired + ", organizationName=" + organizationName
				+ ", organizationUrl=" + organizationUrl + ", organizationEmail=" + organizationEmail + ", bundleJre="
				+ bundleJre + ", customizedJre=" + customizedJre + ", jrePath=" + jrePath + ", jdkPath=" + jdkPath
				+ ", additionalResources=" + additionalResources + ", modules=" + modules + ", additionalModules="
				+ additionalModules + ", platform=" + platform + ", envPath=" + envPath + ", vmArgs=" + vmArgs
				+ ", runnableJar=" + runnableJar + ", copyDependencies=" + copyDependencies + ", jreDirectoryName="
				+ jreDirectoryName + ", winConfig=" + winConfig + ", linuxConfig=" + linuxConfig + ", macConfig="
				+ macConfig + ", createTarball=" + createTarball + ", createZipball=" + createZipball + ", extra="
				+ extra + ", useResourcesAsWorkingDir=" + useResourcesAsWorkingDir + ", assetsDir=" + assetsDir
				+ ", classpath=" + classpath + ", jreMinVersion=" + jreMinVersion + ", manifest=" + manifest
				+ ", additionalModulePaths=" + additionalModulePaths + ", fileAssociations=" + fileAssociations
				+ ", packagingJdk=" + packagingJdk + ", scripts=" + scripts + "]";
	}

}
