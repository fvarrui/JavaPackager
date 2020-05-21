package io.github.fvarrui.javapackager.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

public class Logger {
	
	private static final String TAB = "    ";
	
	private static int tabs = 0;
	private static Log logger;
	
	public static void init(Log logger) {
		Logger.logger = logger;
	}
	
	public static String error(String error) {
		if (logger != null) logger.error(StringUtils.repeat(TAB, tabs) + error);
		return error;
	}

	public static String warn(String warn) {
		if (logger != null) logger.warn(StringUtils.repeat(TAB, tabs) + warn);
		return warn;
	}

	public static String info(String info) {
		if (logger != null) logger.info(StringUtils.repeat(TAB, tabs) + info);
		return info;
	}

	public static void append(String info) {
		info(info);
		tabs++;
	}
	
	public static void subtract(String info) {
		tabs--;
		info(info);
		info("");
	}

}
