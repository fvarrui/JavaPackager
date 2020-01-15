package fvarrui.maven.plugin.javapackager;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

import fvarrui.maven.plugin.javapackager.utils.CommandUtils;

public class Main {

	public static void main(String[] args) throws MojoExecutionException {
		String result = 
			CommandUtils.execute2(
				new File("."),
				"/bin/bash/",
				"-c",
				"/Library/Java/JavaVirtualMachines/adoptopenjdk-13.jdk/Contents/Home/bin/jdeps", 
				"-q",
				"--ignore-missing-deps",
				"--print-module-deps",
				"--multi-release", "13",
				"/Users/fran/teuton-panel/target/app/teuton-panel.app/Contents/Resources/Java/libs/*.jar",
				"/Users/fran/teuton-panel/target/teuton-panel-0.1.1-runnable.jar"
			);
			
		System.out.println(result);
	}

}
