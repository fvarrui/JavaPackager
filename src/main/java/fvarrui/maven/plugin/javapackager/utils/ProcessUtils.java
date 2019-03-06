package fvarrui.maven.plugin.javapackager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

public class ProcessUtils {
	
	public static String execute(File workingDirectory, String executable, Object ... arguments) throws MojoExecutionException {
		StringBuffer outputBuffer = new StringBuffer();
		try { 
			
			Logger.info("Executed command: " + executable + " " + StringUtils.join(arguments, " "));
			
			Commandline command = new Commandline();
			command.setWorkingDirectory(workingDirectory);
			command.setExecutable(executable);
			for (Object argument : arguments) {
				if (argument instanceof File)
					command.createArg().setFile((File)argument);
				else
					command.createArg().setValue(argument.toString());
			}

			Process process = command.execute();
			
			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			while (process.isAlive()) {
				if (output.ready()) outputBuffer.append(Logger.info(output.readLine()));
				if (error.ready()) Logger.error(error.readLine());
			}
			
			output.close();
			error.close();
			
		} catch (IOException | CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		
		return outputBuffer.toString();
	}
	
	public static String execute(String executable, Object... arguments) throws MojoExecutionException {
		return execute(new File("."), executable, arguments);
	}

}
