package fvarrui.maven.plugin.javapackager.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FilenameExtensionFilter implements FilenameFilter {
	
	private String extension = "";
	
	public FilenameExtensionFilter(String extension) {
		this.extension = extension;
	}

	@Override
	public boolean accept(File dir, String name) {
		if (this.extension == null || extension.isEmpty()) return true;
		return (name.toLowerCase().endsWith("." + extension));
	}

}
