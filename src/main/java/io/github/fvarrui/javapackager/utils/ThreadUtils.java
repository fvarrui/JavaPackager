package io.github.fvarrui.javapackager.utils;

public class ThreadUtils {

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Logger.error(e.getMessage());
		}
	}
	
}
