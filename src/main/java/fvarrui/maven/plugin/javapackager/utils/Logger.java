package fvarrui.maven.plugin.javapackager.utils;

import org.apache.maven.plugin.logging.Log;

public class Logger {
	
	private static Log logger;
	
	public static void init(Log logger) {
		Logger.logger = logger;
	}
	
	public static String error(String error) {
		if (logger != null) logger.error(error);
		return error;
	}
	
	public static String info(String info) {
		if (logger != null) logger.info(info);
		return info;
	}
	
	public static String warn(String warn) {
		if (logger != null) logger.warn(warn);
		return warn;
	}

}
