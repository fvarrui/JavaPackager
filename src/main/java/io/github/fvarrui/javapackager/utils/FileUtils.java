package io.github.fvarrui.javapackager.utils;

import static org.apache.commons.io.FileUtils.copyDirectoryToDirectory;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.copyFileToDirectory;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.moveDirectoryToDirectory;
import static org.apache.commons.io.FileUtils.moveFileToDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;


public class FileUtils {

	public static File mkdir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	
	public static File mkdir(File parent, String name) {
		File dir = new File(parent, name);
		return mkdir(dir);
	}
	
	public static void copyFileToFile(File source, File dest) throws Exception {
		Logger.info("Copying file [" + source + "] to folder [" + dest + "]");			
		try {
			copyFile(source, dest);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}
	
	public static void copyFileToFolder(File source, File destFolder) throws Exception {
		Logger.info("Copying file [" + source + "] to folder [" + destFolder + "]");
		if (new File(destFolder, source.getName()).exists()) return;
		try {
			copyFileToDirectory(source, destFolder);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}
	
	public static void concat(File dest, File ... sources) throws Exception {
		Logger.info("Concatenating files [" + StringUtils.join(sources, ",") + "] into file [" + dest + "]");
		try {
			FileOutputStream fos = new FileOutputStream(dest);
			for (File source : sources) {
				FileInputStream fis = new FileInputStream(source);
				IOUtils.copy(fis, fos);
				fis.close();
			}
			fos.flush();
			fos.close();
		} catch (IOException e) {
			throw new Exception("Error concatenating streams", e);
		}
	}
	
	public static void copyFolderToFolder(File from, File to) throws Exception {
		Logger.info("Copying folder [" + from + "] to folder [" + to + "]");
		if (!from.isDirectory()) throw new Exception("Source folder " + from + " is not a directory");
		try {
			copyDirectoryToDirectory(from, to);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}
	
	public static void copyFolderContentToFolder(File from, File to) throws Exception {
		Logger.info("Copying folder content [" + from + "] to folder [" + to + "]");
		if (!from.isDirectory()) throw new Exception("Source folder " + from + " is not a directory");
		if (!to.exists()) to.mkdirs();
		else if (!to.isDirectory()) throw new Exception("Destination folder " + to + " is not a directory");
		for (File file : from.listFiles()) {
			if (file.isDirectory())
				copyFolderToFolder(file, to);
			else
				copyFileToFolder(file, to);
		}
	}

	public static void moveFolderToFolder(File from, File to) throws Exception {
		Logger.info("Moving folder [" + from + "] to folder [" + to + "]");
		if (!from.isDirectory()) throw new Exception("Source folder " + from + " is not a directory");
		else if (to.exists() && !to.isDirectory()) throw new Exception("Destination folder " + to + " is not a directory");
		try {
			moveDirectoryToDirectory(from, to, true);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}
	
	public static void moveFolderContentToFolder(File from, File to) throws Exception {
		Logger.info("Moving folder content [" + from + "] to folder [" + to + "]");
		if (!from.isDirectory()) throw new Exception("Source folder " + from + " is not a directory");
		else if (!to.isDirectory()) throw new Exception("Destination folder " + to + " is not a directory");
		try {
			for (File file : from.listFiles()) {
				if (file.isDirectory())
					moveDirectoryToDirectory(file, to, true);
				else
					moveFileToDirectory(file, to, true);
			}
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	public static void moveFileToFolder(File from, File to) throws Exception {
		Logger.info("Moving file [" + from + "] to folder [" + to + "]");		
		if (!from.isFile()) throw new Exception("Source file " + from + " is not a file");
		if (!to.exists()) to.mkdirs();
		try {
			moveFileToDirectory(from, to, true);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}
	
	private static void copyStreamToFile(InputStream is, File dest) throws Exception {
        try {
        	copyInputStreamToFile(is, dest);
        } catch (IOException ex) {
            throw new Exception("Could not copy input stream to " + dest, ex);
        }
	}
	
	public static void copyResourceToFile(String resource, File dest, boolean unixStyleNewLines) throws Exception  {
		copyResourceToFile(resource, dest);
		if (unixStyleNewLines) {
			try {
				processFileContent(dest, c -> c.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n"));
			} catch (IOException e) {
				throw new Exception(e.getMessage(), e);
			}
		}
	}
	
	public static void processFileContent(File dest, Function<String, String> function) throws IOException {
		String content = org.apache.commons.io.FileUtils.readFileToString(dest, StandardCharsets.UTF_8);
		content = function.apply(content);
		org.apache.commons.io.FileUtils.writeStringToFile(dest, content, StandardCharsets.UTF_8);
	}
	
	public static void copyResourceToFile(String resource, File dest) throws Exception  {
		Logger.info("Copying resource [" + resource + "] to file [" + dest + "]");		
		copyStreamToFile(FileUtils.class.getResourceAsStream(resource), dest);
	}

	
	public static void createSymlink(File link, File target) throws Exception {
		Logger.info("Creating symbolic link [" + link + "] to [" + target + "]");		
        try {
			Files.createSymbolicLink(link.toPath(), target.toPath());
		} catch (IOException e) {
			throw new Exception("Could not create symlink " + link + " to " + target, e);
		}
	}
	
	public static void removeFolder(File folder) throws Exception {
		Logger.info("Removing folder [" + folder + "]");		
		try {
			deleteDirectory(folder);
		} catch (IOException e) {
            throw new Exception("Could not remove folder " + folder, e);
		}
	}
	
	public static void rename(File file, String newName) {
		Logger.info("Changing name of [" + file + "] to [" + newName + "]");		
		file.renameTo(new File(file.getParentFile(), newName)); 
	}
	
	public static List<File> findFiles(File searchFolder, String regex) {
		return Arrays.asList(searchFolder.listFiles((dir, name) -> Pattern.matches(regex, name)))
				.stream()
				.map(f -> new File(f.getName()))
				.collect(Collectors.toList());
	}
	
	public static File findFirstFile(File searchFolder, String regex) {
		return Arrays.asList(searchFolder.listFiles((dir, name) -> Pattern.matches(regex, name)))
				.stream()
				.map(f -> new File(f.getName())).findFirst().get();
	}
	
	public static void downloadFromUrl(URL url, File file) throws IOException {
		org.apache.commons.io.FileUtils.copyURLToFile(url, file);
		Logger.info("File downloaded from [" + url + "] to [" + file.getAbsolutePath() + "]");
	}
	
	public static boolean exists(File file) {
		return file != null && file.exists();
	}

}
