package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;

public class GradleContext extends Context<Logger> {

	private Project project;
	
	public GradleContext(Project project) {
		super();
		this.project = project;
	}

	public Logger getLogger() {
		return project.getLogger();
	}

	public Project getProject() {
		return project;
	}

	@Override
	public File getRootDir() {
		return project.getRootDir();
	}

	@Override
	public File createRunnableJar(Packager packager) {
		return new CreateRunnableJar().apply(packager);
	}

	@Override
	public File copyDependencies(Packager packager) {
		return new CopyDependencies().apply(packager);
	}

	@Override
	public File createTarball(Packager packager) {
		// TODO create tarball from gradle
		return null;
	}

	@Override
	public File createZipball(Packager packager) {
		return new CreateZipball().apply(packager);
	}

	@Override
	public File resolveLicense(Packager packager) {
		// do nothing
		return null;
	}
	
	@Override
	public File createWindowsExe(Packager packager) {
		return new CreateWindowsExe().apply(packager);	
	}

}
