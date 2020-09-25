package io.github.fvarrui.javapackager.maven;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.maven.model.License;

import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a runnable jar file from sources on Maven context
 */
public class ResolveLicenseFromPOM extends ArtifactGenerator {
	
	public ResolveLicenseFromPOM() {
		super("LICENSE");
	}
	
	@Override
	public File apply(Packager packager) {
		Logger.infoIndent("Trying to resolve license from POM ...");
		
		File licenseFile = packager.getLicenseFile();
		List<License> licenses = Context.getMavenContext().getEnv().getMavenProject().getLicenses();
		File assetsFolder = packager.getAssetsFolder();
		
		// if license not specified, gets from pom
		if (licenseFile == null && !licenses.isEmpty()) {
			String urlStr = null; 
			try {
				urlStr = licenses.get(0).getUrl(); 
				URL licenseUrl = new URL(urlStr);
				licenseFile = new File(assetsFolder, "LICENSE");
				FileUtils.downloadFromUrl(licenseUrl, licenseFile);
			} catch (MalformedURLException e) {
				Logger.error("Invalid license URL specified: " + urlStr);
				licenseFile = null;
			} catch (IOException e) {
				Logger.error("Cannot download license from " + urlStr);
				licenseFile = null;
			}
		}

		if (licenseFile != null)
			Logger.infoUnindent("License resolved " + licenseFile + "!");
		else
			Logger.infoUnindent("License not resolved!");
		
		return licenseFile;
	}
	
}
