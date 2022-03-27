package io.github.fvarrui.javapackager.utils;

import static org.apache.commons.io.FileUtils.copyDirectoryToDirectory;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.moveDirectoryToDirectory;
import static org.apache.commons.io.FileUtils.moveFileToDirectory;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.model.Platform;

/**
 * Common files and folders utils
 */
public class FileUtils {

	/**
	 * Creates a directory if it doesn't exist
	 * @param dir Directory to be created
	 * @return Created directory or existing one
	 */
	public static File mkdir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	
	/**
	 * Creates a directory inside an exiting one
	 * @param parent Parent directory
	 * @param name New directory name
	 * @return Created directory
	 */
	public static File mkdir(File parent, String name) {
		File dir = new File(parent, name);
		return mkdir(dir);
	}
	
	/**
	 * Copies a file
	 * @param source Source file
	 * @param dest Destination file
	 * @throws Exception If the file cannot be copied
	 */
	public static void copyFileToFile(File source, File dest) throws Exception {
		Logger.info("Copying file [" + source + "] to file [" + dest + "]");			
		try {
			copyFile(source, dest);
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}
	
	/**
	 * Copies a file inside an existing folder 
	 * @param source File to be copied
	 * @param destFolder Destination folder
	 * @throws Exception If the file cannot be copied
	 */
	public static void copyFileToFolder(File source, File destFolder) throws Exception {
		copyFileToFolder(source, destFolder, false);
	}
	
	public static void copyFileToFolder(File source, File destFolder, boolean overwrite) throws Exception {
		Logger.info("Copying file [" + source + "] to folder [" + destFolder + "]");
		File destFile = new File(destFolder, source.getName());
		if (destFile.exists() && !overwrite) return;
		try {
			if (Platform.windows.isCurrentPlatform())
				Files.copy(source.toPath(), destFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
			else {
				CommandUtils.execute("cp", source, destFile);
			}			
		} catch (IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}
	
	/**
	 * Concatenates several files into a new one
	 * @param dest Destination file
	 * @param sources Source files array
	 * @throws Exception If a file cannot be writen to the destination 
	 */
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
			if (Platform.windows.isCurrentPlatform())
				copyDirectoryToDirectory(from, to);
			else {
				CommandUtils.execute("cp", "-R", from, to);
			}
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
		String content = readFileToString(dest, StandardCharsets.UTF_8);
		content = function.apply(content);
		writeStringToFile(dest, content, StandardCharsets.UTF_8);
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
	
	/**
	 * Renames a file
	 * @param file File to be renamed
	 * @param newName New file name
	 */
	public static void rename(File file, String newName) {
		Logger.info("Renaming file [" + file + "] to [" + newName + "]");		
		file.renameTo(new File(file.getParentFile(), newName)); 
	}
	
	/**
	 * Finds all files in folder that matches the regular expression
	 * @param searchFolder Searching folder
	 * @param regex Regular expression
	 * @return List of found files or an empty list if nothing matches 
	 */
	public static List<File> findFiles(File searchFolder, String regex) {
		return Arrays.asList(searchFolder.listFiles((dir, name) -> Pattern.matches(regex, name)))
				.stream()
				.map(f -> new File(f.getName()))
				.collect(Collectors.toList());
	}
	
	/**
	 * Finds the first file in folder that matches the regular expression
	 * @param searchFolder Searching folder
	 * @param regex Regular expression
	 * @return Found file or null if nothing matches 
	 */
	public static File findFirstFile(File searchFolder, String regex) {
		return Arrays.asList(searchFolder.listFiles((dir, name) -> Pattern.matches(regex, name)))
				.stream()
				.map(f -> new File(f.getName()))
				.findFirst()
				.get();
	}
	
	/**
	 * Download a resource from an URL to a file
	 * @param url URL to download
	 * @param file File to copy the downloaded resource
	 * @throws IOException Resource cannot be copied/downloaded
	 */
	public static void downloadFromUrl(URL url, File file) throws IOException {
		org.apache.commons.io.FileUtils.copyURLToFile(url, file);
		Logger.info("File downloaded from [" + url + "] to [" + file.getAbsolutePath() + "]");
	}
	
	/**
	 * Checks if a file exists or is not null
	 * @param file File
	 * @return true if file exits, false if doesn't or is null
	 */
	public static boolean exists(File file) {
		return file != null && file.exists();
	}
	
	/**
	 * Checks if a folder contains a file
	 * @param folder Searching folder
	 * @param filename Searched file name
	 * @return true if folder contains file
	 */
	public static boolean folderContainsFile(File folder, String filename) {
		return new File(folder, filename).exists();
	}

}
