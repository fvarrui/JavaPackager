package io.github.fvarrui.javapackager.gradle;

import org.gradle.api.Project;
import org.gradle.internal.impldep.org.eclipse.jgit.annotations.NonNull;
import org.gradle.internal.reflect.Instantiator;

import groovy.lang.Closure;
import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.PackagerSettings;

public class PackagePluginExtension extends PackagerSettings {

	private Project project;
	
	public PackagePluginExtension(@NonNull Instantiator instantiator, Project project) {
		this.project = project;
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

}
