package io.github.fvarrui.javapackager.utils;

import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.gradle.GradleContext;
import io.github.fvarrui.javapackager.maven.MavenContext;

public class Logger {
	
	private static final String TAB = "    ";
	
	private static int tabs = 0;
	
	public static String error(String error) {
		if (MavenContext.getLogger() != null) MavenContext.getLogger().error(StringUtils.repeat(TAB, tabs) + error);
		if (GradleContext.getLogger() != null) GradleContext.getLogger().error(StringUtils.repeat(TAB, tabs) + error);
		return error;
	}

	public static String warn(String warn) {
		if (MavenContext.getLogger() != null) MavenContext.getLogger().warn(StringUtils.repeat(TAB, tabs) + warn);
		if (GradleContext.getLogger() != null) GradleContext.getLogger().warn(StringUtils.repeat(TAB, tabs) + warn);
		return warn;
	}

	public static String info(String info) {
		if (MavenContext.getLogger() != null) MavenContext.getLogger().info(StringUtils.repeat(TAB, tabs) + info);
		if (GradleContext.getLogger() != null) GradleContext.getLogger().info(StringUtils.repeat(TAB, tabs) + info);
		return info;
	}

	public static void infoIndent(String info) {
		info(info);
		tabs++;
	}
	
	public static void infoUnindent(String info) {
		tabs--;
		info(info);
		info("");
	}

}
