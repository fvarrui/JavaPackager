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
	protected boolean forceInstaller;
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
	 * Get force installer
	 * @return Force installer
	 */
	public boolean isForceInstaller() {
		return forceInstaller;
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
	
	/**
	 * Set force installer
	 * @param forceInstaller Force installer
	 * @return Packager settings
	 */
	public PackagerSettings forceInstaller(Boolean forceInstaller) {
		this.forceInstaller = forceInstaller;
		return this;
	}

	/**
	 * Set main class
	 * @param mainClass Main class
	 * @return Packager settings
	 */
	public PackagerSettings mainClass(String mainClass) {
		this.mainClass = mainClass;
		return this;
	}

	/**
	 * Set name
	 * @param name Name
	 * @return Packager settings
	 */
	public PackagerSettings name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Set display name
	 * @param displayName Display name
	 * @return Packager settings
	 */
	public PackagerSettings displayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	/**
	 * Set version
	 * @param version Version
	 * @return Packager settings
	 */
	public PackagerSettings version(String version) {
		this.version = version;
		return this;
	}

	/**
	 * Set description
	 * @param description Description
	 * @return Packager settings
	 */
	public PackagerSettings description(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Set URL
	 * @param url URL
	 * @return Packager settings
	 */
	public PackagerSettings url(String url) {
		this.url = url;
		return this;
	}

	/**
	 *  Set administrator required
	 * @param administratorRequired Administrator required
	 * @return Packager settings
	 */
	public PackagerSettings administratorRequired(Boolean administratorRequired) {
		this.administratorRequired = administratorRequired;
		return this;
	}

	/**
	 * Set organizstion name
	 * @param organizationName Organization name
	 * @return Packager settings
	 */
	public PackagerSettings organizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	/**
	 * Set organization URL
	 * @param organizationUrl Organization URL
	 * @return Packager settings
	 */
	public PackagerSettings organizationUrl(String organizationUrl) {
		this.organizationUrl = organizationUrl;
		return this;
	}

	/**
	 * Set organization email
	 * @param organizationEmail
	 * @return Packager settings
	 */
	public PackagerSettings organizationEmail(String organizationEmail) {
		this.organizationEmail = organizationEmail;
		return this;
	}

	/**
	 * Set bundle JRE
	 * @param bundleJre Bundle JRE
	 * @return Packager settings
	 */
	public PackagerSettings bundleJre(Boolean bundleJre) {
		this.bundleJre = bundleJre;
		return this;
	}

	/**
	 * Set customized JRE
	 * @param customizedJre Customized JRE
	 * @return Packager settings
	 */
	public PackagerSettings customizedJre(Boolean customizedJre) {
		this.customizedJre = customizedJre;
		return this;
	}

	/**
	 * Set JRE path
	 * @param jrePath JRE path
	 * @return Packager settings
	 */
	public PackagerSettings jrePath(File jrePath) {
		this.jrePath = jrePath;
		return this;
	}

	/**
	 * Set JDK path
	 * @param jdkPath JDK path
	 * @return Packager settings
	 */
	public PackagerSettings jdkPath(File jdkPath) {
		this.jdkPath = jdkPath;
		return this;
	}

	/**
	 * Set additional resources list
	 * @param additionalResources Additional resources list
	 * @return Packager settings
	 */
	public PackagerSettings additionalResources(List<File> additionalResources) {
		this.additionalResources = new ArrayList<>(additionalResources);
		return this;
	}

	/**
	 * Set modules list
	 * @param modules Modules list
	 * @return Packager settings
	 */
	public PackagerSettings modules(List<String> modules) {
		this.modules = new ArrayList<>(modules);
		return this;
	}

	/**
	 * Set additional modules list
	 * @param additionalModules Additional modules list
	 * @return Packager settings
	 */
	public PackagerSettings additionalModules(List<String> additionalModules) {
		this.additionalModules = new ArrayList<>(additionalModules);
		return this;
	}

	/**
	 * Set platform
	 * @param platform Platform
	 * @return Packager settings
	 */
	public PackagerSettings platform(Platform platform) {
		this.platform = platform;
		return this;
	}

	/**
	 * Set ENV path
	 * @param envPath ENV path
	 * @return Packager settings
	 */
	public PackagerSettings envPath(String envPath) {
		this.envPath = envPath;
		return this;
	}

	/**
	 * Set VM arguments
	 * @param vmArgs VM arguments
	 * @return Packager settings
	 */
	public PackagerSettings vmArgs(List<String> vmArgs) {
		this.vmArgs = new ArrayList<>(vmArgs);
		return this;
	}

	/**
	 * Set runnable JAR
	 * @param runnableJar Runnable JAR
	 * @return Packager settings
	 */
	public PackagerSettings runnableJar(File runnableJar) {
		this.runnableJar = runnableJar;
		return this;
	}

	/**
	 * Set copy dependencies
	 * @param copyDependencies Copy dependencies
	 * @return Packager settings
	 */
	public PackagerSettings copyDependencies(Boolean copyDependencies) {
		this.copyDependencies = copyDependencies;
		return this;
	}

	/**
	 * Set JRE directory name
	 * @param jreDirectoryName JRE directory name
	 * @return Packager settings
	 */
	public PackagerSettings jreDirectoryName(String jreDirectoryName) {
		this.jreDirectoryName = jreDirectoryName;
		return this;
	}

	/**
	 * Set Windows specific configuration
	 * @param winConfig Windows specific configuration
	 * @return Packager settings
	 */
	public PackagerSettings winConfig(WindowsConfig winConfig) {
		this.winConfig = winConfig;
		return this;
	}

	/**
	 * Set GNU/Linux specific configuration
	 * @param linuxConfig GNU/Linux specific configuration
	 * @return Packager settings
	 */
	public PackagerSettings linuxConfig(LinuxConfig linuxConfig) {
		this.linuxConfig = linuxConfig;
		return this;
	}

	/**
	 * Set Mac OS specific configuration
	 * @param macConfig Mac OS specific configuration
	 * @return Packager settings
	 */
	public PackagerSettings macConfig(MacConfig macConfig) {
		this.macConfig = macConfig;
		return this;
	}

	/**
	 * Set create tarball
	 * @param createTarball Create tarball
	 * @return Packager settings
	 */
	public PackagerSettings createTarball(Boolean createTarball) {
		this.createTarball = createTarball;
		return this;
	}

	/**
	 * Set create zipball
	 * @param createZipball Create zipball
	 * @return Packager settings
	 */
	public PackagerSettings createZipball(Boolean createZipball) {
		this.createZipball = createZipball;
		return this;
	}

	/**
	 * Set extra parameters map
	 * @param extra Extra parameters map
	 * @return Packager settings
	 */
	public PackagerSettings extra(Map<String, String> extra) {
		this.extra = extra;
		return this;
	}

	/**
	 * Set if it use resources folder as working directory
	 * @param useResourcesAsWorkingDir Use resources folder as working directory
	 * @return Packager settings
	 */
	public PackagerSettings useResourcesAsWorkingDir(boolean useResourcesAsWorkingDir) {
		this.useResourcesAsWorkingDir = useResourcesAsWorkingDir;
		return this;
	}

	/**
	 * Set asstes directory
	 * @param assetsDir Assets directory
	 * @return Packager settings
	 */
	public PackagerSettings assetsDir(File assetsDir) {
		this.assetsDir = assetsDir;
		return this;
	}

	/**
	 * Set classpath
	 * @param classpath Classpath
	 * @return Packager settings
	 */
	public PackagerSettings classpath(String classpath) {
		this.classpath = classpath;
		return this;
	}

	/**
	 * Set minimal JRE version
	 * @param jreMinVersion JRE minimal version
	 * @return Packager settings
	 */
	public PackagerSettings jreMinVersion(String jreMinVersion) {
		this.jreMinVersion = jreMinVersion;
		return this;
	}

	/**
	 * Set Manifest configuration
	 * @param manifest Manifest
	 * @return Packager settings
	 */
	public PackagerSettings manifest(Manifest manifest) {
		this.manifest = manifest;
		return this;
	}

	/**
	 * Set additional module paths
	 * @param additionalModulePaths Additional module path list
	 * @return Packager settings
	 */
	public PackagerSettings additionalModulePaths(List<File> additionalModulePaths) {
		this.additionalModulePaths = additionalModulePaths;
		return this;
	}

	/**
	 * Set file associations
	 * @param fileAssociations File associations list
	 * @return Packager settings
	 */
	public PackagerSettings fileAssociations(List<FileAssociation> fileAssociations) {
		this.fileAssociations = fileAssociations;
		return this;
	}

	/**
	 * Set scripts
	 * @param scripts Scripts
	 * @return Packager settings
	 */
	public PackagerSettings scripts(Scripts scripts) {
		this.scripts = scripts;
		return this;
	}

	// some helpful methods

	/**
	 * Checks if there are file associations specified
	 * @return true if there are file asociations, otherwise false
	 */
	public boolean isThereFileAssociations() {
		return fileAssociations != null && !fileAssociations.isEmpty();
	}

	/**
	 * Mime types list to string
	 * @param separator Character used to join mime types into one string
	 * @return Mime type list string
	 */
	public String getMimeTypesListAsString(String separator) {
		return StringUtils.join(fileAssociations.stream().map(fa -> fa.getMimeType()).collect(Collectors.toList()),
				separator);
	}

	@Override
	public String toString() {
		return "PackagerSettings [outputDirectory=" + outputDirectory + ", licenseFile=" + licenseFile + ", iconFile="
				+ iconFile + ", generateInstaller=" + generateInstaller + ", forceInstaller=" + forceInstaller
				+ ", mainClass=" + mainClass + ", name=" + name + ", displayName=" + displayName + ", version="
				+ version + ", description=" + description + ", url=" + url + ", administratorRequired="
				+ administratorRequired + ", organizationName=" + organizationName + ", organizationUrl="
				+ organizationUrl + ", organizationEmail=" + organizationEmail + ", bundleJre=" + bundleJre
				+ ", customizedJre=" + customizedJre + ", jrePath=" + jrePath + ", jdkPath=" + jdkPath
				+ ", additionalResources=" + additionalResources + ", modules=" + modules + ", additionalModules="
				+ additionalModules + ", platform=" + platform + ", envPath=" + envPath + ", vmArgs=" + vmArgs
				+ ", runnableJar=" + runnableJar + ", copyDependencies=" + copyDependencies + ", jreDirectoryName="
				+ jreDirectoryName + ", winConfig=" + winConfig + ", linuxConfig=" + linuxConfig + ", macConfig="
				+ macConfig + ", createTarball=" + createTarball + ", createZipball=" + createZipball + ", extra="
				+ extra + ", useResourcesAsWorkingDir=" + useResourcesAsWorkingDir + ", assetsDir=" + assetsDir
				+ ", classpath=" + classpath + ", jreMinVersion=" + jreMinVersion + ", manifest=" + manifest
				+ ", additionalModulePaths=" + additionalModulePaths + ", fileAssociations=" + fileAssociations
				+ ", packagingJdk=" + packagingJdk + ", scripts=" + scripts + "]";
	}}
