package io.github.fvarrui.javapackager;

import groovy.lang.Closure;
import io.github.fvarrui.javapackager.gradle.GradleContext;
import io.github.fvarrui.javapackager.gradle.PackagePlugin;
import io.github.fvarrui.javapackager.model.*;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GradlePackageTask extends DefaultTask implements PackagerFactory {
    private final Project gradleProject = Context.getGradleContext().getProject();
    /**
     * This is the global javapackager extension instance. <br>
     * Sadly we need to do it like this because
     * we cannot extend {@link PackageTask} because we already
     * extend another class. This means that this task
     * has no properties, instead we use this extensions properties.
     *
     * @see PackagePlugin
     */
    public PackageTask extension;
    private List<File> outputFiles;

    public GradlePackageTask() {
        setGroup(PackagePlugin.GROUP_NAME);
        setDescription("Packages the application as a native Windows, Mac OS X or GNU/Linux executable and creates an installer");
        getOutputs().upToDateWhen(o -> false);
        updateExtension(PackagePlugin.GLOBAL_EXTENSION);
    }

    public void updateExtension(PackageTask extension) {
        this.extension = extension;
        // Defaults specific to gradle
        this.extension.outputDirectory = gradleProject.getBuildDir();
        this.extension.description = gradleProject.getDescription();
        this.extension.appName = gradleProject.getName();
        this.extension.appDisplayName = gradleProject.getName();
        this.extension.version = gradleProject.getVersion().toString();
        this.extension.description = gradleProject.getDescription();
        this.extension.organizationName = null;
        this.extension.organizationUrl = null;
        this.extension.assetsDir = new File(gradleProject.getProjectDir(), "assets");
    }

    @OutputFiles
    public List<File> getOutputFiles() {
        return outputFiles != null ? outputFiles : new ArrayList<>();
    }

    /**
     * Packaging task action
     *
     * @throws Exception Throwed if something went wrong
     */
    @TaskAction
    public void doPackage() throws Exception {

        Packager packager = this.createPackager(extension);
        // generates app, installers and bundles
        File app = packager.createApp();
        List<File> installers = packager.generateInstallers();
        List<File> bundles = packager.createBundles();

        // sets generated files as output
        outputFiles = new ArrayList<>();
        outputFiles.add(app);
        outputFiles.addAll(installers);
        outputFiles.addAll(bundles);

    }

    public LinuxConfig linuxConfig(Closure<LinuxConfig> closure) {
        extension.linuxConfig = new LinuxConfig();
        GradleContext.getGradleContext().getProject().configure(extension.linuxConfig, closure);
        return extension.linuxConfig;
    }

    public MacConfig macConfig(Closure<MacConfig> closure) {
        extension.macConfig = new MacConfig();
        gradleProject.configure(extension.macConfig, closure);
        return extension.macConfig;
    }

    public WindowsConfig winConfig(Closure<WindowsConfig> closure) {
        extension.winConfig = new WindowsConfig();
        gradleProject.configure(extension.winConfig, closure);
        return extension.winConfig;
    }

    public Manifest manifest(Closure<Manifest> closure) {
        extension.manifest = new Manifest();
        gradleProject.configure(extension.manifest, closure);
        return extension.manifest;
    }

    public Scripts scripts(Closure<Scripts> closure) {
        extension.scripts = new Scripts();
        gradleProject.configure(extension.scripts, closure);
        return extension.scripts;
    }

}
