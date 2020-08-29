package io.github.fvarrui.javapackager.maven;

import org.apache.maven.plugin.logging.Log;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

public class MavenContext {

	private static Log logger;
	private static ExecutionEnvironment env;

	public static ExecutionEnvironment getEnv() {
		return env;
	}

	public static void setEnv(ExecutionEnvironment env) {
		MavenContext.env = env;
	}

	public static Log getLogger() {
		return logger;
	}

	public static void setLogger(Log logger) {
		MavenContext.logger = logger;
	}

}
