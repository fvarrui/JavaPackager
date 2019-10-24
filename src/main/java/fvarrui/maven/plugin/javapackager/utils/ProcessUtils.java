package fvarrui.maven.plugin.javapackager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

public class ProcessUtils {
	
	private static Object [] expandArray(Object [] array) {
		List<Object> args = new ArrayList<Object>();
		for (Object o : array) {
			if (o.getClass().isArray()) {
				Object [] a = (Object[]) o;
				args.addAll(Arrays.asList(a));
			} else {
				args.add(o);
			}
		}
		return args.toArray();
	}
	
	private static void createArguments(Commandline command, Object ... arguments) {
		for (Object argument : arguments) {
			if (argument instanceof File)
				command.createArg().setFile((File)argument);
			else if (argument.getClass().isArray())
				createArguments(command, argument);
			else
				command.createArg().setValue(argument.toString());
		}
	}
	
	public static String execute(File workingDirectory, String executable, Object ... arguments) throws MojoExecutionException {
		StringBuffer outputBuffer = new StringBuffer();
		try { 
			
			arguments = expandArray(arguments);
			
			Logger.info("Executing command: " + executable + " " + StringUtils.join(arguments, " "));
			
			Commandline command = new Commandline();
			command.setWorkingDirectory(workingDirectory);
			command.setExecutable(executable);
			createArguments(command, arguments);

			Process process = command.execute();
			
			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			
			while (process.isAlive()) {
				if (output.ready()) outputBuffer.append(Logger.info(output.readLine()) + "\n");
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
