package io.github.fvarrui.javapackager;

import io.github.fvarrui.javapackager.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Parameter;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Package task that gets detected by maven and gradle.
 */

public class PackageTask {
    /**
     * Output directory.
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}")
    @OutputDirectory
    @Optional
    protected File outputDirectory;
    /**
     * Path to project license file.
     */
    @Parameter(property = "licenseFile")
    @InputFile
    @Optional
    protected File licenseFile;
    /**
     * Path to the app icon file (PNG, ICO or ICNS).
     */
    @Parameter(property = "iconFile")
    @InputFile
    @Optional
    protected File iconFile;
    /**
     * Generates an installer for the app.
     */
    @Parameter(property = "generateInstaller")
    @Input
    @Optional
    protected Boolean generateInstaller;
    /**
     * Forces installer generation.
     */
    @Parameter(property = "forceInstaller")
    @Input
    @Optional
    protected Boolean forceInstaller;
    /**
     * Full path to your app main class.
     */
    @Parameter(property = "mainClass", required = true, defaultValue = "${exec.mainClass}")
    @Input
    @Optional
    protected String mainClass;
    /**
     * App name.
     */
    @Parameter(property = "appName", defaultValue = "${project.name}")
    @Input
    @Optional
    protected String appName;
    /**
     * App name to show.
     */
    @Parameter(property = "appDisplayName", defaultValue = "${project.name}")
    @Input
    @Optional
    protected String appDisplayName;
    /**
     * Project version.
     */
    @Parameter(property = "version", defaultValue = "${project.version}")
    @Input
    @Optional
    protected String version;
    /**
     * Project description.
     */
    @Parameter(property = "description", defaultValue = "${project.description}")
    @Input
    @Optional
    protected String description;
    /**
     * App website URL.
     */
    @Parameter(property = "url", defaultValue = "${project.url}")
    @Input
    @Optional
    protected String url;
    /**
     * App will run as administrator (with elevated privileges).
     */
    @Parameter(property = "administratorRequired")
    @Input
    @Optional
    protected Boolean administratorRequired;
    /**
     * Organization name.
     */
    @Parameter(property = "organizationName", defaultValue = "${project.organization.name}")
    @Input
    @Optional
    protected String organizationName;
    /**
     * Organization website URL.
     */
    @Parameter(property = "organizationUrl", defaultValue = "${project.organization.url}")
    @Input
    @Optional
    protected String organizationUrl;
    /**
     * Organization email.
     */
    @Parameter(property = "organizationEmail", required = false)
    @Input
    @Optional
    protected String organizationEmail;
    /**
     * Embeds a customized JRE with the app.
     */
    @Parameter(property = "bundleJre", required = false)
    @Input
    @Optional
    protected Boolean bundleJre;
    /**
     * Generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included.
     */
    @Parameter(property = "customizedJre", required = false)
    @Input
    @Optional
    protected Boolean customizedJre;
    /**
     * Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least.
     */
    @Parameter(property = "jrePath", required = false)
    @InputDirectory
    @Optional
    protected File jrePath;
    /**
     * Path to JDK folder. If specified, it will use this JDK modules to generate a customized JRE. Allows generating JREs for different platforms.
     */
    @Parameter(property = "jdkPath", required = false)
    @InputDirectory
    @Optional
    protected File jdkPath;
    /**
     * The JDK version. Supported versions differ from vendor to vendor, thus its recommended checking the vendors' website first before doing any changes.
     */
    @Parameter(property = "jdkVersion", required = false)
    @Input
    @Optional
    protected String jdkVersion = "8";
    /**
     * The JDK vendor.
     */
    @Parameter(property = "jdkVendor", required = false)
    @Input
    @Optional
    protected String jdkVendor = "adoptium";
    /**
     * Additional files and folders to include in the bundled app.
     */
    @Parameter(property = "additionalResources", required = false)
    @Input
    @Optional
    protected List<File> additionalResources;
    /**
     * Defines modules to customize the bundled JRE. Don't use jdeps to get module dependencies.
     */
    @Parameter(property = "modules", required = false)
    @Input
    @Optional
    protected List<String> modules;
    /**
     * Additional modules to the ones identified by jdeps or the specified with modules property.
     */
    @Parameter(property = "additionalModules", required = false)
    @Input
    @Optional
    protected List<String> additionalModules;
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
    @Parameter(property = "platform", required = true)
    @Input
    @Optional
    protected Platform platform;
    /**
     * Defines PATH environment variable in GNU/Linux and Mac OS X startup scripts.
     */
    @Parameter(property = "envPath", required = false)
    @Input
    @Optional
    protected String envPath;
    /**
     * Additional arguments to provide to the JVM (for example <tt>-Xmx2G</tt>).
     */
    @Parameter(property = "vmArgs", required = false)
    @Input
    @Optional
    protected List<String> vmArgs;
    /**
     * Provide your own runnable .jar (for example, a shaded .jar) instead of letting this plugin create one via
     * the <tt>maven-jar-plugin</tt>.
     */
    @Parameter(property = "runnableJar", required = false)
    @InputFile
    @Optional
    protected File runnableJar;
    /**
     * Whether or not to copy dependencies into the bundle. Generally, you will only disable this if you specified
     * a <tt>runnableJar</tt> with all dependencies shaded into the .jar itself.
     */
    @Parameter(property = "copyDependencies", required = true)
    @Input
    @Optional
    protected Boolean copyDependencies;
    /**
     * Bundled JRE directory name
     */
    @Parameter(property = "jreDirectoryName", required = false)
    @Input
    @Optional
    protected String jreDirectoryName;
    /**
     * Windows specific config
     */
    @Parameter(property = "winConfig", required = false)
    @Input
    @Optional
    protected WindowsConfig winConfig;
    /**
     * GNU/Linux specific config
     */
    @Parameter(property = "linuxConfig", required = false)
    @Input
    @Optional
    protected LinuxConfig linuxConfig;
    /**
     * Mac OS X specific config
     */
    @Parameter(property = "macConfig", required = false)
    @Input
    @Optional
    protected MacConfig macConfig;
    /**
     * Bundles app in a tarball file
     */
    @Parameter(property = "createTarball", required = false)
    @Input
    @Optional
    protected Boolean createTarball;
    /**
     * Bundles app in a zipball file
     */
    @Parameter(property = "createZipball", required = false)
    @Input
    @Optional
    protected Boolean createZipball;
    /**
     * Extra properties for customized Velocity templates, accesible through '$this.extra' map.
     */
    @Parameter(required = false)
    @Input
    @Optional
    protected Map<String, String> extra;
    /**
     * Uses app resources folder as default working directory.
     */
    @Parameter(property = "useResourcesAsWorkingDir", required = false)
    @Input
    @Optional
    protected Boolean useResourcesAsWorkingDir;
    /**
     * Assets directory
     */
    @Parameter(property = "assetsDir", defaultValue = "${project.basedir}/assets")
    @InputDirectory
    @Optional
    protected File assetsDir;
    /**
     * Classpath
     */
    @Parameter(property = "classpath", required = false)
    @Input
    @Optional
    protected String classpath;
    /**
     * JRE min version
     */
    @Parameter(property = "jreMinVersion", required = false)
    @Input
    @Optional
    protected String jreMinVersion;
    /**
     * Additional JAR manifest entries
     */
    @Parameter(required = false)
    @Input
    @Optional
    protected Manifest manifest;
    /**
     * Additional module paths
     */
    @Parameter(property = "additionalModulePaths", required = false)
    @Input
    @Optional
    protected List<File> additionalModulePaths;
    /**
     * Additional module paths
     */
    @Parameter(property = "fileAssociations", required = false)
    @Input
    @Optional
    protected List<FileAssociation> fileAssociations;
    /**
     * Packaging JDK
     */
    @Parameter(property = "packagingJdk", required = false)
    @InputDirectory
    @Optional
    protected File packagingJdk;
    /**
     * Scripts
     */
    @Parameter(property = "scripts", required = false)
    @Input
    @Optional
    protected Scripts scripts;

