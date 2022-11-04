package io.github.fvarrui.javapackager.maven;

import java.io.File;

import io.github.fvarrui.javapackager.packagers.*;
import org.apache.maven.plugin.logging.Log;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

/**
 * Maven context 
 */
public class MavenContext extends Context<Log> {

	private Log logger;
	private ExecutionEnvironment env;
	
	public MavenContext(ExecutionEnvironment env, Log logger) {
		super();
		this.env = env;
		this.logger = logger;
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
	public File getBuildDir() {
		return new File(env.getMavenProject().getBuild().getDirectory());
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
		return new ResolveLicenseFromPOM().apply(packager);
	}

	@Override
	public File createWindowsExe(WindowsPackager packager) throws Exception {
		AbstractCreateWindowsExe createWindowsExe;
		switch (packager.getWinConfig().getExeCreationTool()) {
			case launch4j: createWindowsExe = new CreateWindowsExeLaunch4j(); break;
			case why: createWindowsExe = new CreateWindowsExeWhy(); break;
			case winrun4j: createWindowsExe = new CreateWindowsExeWinRun4j(); break;
			default: return null;
		}
		if (!createWindowsExe.skip(packager)) {
			return createWindowsExe.apply(packager);
		}
		return null;
	}
	


}
