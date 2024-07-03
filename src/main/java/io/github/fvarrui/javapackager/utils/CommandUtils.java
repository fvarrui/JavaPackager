package io.github.fvarrui.javapackager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * Command utils
 */
public class CommandUtils {

	public static String executeOnDirectory(File workingDirectory, String executable, Object... arguments) throws IOException, CommandLineException {
		ExecutionResult result = executeWithResult(workingDirectory, executable, arguments);
		if (result.getExitCode() != 0) {
			throw new CommandLineException("Command execution failed: " + executable + " " + StringUtils.join(arguments, " "));
		}
		return result.getOutput();
	}

	public static String execute(File executable, Object... arguments) throws IOException, CommandLineException {
		return execute(executable.getAbsolutePath(), arguments);
	}

	public static String execute(String executable, Object... arguments) throws IOException, CommandLineException {
		return executeOnDirectory(new File("."), executable, arguments);
	}
	
	public static String execute(String executable, List<Object> arguments) throws IOException, CommandLineException {
		return executeOnDirectory(new File("."), executable, arguments.toArray(new Object[0]));
	}
	
	public static ExecutionResult executeWithResult(File workingDirectory, String executable, Object... arguments) throws IOException, CommandLineException {
		ExecutionResult result = new ExecutionResult();
		
		StringBuilder outputBuffer = new StringBuilder();
		StringBuilder errorBuffer = new StringBuilder();
		
		Commandline command = new Commandline();
		command.setWorkingDirectory(workingDirectory);
		command.setExecutable(executable);
		command.createArguments(arguments);

		String commandLine = command.getCommandLineAsString();

		Logger.info("Executing command: " + commandLine);

		Process process = command.execute();

		BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream(), CharsetUtil.getCommandLineCharset()));
		BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while (process.isAlive() || output.ready() || error.ready()) {
			if (output.ready()) {
				String outputLine = output.readLine();
				Logger.info(outputLine);
				outputBuffer.append(outputLine).append("\n");
			}
			if (error.ready()) {
				String errorLine = error.readLine();
				Logger.error(errorLine);					
				errorBuffer.append(errorLine).append("\n");
			}
		}
		output.close();
		error.close();

		result.setCommandLine(commandLine);
		result.setOutput(outputBuffer.toString());
		result.setError(errorBuffer.toString());
		result.setExitCode(process.exitValue());
			
		return result;
	}
	
	public static String run(String ... command) throws IOException {
    	Process p = Runtime.getRuntime().exec(command);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String result = br.readLine();	        	
		br.close();
		return result;
	}

}
