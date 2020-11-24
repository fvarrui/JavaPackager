package io.github.fvarrui.javapackager.utils;

import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.packagers.Context;

public class Logger {
	
	private static final String TAB = "    ";
	
	private static int tabs = 0;
	
	public static String error(String error) {
		if (Context.isMaven()) Context.getMavenContext().getLogger().error(StringUtils.repeat(TAB, tabs) + error);
		if (Context.isGradle()) Context.getGradleContext().getLogger().error(StringUtils.repeat(TAB, tabs) + error);
		return error;
	}

	public static String error(String error, Throwable t) {
		if (Context.isMaven()) {
			Context.getMavenContext().getLogger().error(StringUtils.repeat(TAB, tabs) + error);
			Context.getMavenContext().getLogger().error(t);
		}
		if (Context.isGradle()) {
			Context.getGradleContext().getLogger().error(StringUtils.repeat(TAB, tabs) + error, t);
		}
		return error;
	}

	public static String warn(String warn) {
		if (Context.isMaven()) Context.getMavenContext().getLogger().warn(StringUtils.repeat(TAB, tabs) + warn);
		if (Context.isGradle()) Context.getGradleContext().getLogger().warn(StringUtils.repeat(TAB, tabs) + warn);
		return warn;
	}

	public static String info(String info) {
		if (Context.isMaven()) Context.getMavenContext().getLogger().info(StringUtils.repeat(TAB, tabs) + info);
		if (Context.isGradle()) Context.getGradleContext().getLogger().quiet(StringUtils.repeat(TAB, tabs) + info);
		return info;
	}

	public static void infoIndent(String msg) {
		info(msg);
		tabs++;
	}
	
	public static void infoUnindent(String msg) {
		tabs--;
		info(msg);
		info("");
	}
	
	public static void warnUnindent(String msg) {
		tabs--;
		warn(msg);
		info("");
	}

	public static void errorUnindent(String msg) {
		tabs--;
		error(msg);
		info("");
	}
	
	public static void errorUnindent(String msg, Throwable t) {
		tabs--;
		error(msg, t);
		info("");
	}
	
}
