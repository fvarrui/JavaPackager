package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.gradle.GradleContext;
import io.github.fvarrui.javapackager.maven.MavenContext;

public abstract class Context<T> {
	
	// common properties
	
	public abstract File getRootDir();
	public abstract T getLogger();

	// commons functions
	
	public abstract File createRunnableJar(Packager packager);
	public abstract File copyDependencies(Packager packager);
	public abstract File createTarball(Packager packager);
	public abstract File createZipball(Packager packager);
	public abstract File resolveLicense(Packager packager);
	public abstract File createWindowsExe(Packager packager);

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