    public PackageTask() {
        //this.outputDirectory = (isGradle ? gradleProject.getBuildDir() : new File("${project.build.directory}"));
        this.platform = Platform.getCurrentPlatform();
        this.bundleJre = true;
        this.copyDependencies = true;
        this.createTarball = false;
        this.createZipball = false;
        //this.description = gradleProject.getDescription(); // TODO maven?
        this.generateInstaller = true;
        this.linuxConfig = new LinuxConfig();
        this.macConfig = new MacConfig();
        this.manifest = new Manifest();
        this.modules = new ArrayList<>();
        this.forceInstaller = false;
        this.mainClass = "${exec.mainClass}"; //TODO gradle?
        //this.appName = (isGradle ? gradleProject.getName() : "${project.name}");
        //this.appDisplayName = (isGradle ? gradleProject.getName() : "${project.name}");
        //this.version = (isGradle ? (String) gradleProject.getVersion() : "${project.version}");
        //this.description = (isGradle ? gradleProject.getDescription(): "${project.description}");
        this.url = "${project.url}"; //TODO gradle?
        this.administratorRequired = false;
        //this.organizationName = (isGradle ? null : "${project.organization.name}");
        //this.organizationUrl = (isGradle ? null : "${project.organization.url}");
        this.organizationEmail = "";
        this.bundleJre = false;
        this.customizedJre = true;
        this.jrePath = null;
        this.jdkPath = null;
        this.jdkVersion = "17";
        this.jdkVendor = "adoptium";
        this.additionalResources = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.additionalModules = new ArrayList<>();
        this.envPath = null;
        this.vmArgs = new ArrayList<>();
        this.runnableJar = null;
        this.copyDependencies = true;
        this.jreDirectoryName = "jre";
        this.winConfig = new WindowsConfig();
        this.linuxConfig = new LinuxConfig();
        this.macConfig = new MacConfig();
        this.createTarball = false;
        this.createZipball = false;
        this.extra = new HashMap<>();
        this.useResourcesAsWorkingDir = true;
        //this.assetsDir = (isGradle ? new File(gradleProject.getProjectDir(), "assets") : new File("${project.basedir}/assets"));
        this.classpath = null;
        this.jreMinVersion = null;
        this.manifest = new Manifest();
        this.additionalModulePaths = new ArrayList<>();
        this.fileAssociations = null;
        this.packagingJdk = null;
        scripts = new Scripts();
    }

