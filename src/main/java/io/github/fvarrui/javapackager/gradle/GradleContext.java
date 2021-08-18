package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.internal.provider.Providers;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.jvm.toolchain.JavaLauncher;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.jvm.toolchain.JavaToolchainSpec;

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
	
	/**
	 * Returns project's default toolchain
	 * @return Default toolchain
	 */
	public File getDefaultToolchain() {
		// Default toolchain
		JavaToolchainSpec toolchain = project.getExtensions().getByType(JavaPluginExtension.class).getToolchain();

		// acquire a provider that returns the launcher for the toolchain
		JavaToolchainService service = project.getExtensions().getByType(JavaToolchainService.class);
		Provider<JavaLauncher> defaultLauncher = service.launcherFor(toolchain).orElse(Providers.notDefined());

		if (defaultLauncher.isPresent()) {
			return defaultLauncher.get().getMetadata().getInstallationPath().getAsFile();
		}
		return super.getDefaultToolchain();
	}

}
