package io.github.fvarrui.javapackager.gradle;

import org.gradle.api.logging.Logger;

public class GradleContext {

	private static Logger logger;

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		GradleContext.logger = logger;
	}

}