    /**
     * Get packaging JDK
     *
     * @return Packaging JDK
     */
    public File getPackagingJdk() {
        return packagingJdk;
    }

    /**
     * Get output directory
     *
     * @return Output directory
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Get license file
     *
     * @return License file
     */
    public File getLicenseFile() {
        return licenseFile;
    }

    /**
     * Get icon file
     *
     * @return Icon file
     */
    public File getIconFile() {
        return iconFile;
    }

    /**
     * Get generate installer
     *
     * @return Generate installer
     */
    public Boolean getGenerateInstaller() {
        return generateInstaller;
    }

    /**
     * Get force installer
     *
     * @return Force installer
     */
    public Boolean isForceInstaller() {
        return forceInstaller;
    }

    /**
     * Get main class
     *
     * @return Main class
     */
    public String getMainClass() {
        return mainClass;
    }

    /**
     * Get app name
     *
     * @return App name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Get display name
     *
     * @return Display name
     */
    public String getAppDisplayName() {
        return appDisplayName;
    }

    /**
     * Get version
     *
     * @return Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get description
     *
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get URL
     *
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get administrator required
     *
     * @return Administrator required
     */
    public Boolean getAdministratorRequired() {
        return administratorRequired;
    }

    /**
     * Get organization name
     *
     * @return Organization name
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Get organization URL
     *
     * @return Organization URL
     */
    public String getOrganizationUrl() {
        return organizationUrl;
    }

    /**
     * Get organization email
     *
     * @return Organization email
     */
    public String getOrganizationEmail() {
        return organizationEmail;
    }

    /**
     * Get bundle JRE
     *
     * @return Bundle JRE
     */
    public Boolean getBundleJre() {
        return bundleJre;
    }

    /**
     * Get customized JRE
     *
     * @return Customized JRE
     */
    public Boolean getCustomizedJre() {
        return customizedJre;
    }

    /**
     * Get JRE path
     *
     * @return JRE path
     */
    public File getJrePath() {
        return jrePath;
    }

    /**
     * Get JDK path
     *
     * @return JDK path
     */
    public File getJdkPath() {
        return jdkPath;
    }

    /**
     * Get JDK version
     *
     * @return JDK version
     */
    public String getJdkVersion() {
        return jdkVersion;
    }

    /**
     * Get JDK vendor
     *
     * @return JDK vendor
     */
    public String getJdkVendor() {
        return jdkVendor;
    }

    /**
     * Get additional resourcxes
     *
     * @return Additional resources
     */
    public List<File> getAdditionalResources() {
        return additionalResources;
    }

    /**
     * Get Modules
     *
     * @return Modules
     */
    public List<String> getModules() {
        return modules;
    }

    /**
     * Get additional modules
     *
     * @return Additional modules
     */
    public List<String> getAdditionalModules() {
        return additionalModules;
    }

