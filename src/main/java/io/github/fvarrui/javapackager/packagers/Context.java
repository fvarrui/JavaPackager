package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.fvarrui.javapackager.gradle.GradleContext;
import io.github.fvarrui.javapackager.maven.MavenContext;

public abstract class Context<T> {
	
	public Context() {
		super();
		macInstallerGenerators.add(new GenerateDmg());
		macInstallerGenerators.add(new GeneratePkg());
		windowsInstallerGenerators.add(new GenerateSetup());
		windowsInstallerGenerators.add(new GenerateMsm());
		windowsInstallerGenerators.add(new GenerateMsi());
	}
	
	// common properties
	
	public abstract File getRootDir();
	public abstract T getLogger();

	// platform independent functions
	
	public abstract File createRunnableJar(Packager packager) throws Exception;
	public abstract File copyDependencies(Packager packager) throws Exception;
	public abstract File createTarball(Packager packager) throws Exception;
	public abstract File createZipball(Packager packager) throws Exception;
	public abstract File resolveLicense(Packager packager) throws Exception;
	public abstract File createWindowsExe(Packager packager) throws Exception;
	
	// installer producers
	
	private List<ArtifactGenerator> linuxInstallerGenerators = new ArrayList<>();
	private List<ArtifactGenerator> macInstallerGenerators = new ArrayList<>();
	private List<ArtifactGenerator> windowsInstallerGenerators = new ArrayList<>();

	public List<ArtifactGenerator> getLinuxInstallerGenerators() {
		return linuxInstallerGenerators;
	}
	
	public List<ArtifactGenerator> getMacInstallerGenerators() {
		return macInstallerGenerators;
	}
	
	public List<ArtifactGenerator> getWindowsInstallerGenerators() {
		return windowsInstallerGenerators;
	}
	
	// static context

	private static Context<?> context;

	public static Context<?> getContext() {
		return context;
	}

	public static void setContext(Context<?> context) {
		Context.context = context;
	}
	
	public static boolean isMaven() {
		return context instanceof MavenContext; 
	}

	public static boolean isGradle() {
		return context instanceof GradleContext; 
	}
	
	public static MavenContext getMavenContext() {
		return (MavenContext) context;
	}

	public static GradleContext getGradleContext() {
		return (GradleContext) context;
	}
	
}
