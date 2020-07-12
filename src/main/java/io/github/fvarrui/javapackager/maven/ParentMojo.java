package io.github.fvarrui.javapackager.maven;

import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.utils.Logger;

public abstract class ParentMojo extends AbstractMojo {
	
	// maven components
	
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${session}", readonly = true)
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	// maven execution environment 
	
	private ExecutionEnvironment env;

	public ParentMojo() {
		super();
		Logger.init(getLog()); // sets Mojo's logger to Logger class, so it could be used from static methods
	}
	
	public ExecutionEnvironment getEnv() {
		if (env == null) {
			this.env = executionEnvironment(mavenProject, mavenSession, pluginManager);
		}
		return env;
	}

}
