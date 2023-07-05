package io.github.fvarrui.javapackager.utils;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.cli.CommandLineException;

import io.github.fvarrui.javapackager.packagers.Context;

public class JarUtils {

	/**
	 * Runs "jar uf <jarfile> <newfile>" to add newfile into jarfile. 
	 * @param jarFile JAR file
	 * @param newFile File to add to jar file
	 * @throws IOException If something related to IO went wrong
	 * @throws CommandLineException If something related to command execution went wrong 
	 */
	public static void addFileToJar(File jarFile, File newFile) throws IOException, CommandLineException {		
		File jar = new File(Context.getContext().getDefaultToolchain(), "/bin/jar");
		CommandUtils.executeOnDirectory(newFile.getParentFile(), jar.getAbsolutePath(), "uf", jarFile, newFile.getName());
	}

}