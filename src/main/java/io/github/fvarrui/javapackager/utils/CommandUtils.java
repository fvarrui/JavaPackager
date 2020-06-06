package io.github.fvarrui.javapackager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

public class CommandUtils {

	private static void createArguments(Commandline command, Object... arguments) {
		for (Object argument : arguments) {
			
			if (argument == null)
				continue;
			
			if (argument.getClass().isArray()) {
				createArguments(command, (Object[])argument);
				continue;
			}
			
			if (argument instanceof File)
				argument = ((File) argument).getAbsolutePath();

			String arg = argument.toString().trim(); 
			if (!arg.contains("\"") && StringUtils.containsWhitespace(arg)) {
				arg = org.codehaus.plexus.util.StringUtils.quoteAndEscape(arg, '\"');
			}
			command.createArg().setValue(arg);

		}
	}

	public static String execute(File workingDirectory, String executable, Object... arguments)
			throws MojoExecutionException {
		StringBuffer outputBuffer = new StringBuffer();
		StringBuffer errorBuffer = new StringBuffer();
		try {

			Commandline command = new Commandline();
			command.setWorkingDirectory(workingDirectory);

			if (SystemUtils.IS_OS_WINDOWS) {
				command.setExecutable(executable);
				command.getShell().setShellArgs(new String[] { "/s", "/c" } );
				command.getShell().setQuotedArgumentsEnabled(false);
				createArguments(command, arguments);
			} else {
				Commandline bash = new Commandline();
				bash.setExecutable(executable);
				createArguments(bash, arguments);
				command.setExecutable("/bin/bash");
				command.createArg().setValue("-c");
				command.createArg().setLine(StringUtils.join(bash.getCommandline(), " "));
			}

			Logger.info("Executing command: " + StringUtils.join(command.getCommandline(), " "));

			Process process = command.execute();

			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while (process.isAlive()) {
				if (output.ready()) outputBuffer.append(Logger.info(output.readLine()) + "\n");
				if (error.ready()) errorBuffer.append(Logger.error(error.readLine()) + "\n");
			}
			output.close();
			error.close();
			
			if (process.exitValue() != 0) {
				throw new CommandLineException("Command execution failed: " + executable + " " + StringUtils.join(arguments, " "));
			}

		} catch (IOException | CommandLineException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

		return outputBuffer.toString();
	}

	public static String execute(String executable, Object... arguments) throws MojoExecutionException {
		return execute(new File("."), executable, arguments);
	}

}
