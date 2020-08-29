package io.github.fvarrui.javapackager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

public class CommandUtils {

	private static void createArguments(Commandline command, Object... arguments) {
		for (Object argument : arguments) {
			
			if (argument == null)
				continue;
			
			if (argument.getClass().isArray()) {
				createArguments(command, (Object[])argument);
				continue;
			}
			
			if (argument instanceof File) {
				
				File argFile = (File) argument;
				if (argFile.getName().contains("*")) {
					argument = org.codehaus.plexus.util.StringUtils.quoteAndEscape(argFile.getParentFile().getAbsolutePath(), '\"') + File.separator + argFile.getName();
				} else {
					argument = ((File) argument).getAbsolutePath();
				}
				
			}

			String arg = argument.toString().trim(); 
			if (!arg.contains("\"") && StringUtils.containsWhitespace(arg)) {
				arg = org.codehaus.plexus.util.StringUtils.quoteAndEscape(arg, '\"');
			}
			command.createArg().setValue(arg);

		}
	}

	public static String execute(File workingDirectory, String executable, Object... arguments) throws Exception {
		StringBuffer outputBuffer = new StringBuffer();
		StringBuffer errorBuffer = new StringBuffer();
		try {

			Commandline command = new Commandline();
			command.setWorkingDirectory(workingDirectory);
			command.setExecutable(executable);
			command.getShell().setQuotedArgumentsEnabled(false);

			if (SystemUtils.IS_OS_WINDOWS) {
				command.getShell().setShellArgs(new String[] { "/s", "/c" } );
			}
		
			createArguments(command, arguments);

			Logger.info("Executing command: " + StringUtils.join(command.getCommandline(), " "));

			Process process = command.execute();

			BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while (process.isAlive() || output.ready() || error.ready()) {
				if (output.ready()) {
					String outputLine = output.readLine();
					Logger.info(outputLine);
					outputBuffer.append(outputLine + "\n");
				}
				if (error.ready()) {
					String errorLine = error.readLine();
					Logger.error(errorLine);
					errorBuffer.append(errorLine + "\n");
				}
			}
			output.close();
			error.close();
			
			if (process.exitValue() != 0) {
				throw new CommandLineException("Command execution failed: " + executable + " " + StringUtils.join(arguments, " "));
			}

		} catch (IOException | CommandLineException e) {
			throw new Exception(e.getMessage(), e);
		}

		return outputBuffer.toString();
	}

	public static String execute(String executable, Object... arguments) throws Exception {
		return execute(new File("."), executable, arguments);
	}

}
