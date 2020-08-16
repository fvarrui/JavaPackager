package io.github.fvarrui.javapackager.maven;

import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

public class MavenContext {
	
	private static ExecutionEnvironment env;

	public static ExecutionEnvironment getEnv() {
		return env;
	}
	
	public static void setEnv(ExecutionEnvironment env) {
		MavenContext.env = env;
	}

}
