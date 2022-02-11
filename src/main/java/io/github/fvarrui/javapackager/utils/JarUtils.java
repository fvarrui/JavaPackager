package io.github.fvarrui.javapackager.utils;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.cli.CommandLineException;

public class JarUtils {

	public static void addFileToJar(File jarFile, File newFile) throws IOException, CommandLineException {
		File jar = new File(System.getProperty("java.home"), "/bin/jar");
		CommandUtils.execute(newFile.getParentFile(), jar.getAbsolutePath(), "uf", jarFile, newFile.getName());
	}

}
