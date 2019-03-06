package fvarrui.maven.plugin.javapackager.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;

import com.google.common.io.Files;

public class FileUtils {
	
	public static void copy(File source, File dest) throws MojoExecutionException {
		try {
			Files.copy(source, dest);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	public static void copyToFolder(File source, File destFolder) throws MojoExecutionException {
		copy(source, new File(destFolder, source.getName()));
	}
	
	public static void concat(OutputStream dest, InputStream ... sources) throws MojoExecutionException {
		try {

			for (InputStream source : sources) {
				IOUtil.copy(source, dest);
				source.close();
			}

			dest.flush();
			dest.close();

		} catch (IOException e) {
			throw new MojoExecutionException("Error concatenating streams", e);
		}
	}
	
	public static void move(File from, File to) throws MojoExecutionException {
		try {
			Files.move(from, to);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
