package io.github.fvarrui.javapackager.maven;

import java.io.File;
import java.time.Year;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Organization;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.packagers.AbstractCreateWindowsExe;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.CreateWindowsExeWhy;
import io.github.fvarrui.javapackager.packagers.CreateWindowsExeWinRun4j;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;

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
		
		// initialize some default params on project (avoid launch4j-maven-plugin warnings)
		MavenProject project = env.getMavenProject();
		if (project.getOrganization() == null) {
			project.setOrganization(new Organization());
		}
		// set default organization name
		if (StringUtils.isBlank(project.getOrganization().getName())) {
			project.getOrganization().setName(Packager.DEFAULT_ORGANIZATION_NAME);
		}
		// set default inception year
		if (StringUtils.isBlank(project.getInceptionYear())) {
			project.setInceptionYear(Year.now().toString());
		}
		// set default description
		if (StringUtils.isBlank(project.getDescription())) {
			project.setDescription(project.getArtifactId());
		}
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
