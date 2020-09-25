package io.github.fvarrui.javapackager.maven;

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;

public class MavenContext extends Context<Log> {

	private Log logger;
	private ExecutionEnvironment env;
	
	public MavenContext(ExecutionEnvironment env, Log logger) {
		super();
		this.env = env;
		this.logger = logger;
		this.getLinuxInstallerGenerators().add(new GenerateDeb());
		this.getLinuxInstallerGenerators().add(new GenerateRpm());
	}

	public ExecutionEnvironment getEnv() {
		return env;
	}

	public Log getLogger() {
		return logger;
	}

	@Override
	public File getRootDir() {
		return env.getMavenProject().getBasedir();
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
		return new CreateTarball().apply(packager);
	}

	@Override
	public File createZipball(Packager packager) {
		return new CreateZipball().apply(packager);
	}

	@Override
	public File resolveLicense(Packager packager) {
		return new ResolveLicenseFromPOM().apply(packager);
	}
	
	@Override
	public File createWindowsExe(Packager packager) {
		return new CreateWindowsExe().apply(packager);
	}

}
