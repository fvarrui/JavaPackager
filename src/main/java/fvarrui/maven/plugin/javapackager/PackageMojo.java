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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import org.apache.maven.project.MavenProjectHelper;
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

    // plugin configuration properties
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.directory}/${project.name}-${project.version}.jar", property = "jarFile", required = true)
    private File jarFile;

    @Parameter(defaultValue = "${project.build.directory}/../src/main/resources/LICENSE.txt", property = "licenseFile", required = false)
    private File licenseFile;

    @Parameter(defaultValue = "${project.build.directory}/app/${project.name}", property = "executable", required = true)
    private File executable;

    @Parameter(defaultValue = "${project.build.directory}/../src/main/resources/${project.name}.icns", property = "iconFile")
    private File iconFile;

    @Parameter(defaultValue = "${java.version}", property = "jreMinVersion", required = true)
    private String jreMinVersion;

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

    @Parameter(defaultValue = "true", property = "bundleJre", required = true)
    private Boolean bundleJre;

    public PackageMojo() {
        super();
        Logger.init(getLog());
    }

    public void execute() throws MojoExecutionException {

        appFolder = new File(outputDirectory + "/app");
        assetsFolder = new File(outputDirectory + "/assets");

        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        if (!assetsFolder.exists()) {
            assetsFolder.mkdirs();
        }

        // if default license file doesn't exist and there's a license specified in pom.xml file, get this last one
        if (!licenseFile.exists() && !mavenProject.getLicenses().isEmpty()) {
            licenseFile = new File(mavenProject.getLicenses().get(0).getUrl());
        }

        // copy license file to app folder
        if (licenseFile.exists()) {
            FileUtils.copyFileToFolder(licenseFile, appFolder);
        }

        this.info = getInfo();

        this.env = executionEnvironment(mavenProject, mavenSession, pluginManager);

        copyAllDependencies();

        createCustomizedJre();

        if (SystemUtils.IS_OS_MAC_OSX) {

            if (!iconFile.exists()) {
                iconFile = new File("assets/mac/", mavenProject.getName() + ".icns");
            }

            createMacAppBundle();

        } else if (SystemUtils.IS_OS_LINUX) {

            if (iconFile == null) {
                iconFile = new File("assets/linux", mavenProject.getName() + ".png");
            }

            FileUtils.copyFileToFolder(iconFile, appFolder);

            createLinuxExecutable();
            generateDebPackage();
            generateRpmPackage();

        } else if (SystemUtils.IS_OS_WINDOWS) {

            if (iconFile == null) {
                iconFile = new File("assets/windows", mavenProject.getName() + ".ico");
            }

            createWindowsExecutable();
            generateWindowsInstaller();

        } else {

            throw new MojoExecutionException("Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);

        }

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

        // if license file exists
        if (licenseFile != null) {
            info.put("license", licenseFile.getAbsolutePath());
        }

        return info;
    }

    private void generateRpmPackage() throws MojoExecutionException {
        getLog().info("Generate RPM package...");

        String name = (String) info.get("name");
        String version = (String) info.get("version");

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

        File packageFolder = new File(assetsFolder, name + "-" + version);
        File specFile = new File(packageFolder, name + "-" + version + "-2.spec");

        try {
            // rebuild rpm package
            ProcessUtils.execute(assetsFolder, "rpmbuild", "--buildroot", packageFolder, "--nodeps", "-bb", specFile);
        } catch (MojoExecutionException e) {
            getLog().warn("rpmbuild command execution failed", e);
            return;
        }

        // rename generated rpm package
        File rpmFile = new File(assetsFolder, name + "-" + version + "-2.x86_64.rpm");
        rpmFile.renameTo(new File(assetsFolder, name + "_" + version + ".rpm"));

    }

    private void createMacAppBundle() throws MojoExecutionException {
        getLog().info("Creating Mac OS X app bundle...");

        String name = (String) info.get("name");

        // 1. create and set up directories
        getLog().info("-----> Creating and setting up the bundle directories");

        File contentsFolder = new File(appFolder, "Contents");
        contentsFolder.mkdirs();

        File resourcesFolder = new File(contentsFolder, "Resources");
        resourcesFolder.mkdirs();

        File javaFolder = new File(resourcesFolder, "Java");
        javaFolder.mkdirs();

        File macOSFolder = new File(contentsFolder, "MacOS");
        macOSFolder.mkdirs();

        // 2. create startup file to boot java app
        getLog().info("-----> Creating startup file");
        File startupFile = new File(macOSFolder, "startup");
        VelocityUtils.render("mac/startup.vtl", startupFile, info);

        //FileUtils.copyStreamToFile(getClass().getResourceAsStream("/mac/launcher"), startupFile);
        startupFile.setExecutable(true, false);

        // 3. copy icon file to resources folder if specified
        getLog().info("-----> Copying icon file to Resources folder");
        FileUtils.copyFileToFolder(iconFile.getAbsoluteFile(), resourcesFolder);

        // 4. move all dependencies from the pom to Java folder
        getLog().info("-----> Moving dependencies to Java folder");
        File libsFolder = new File(appFolder, "libs");
        FileUtils.moveFolderContentToFolder(libsFolder, javaFolder);
        FileUtils.moveFileToFolder(jarFile, javaFolder);
        libsFolder.delete();

        // 5. check if JRE should be embedded. Move generated JRE inside
        if (bundleJre) {

            getLog().info("-----> Bundling JRE");

            File pluginsFolder = new File(contentsFolder, "PlugIns/jre/Contents/Home");
            pluginsFolder.mkdirs();

            File jreFolder = new File(appFolder, "jre");

            getLog().info("Moving the JRE Folder from : [" + jreFolder + "] to PlugIn folder: [" + pluginsFolder + "]");

            FileUtils.moveFolderContentToFolder(jreFolder, pluginsFolder);
            jreFolder.delete();

            // set execution permissions on executables in jre
            File binFolder = new File(pluginsFolder, "bin");
            Arrays.asList(binFolder.listFiles()).forEach(f -> f.setExecutable(true, false));

        }

        // 6. create and write the Info.plist file
        getLog().info("Writing the Info.plist file");
        File infoPlistFile = new File(contentsFolder, "Info.plist");
        VelocityUtils.render("mac/Info.plist.vtl", infoPlistFile, info);

        // 7. Copy specified additional resources into the top level directory
        getLog().info("Copying additional resources");
        if (licenseFile != null) {
            FileUtils.moveFileToFolder(new File(appFolder, licenseFile.getName()), resourcesFolder);
        }

        // 8. Create "<projectname>.app" in app folder, and move content inside
        File appFile = new File(outputDirectory, name + ".app");
        appFile.mkdirs();
        FileUtils.moveFolderToFolder(contentsFolder, appFile);
        FileUtils.moveFolderToFolder(appFile, appFolder);

        // FIXME waiting confirmation about if it really works or not
        appFile = new File(appFolder, appFile.getName());
        ProcessUtils.execute("codesign", "--force", "--deep", "--sign", "-", appFile);

        // 9. Create a symlink to Applications folder
        File targetFolder = new File("/Applications");
        File linkFile = new File(appFolder, "Applications");
        FileUtils.createSymlink(linkFile, targetFolder);

        // 10. Create the DMG file including app folder content
        getLog().info("Generating the Disk Image file");
        File diskImageFile = new File(outputDirectory, name + "-" + version + ".dmg");
        ProcessUtils.execute("hdiutil", "create", "-srcfolder", appFolder, "-volname", name, diskImageFile);

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

    }

    private void createWindowsExecutable() throws MojoExecutionException {
        getLog().info("Creating Windows executable...");

        String name = (String) info.get("name");

        // generate manifest file to require administrator privileges from velocity template
        File manifestFile = new File(assetsFolder, name + ".exe.manifest");
        VelocityUtils.render("windows/exe.manifest.vtl", manifestFile, info);

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
                        element(name("manifest"), manifestFile.getAbsolutePath()),
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

        String name = (String) info.get("name");
        String version = (String) info.get("version");

        // copy ico file to assets folder
        FileUtils.copyFileToFile(iconFile, new File(assetsFolder, name + ".ico"));

        // generate iss file from velocity template
        File issFile = new File(assetsFolder, name + ".iss");
        VelocityUtils.render("windows/iss.vtl", issFile, info);

        // generate windows installer with inno setup command line compiler
        ProcessUtils.execute("iscc", "/O" + outputDirectory.getAbsolutePath(), "/F" + name + "_" + version, issFile);
    }

    private void generateDebPackage() throws MojoExecutionException {
        getLog().info("Generating DEB package ...");

        String name = (String) info.get("name");
        String version = (String) info.get("version");

        // generate desktop file from velocity template
        File desktopFile = new File(assetsFolder, name + ".desktop");
        VelocityUtils.render("linux/desktop.vtl", desktopFile, info);

        // generate policy file from velocity template
//		File policyFile = new File(assetsFolder, name + ".policy");
//		VelocityUtils.render("linux/policy.vtl", policyFile, info);
        // generate deb control file from velocity template
        File controlFile = new File(assetsFolder, "control");
        VelocityUtils.render("linux/control.vtl", controlFile, info);

        debFile = new File(outputDirectory.getAbsolutePath(), name + "_" + version + ".deb");

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
                                //							element(name("data"), 
                                //									element(name("type"), "file"),
                                //									element(name("src"), policyFile.getAbsolutePath()),
                                //									element(name("mapper"), 
                                //											element(name("type"), "perm"),
                                //											element(name("prefix"), "/usr/share/polkit-1/actions")
                                //											)
                                //									),
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

    /**
     * Create a customized Java Runtime Enrironment from the current JDK using
     * jdeps and jlink tools.
     *
     * Next link explains the process:
     * {@link https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4}
     *
     * @throws MojoExecutionException
     */
    private void createCustomizedJre() throws MojoExecutionException {
        if (!bundleJre) {
            return;
        }
        
        if (JavaUtils.getJavaMajorVersion() <= 12) {
            getLog().warn("We need JDK 12+ for correctly generating the dependencies. You run " + System.getProperty("java.home"));
            getLog().warn("Try to build without JRE embeded.");
            return;
        }

        getLog().info("Create customized JRE ... with " + System.getProperty("java.home"));

        File libsFolder = new File(appFolder, "libs");
        File jreFolder = new File(appFolder, "jre");

        // determine required modules for libs and app jar
        String modules = "java.scripting,jdk.unsupported,"; // add required modules by default
        modules += ProcessUtils.execute(System.getProperty("java.home") + "/bin/jdeps", "-q", "--ignore-missing-deps", "--print-module-deps", "--class-path", new File(libsFolder, "*"), jarFile);

        // if exists, remove old jre folder
        if (jreFolder.exists()) {
            FileUtils.removeFolder(jreFolder);
        }

        // generate customized jre using modules
        File modulesDir = new File(System.getProperty("java.home"), "jmods");
        ProcessUtils.execute(System.getProperty("java.home") + "/bin/jlink", "--module-path", modulesDir, "--add-modules", modules, "--output", jreFolder, "--no-header-files", "--no-man-pages", "--strip-debug", "--compress=2");

    }

}
