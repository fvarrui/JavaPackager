package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Gradle context 
 */
public class GradleContext extends Context<Logger> {

	private Project project;

	private Launch4jLibraryTask libraryTask;
	
	public GradleContext(Project project) {
		super();
		this.project = project;
		
		// gradle dependant generators 
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

	public Launch4jLibraryTask getLibraryTask() {
		return libraryTask;
	}

	public void setLibraryTask(Launch4jLibraryTask libraryTask) {
		this.libraryTask = libraryTask;
	}

}
