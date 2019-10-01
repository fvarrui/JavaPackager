package fvarrui.maven.plugin.javapackager;

import org.apache.commons.lang.SystemUtils;

public class Main {

	public static void main(String[] args) {
		System.out.println(SystemUtils.JAVA_VERSION.split("\\.")[0]);
	}

}
