package io.github.fvarrui.javapackager.utils;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.plist.XMLPropertyListConfiguration;

public class InfoPlistUtils {
	
	public static void prettifyInfoPlist(File infoPlistFile) throws ConfigurationException {
		XMLPropertyListConfiguration config = new XMLPropertyListConfiguration(infoPlistFile);
		config.save();
	}

}
