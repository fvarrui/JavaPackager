package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.gradle.api.Project;

import groovy.lang.Closure;
import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.Manifest;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.PackagerSettings;

public class PackagePluginExtension extends PackagerSettings {

	private Project project;
	
	public PackagePluginExtension(Project project) {
		super();
		this.project = project;
		this.platform = Platform.auto;
		this.additionalModules = new ArrayList<>();
		this.additionalResources = new ArrayList<>();
		this.administratorRequired = false;
		this.assetsDir = new File(project.getProjectDir(), "assets");
		this.bundleJre = true;
		this.copyDependencies = true;
		this.createTarball = false;
		this.createZipball = false;
		this.customizedJre = true;
		this.description = project.getDescription();
		this.extra = new HashMap<>();
		this.generateInstaller = true;
		this.jreDirectoryName = "jre";
		this.linuxConfig = new LinuxConfig();
		this.macConfig = new MacConfig();
		this.manifest = new Manifest();
		this.modules = new ArrayList<>();
		this.name = project.getName();
		this.organizationEmail = "";
		this.useResourcesAsWorkingDir = true;
		this.vmArgs = new ArrayList<>();
		this.winConfig = new WindowsConfig();
		this.outputDirectory = project.getBuildDir();		
	}
	
    public LinuxConfig linuxConfig(Closure<LinuxConfig> closure) {
        linuxConfig = new LinuxConfig();
        project.configure(linuxConfig, closure);
        return linuxConfig;
    }

    public MacConfig macConfig(Closure<MacConfig> closure) {
        macConfig = new MacConfig();
        project.configure(macConfig, closure);
        return macConfig;
    }

    public WindowsConfig winConfig(Closure<WindowsConfig> closure) {
        winConfig = new WindowsConfig();
        project.configure(winConfig, closure);
        return winConfig;
    }
    
    public Manifest manifest(Closure<Manifest> closure) {
        manifest = new Manifest();
        project.configure(manifest, closure);
        return manifest;
    }

}