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
		this.getLinuxInstallerGenerators().add(new GenerateDeb());
		this.getLinuxInstallerGenerators().add(new GenerateRpm());
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
	public File createRunnableJar(Packager packager) throws Exception {
		return new CreateRunnableJar().apply(packager);
	}

	@Override
	public File copyDependencies(Packager packager) throws Exception {
		return new CopyDependencies().apply(packager);
	}

	@Override
	public File createTarball(Packager packager) throws Exception {
		return new CreateTarball().apply(packager);
	}

	@Override
	public File createZipball(Packager packager) throws Exception {
		return new CreateZipball().apply(packager);
	}

	@Override
	public File resolveLicense(Packager packager) throws Exception {
		// do nothing
		return null;
	}
	
	@Override
	public File createWindowsExe(Packager packager) throws Exception {
		return new CreateWindowsExe().apply(packager);	
	}

}
