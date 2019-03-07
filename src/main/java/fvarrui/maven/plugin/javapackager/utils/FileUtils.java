package fvarrui.maven.plugin.javapackager.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.IOUtil;

import com.google.common.io.Files;

public class FileUtils {
	
	public static void copyFileToFile(File source, File dest) throws MojoExecutionException {
		try {
			Files.copy(source, dest);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	public static void copyFileToFolder(File source, File destFolder) throws MojoExecutionException {
		copyFileToFile(source, new File(destFolder, source.getName()));
	}
	
	public static void concatStreams(OutputStream dest, InputStream ... sources) throws MojoExecutionException {
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

	public static void moveFolderToFolder(File from, File to) throws MojoExecutionException {
		if (!from.isDirectory()) throw new MojoExecutionException("Source folder " + from + " is not a directory");
		if (!to.exists()) to.mkdirs();
		else if (!to.isDirectory()) throw new MojoExecutionException("Destination folder " + to + " is not a directory");
		try {
			Files.move(from, to);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	public static void moveFileToFolder(File from, File to) throws MojoExecutionException {
		if (!from.isFile()) throw new MojoExecutionException("Source file " + from + " is not a file");
		if (!to.exists()) to.mkdirs();
		try {
			Files.move(from, new File(to, from.getName()));
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
	
	public static void copyStreamToFile(InputStream is, File dest) throws MojoExecutionException {
        try {
        	FileOutputStream destStream = new FileOutputStream(dest);
            IOUtil.copy(is, destStream);
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not copy input stream to " + dest, ex);
        }
	}

}