    /**
     * Get platform
     *
     * @return Platform
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * Get env path
     *
     * @return Env path
     */
    public String getEnvPath() {
        return envPath;
    }

    /**
     * Get VM args
     *
     * @return VM args
     */
    public List<String> getVmArgs() {
        return vmArgs;
    }

    /**
     * Get runnable JAR
     *
     * @return Runnable JAR
     */
    public File getRunnableJar() {
        return runnableJar;
    }

    /**
     * Get copy dependencies
     *
     * @return Copy dependencies
     */
    public Boolean getCopyDependencies() {
        return copyDependencies;
    }

    /**
     * Get JRE directory name
     *
     * @return JRE directory name
     */
    public String getJreDirectoryName() {
        return jreDirectoryName;
    }

    /**
     * Get Windows config
     *
     * @return Windows config
     */
    public WindowsConfig getWinConfig() {
        return winConfig;
    }

    /**
     * Get Linux config
     *
     * @return Linux config
     */
    public LinuxConfig getLinuxConfig() {
        return linuxConfig;
    }

    /**
     * Get Mac OS config
     *
     * @return Mac OS config
     */
    public MacConfig getMacConfig() {
        return macConfig;
    }

    /**
     * Get create tarball
     *
     * @return Create tarball
     */
    public Boolean getCreateTarball() {
        return createTarball;
    }

    /**
     * Get create zipball
     *
     * @return Create zipball
     */
    public Boolean getCreateZipball() {
        return createZipball;
    }

    /**
     * Get extra parameters
     *
     * @return Extra parameters
     */
    public Map<String, String> getExtra() {
        return extra;
    }

    /**
     * Get if it has to use resources folder as working directory
     *
     * @return Use resources folder as working directory
     */
    public Boolean isUseResourcesAsWorkingDir() {
        return useResourcesAsWorkingDir;
    }

    /**
     * Get assets dir
     *
     * @return Assets dir
     */
    public File getAssetsDir() {
        return assetsDir;
    }

    /**
     * Get classpath
     *
     * @return Classpath
     */
    public String getClasspath() {
        return classpath;
    }

    /**
     * Get JRE min version
     *
     * @return JRE min version
     */
    public String getJreMinVersion() {
        return jreMinVersion;
    }

    /**
     * Get Manifest
     *
     * @return manifest
     */
    public Manifest getManifest() {
        return manifest;
    }

    /**
     * Get additional modules paths
     *
     * @return Additional module paths
     */
    public List<File> getAdditionalModulePaths() {
        return additionalModulePaths;
    }

    /**
     * Get file associations
     *
     * @return File associations
     */
    public List<FileAssociation> getFileAssociations() {
        return fileAssociations;
    }

    /**
     * Get scripts
     *
     * @return Scripts
     */
    public Scripts getScripts() {
        return scripts;
    }

    // fluent api

