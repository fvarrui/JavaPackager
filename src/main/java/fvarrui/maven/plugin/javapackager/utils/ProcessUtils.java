package fvarrui.maven.plugin.javapackager.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class ProcessUtils {
	
	public static void exec(String... command) throws MojoExecutionException {
		exec(null, command);
	}

	public static void exec(Log log, String... command) throws MojoExecutionException {
		try { 
			Process process = Runtime.getRuntime().exec(command);
			if (log != null) {
				log.info("Command line: " + StringUtils.join(command, " "));
				BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while (process.isAlive()) {
					if (output.ready()) {
						log.info(output.readLine());
					}
					if (error.ready()) {
						log.info(error.readLine());
					}
				}
			}
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
