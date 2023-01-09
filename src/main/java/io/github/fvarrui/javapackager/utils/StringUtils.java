package io.github.fvarrui.javapackager.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static String dosToUnix(String input) {
		return input.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
	}

	public static String find(String pattern, String data) {
		Pattern r = Pattern.compile(pattern);
		Matcher matcher = r.matcher(data);
		matcher.find();
		return matcher.group();
	}

}