    /**
     * Set output directory
     *
     * @param outputDirectory Output directory
     * @return Packager settings
     */
    public PackageTask outputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        return this;
    }

    /**
     * Set packaging JDK
     *
     * @param packagingJdk Packaging JDK
     * @return Packager settings
     */
    public PackageTask packagingJdk(File packagingJdk) {
        this.packagingJdk = packagingJdk;
        return this;
    }

    /**
     * Set license file
     *
     * @param licenseFile License file
     * @return Packager settings
     */
    public PackageTask licenseFile(File licenseFile) {
        this.licenseFile = licenseFile;
        return this;
    }

    /**
     * Set icon file
     *
     * @param iconFile Icon file
     * @return Packager settings
     */
    public PackageTask iconFile(File iconFile) {
        this.iconFile = iconFile;
        return this;
    }

    /**
     * Set generate installer
     *
     * @param generateInstaller Generate installer
     * @return Packager settings
     */
    public PackageTask generateInstaller(Boolean generateInstaller) {
        this.generateInstaller = generateInstaller;
        return this;
    }

    /**
     * Set force installer
     *
     * @param forceInstaller Force installer
     * @return Packager settings
     */
    public PackageTask forceInstaller(Boolean forceInstaller) {
        this.forceInstaller = forceInstaller;
        return this;
    }

    /**
     * Set main class
     *
     * @param mainClass Main class
     * @return Packager settings
     */
    public PackageTask mainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    /**
     * Set name
     *
     * @param appName Name
     * @return Packager settings
     */
    public PackageTask appName(String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * Set display name
     *
     * @param appDisplayName Display name
     * @return Packager settings
     */
    public PackageTask appDisplayName(String appDisplayName) {
        this.appDisplayName = appDisplayName;
        return this;
    }

    /**
     * Set version
     *
     * @param version Version
     * @return Packager settings
     */
    public PackageTask version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set description
     *
     * @param description Description
     * @return Packager settings
     */
    public PackageTask description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set URL
     *
     * @param url URL
     * @return Packager settings
     */
    public PackageTask url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Set administrator required
     *
     * @param administratorRequired Administrator required
     * @return Packager settings
     */
    public PackageTask administratorRequired(Boolean administratorRequired) {
        this.administratorRequired = administratorRequired;
        return this;
    }

    /**
     * Set organizstion name
     *
     * @param organizationName Organization name
     * @return Packager settings
     */
    public PackageTask organizationName(String organizationName) {
        this.organizationName = organizationName;
        return this;
    }

    /**
     * Set organization URL
     *
     * @param organizationUrl Organization URL
     * @return Packager settings
     */
    public PackageTask organizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
        return this;
    }

    /**
     * Set organization email
     *
     * @param organizationEmail
     * @return Packager settings
     */
    public PackageTask organizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
        return this;
    }

    /**
     * Set bundle JRE
     *
     * @param bundleJre Bundle JRE
     * @return Packager settings
     */
    public PackageTask bundleJre(Boolean bundleJre) {
        this.bundleJre = bundleJre;
        return this;
    }

    /**
     * Set customized JRE
     *
     * @param customizedJre Customized JRE
     * @return Packager settings
     */
    public PackageTask customizedJre(Boolean customizedJre) {
        this.customizedJre = customizedJre;
        return this;
    }

    /**
     * Set JRE path
     *
     * @param jrePath JRE path
     * @return Packager settings
     */
    public PackageTask jrePath(File jrePath) {
        this.jrePath = jrePath;
        return this;
    }

    /**
     * Set JDK path
     *
     * @param jdkPath JDK path
     * @return Packager settings
     */
    public PackageTask jdkPath(File jdkPath) {
        this.jdkPath = jdkPath;
        return this;
    }

    /**
     * Set JDK version
     *
     * @param jdkVersion JDK version
     * @return Packager settings
     */
    public PackageTask jdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
        return this;
    }

    /**
     * Set JDK vendor
     *
     * @param jdkVendor JDK vendor
     * @return Packager settings
     */
    public PackageTask jdkVendor(String jdkVendor) {
        this.jdkVendor = jdkVendor;
        return this;
    }

    /**
     * Set additional resources list
     *
     * @param additionalResources Additional resources list
     * @return Packager settings
     */
    public PackageTask additionalResources(List<File> additionalResources) {
        this.additionalResources = new ArrayList<>(additionalResources);
        return this;
    }

    /**
     * Set modules list
     *
     * @param modules Modules list
     * @return Packager settings
     */
    public PackageTask modules(List<String> modules) {
        this.modules = new ArrayList<>(modules);
        return this;
    }

    /**
     * Set additional modules list
     *
     * @param additionalModules Additional modules list
     * @return Packager settings
     */
    public PackageTask additionalModules(List<String> additionalModules) {
        this.additionalModules = new ArrayList<>(additionalModules);
        return this;
    }

    /**
     * Set platform
     *
     * @param platform Platform
     * @return Packager settings
     */
    public PackageTask platform(Platform platform) {
        this.platform = platform;
        return this;
    }

    /**
     * Set ENV path
     *
     * @param envPath ENV path
     * @return Packager settings
     */
    public PackageTask envPath(String envPath) {
        this.envPath = envPath;
        return this;
    }

    /**
     * Set VM arguments
     *
     * @param vmArgs VM arguments
     * @return Packager settings
     */
    public PackageTask vmArgs(List<String> vmArgs) {
        this.vmArgs = new ArrayList<>(vmArgs);
        return this;
    }

    /**
     * Set runnable JAR
     *
     * @param runnableJar Runnable JAR
     * @return Packager settings
     */
    public PackageTask runnableJar(File runnableJar) {
        this.runnableJar = runnableJar;
        return this;
    }

    /**
     * Set copy dependencies
     *
     * @param copyDependencies Copy dependencies
     * @return Packager settings
     */
    public PackageTask copyDependencies(Boolean copyDependencies) {
        this.copyDependencies = copyDependencies;
        return this;
    }

    /**
     * Set JRE directory name
     *
     * @param jreDirectoryName JRE directory name
     * @return Packager settings
     */
    public PackageTask jreDirectoryName(String jreDirectoryName) {
        this.jreDirectoryName = jreDirectoryName;
        return this;
    }

    /**
     * Set Windows specific configuration
     *
     * @param winConfig Windows specific configuration
     * @return Packager settings
     */
    public PackageTask winConfig(WindowsConfig winConfig) {
        this.winConfig = winConfig;
        return this;
    }

    /**
     * Set GNU/Linux specific configuration
     *
     * @param linuxConfig GNU/Linux specific configuration
     * @return Packager settings
     */
    public PackageTask linuxConfig(LinuxConfig linuxConfig) {
        this.linuxConfig = linuxConfig;
        return this;
    }

    /**
     * Set Mac OS specific configuration
     *
     * @param macConfig Mac OS specific configuration
     * @return Packager settings
     */
    public PackageTask macConfig(MacConfig macConfig) {
        this.macConfig = macConfig;
        return this;
    }

    /**
     * Set create tarball
     *
     * @param createTarball Create tarball
     * @return Packager settings
     */
    public PackageTask createTarball(Boolean createTarball) {
        this.createTarball = createTarball;
        return this;
    }

    /**
     * Set create zipball
     *
     * @param createZipball Create zipball
     * @return Packager settings
     */
    public PackageTask createZipball(Boolean createZipball) {
        this.createZipball = createZipball;
        return this;
    }

    /**
     * Set extra parameters map
     *
     * @param extra Extra parameters map
     * @return Packager settings
     */
    public PackageTask extra(Map<String, String> extra) {
        this.extra = extra;
        return this;
    }

    /**
     * Set if it use resources folder as working directory
     *
     * @param useResourcesAsWorkingDir Use resources folder as working directory
     * @return Packager settings
     */
    public PackageTask useResourcesAsWorkingDir(boolean useResourcesAsWorkingDir) {
        this.useResourcesAsWorkingDir = useResourcesAsWorkingDir;
        return this;
    }

    /**
     * Set asstes directory
     *
     * @param assetsDir Assets directory
     * @return Packager settings
     */
    public PackageTask assetsDir(File assetsDir) {
        this.assetsDir = assetsDir;
        return this;
    }

    /**
     * Set classpath
     *
     * @param classpath Classpath
     * @return Packager settings
     */
    public PackageTask classpath(String classpath) {
        this.classpath = classpath;
        return this;
    }

    /**
     * Set minimal JRE version
     *
     * @param jreMinVersion JRE minimal version
     * @return Packager settings
     */
    public PackageTask jreMinVersion(String jreMinVersion) {
        this.jreMinVersion = jreMinVersion;
        return this;
    }

    /**
     * Set Manifest configuration
     *
     * @param manifest Manifest
     * @return Packager settings
     */
    public PackageTask manifest(Manifest manifest) {
        this.manifest = manifest;
        return this;
    }

    /**
     * Set additional module paths
     *
     * @param additionalModulePaths Additional module path list
     * @return Packager settings
     */
    public PackageTask additionalModulePaths(List<File> additionalModulePaths) {
        this.additionalModulePaths = additionalModulePaths;
        return this;
    }

    /**
     * Set file associations
     *
     * @param fileAssociations File associations list
     * @return Packager settings
     */
    public PackageTask fileAssociations(List<FileAssociation> fileAssociations) {
        this.fileAssociations = fileAssociations;
        return this;
    }

    /**
     * Set scripts
     *
     * @param scripts Scripts
     * @return Packager settings
     */
    public PackageTask scripts(Scripts scripts) {
        this.scripts = scripts;
        return this;
    }

    // some helpful methods

    /**
     * Checks if there are file associations specified
     *
     * @return true if there are file asociations, otherwise false
     */
    public boolean isThereFileAssociations() {
        return fileAssociations != null && !fileAssociations.isEmpty();
    }

    /**
     * Mime types list to string
     *
     * @param separator Character used to join mime types into one string
     * @return Mime type list string
     */
    public String getMimeTypesListAsString(String separator) {
        return StringUtils.join(fileAssociations.stream().map(fa -> fa.getMimeType()).collect(Collectors.toList()),
                separator);
    }

    @Override
    public String toString() {
        return "PackageTask [outputDirectory=" + outputDirectory + ", licenseFile=" + licenseFile + ", iconFile="
                + iconFile + ", generateInstaller=" + generateInstaller + ", forceInstaller=" + forceInstaller
                + ", mainClass=" + mainClass + ", name=" + appName + ", displayName=" + appDisplayName + ", version="
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
    }
}