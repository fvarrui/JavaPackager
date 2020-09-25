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

	public static void infoIndent(String info) {
		info(info);
		tabs++;
	}
	
	public static void infoUnindent(String info) {
		tabs--;
		info(info);
		info("");
	}
	
	public static void warnUnindent(String info) {
		tabs--;
		warn(info);
		info("");
	}

	public static void errorUnindent(String info) {
		tabs--;
		error(info);
		info("");
	}
	
}
