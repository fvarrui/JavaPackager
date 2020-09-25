package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a DEB package file including all app folder's content only for 
 * GNU/Linux so app could be easily distributed on Gradle context
 */
public class GenerateDeb extends ArtifactGenerator {

	public GenerateDeb() {
		super("DEB package");
	}
	
	@Override
	public File apply(Packager packager) throws Exception {
		LinuxPackager linuxPackager = (LinuxPackager) packager;
		
		if (!linuxPackager.getLinuxConfig().isGenerateDeb()) {
			Logger.info(getArtifactName() + " generation skipped by 'linuxConfig.generateDeb' property!");
			return null;
		}

		Logger.warn("Sorry! " + getArtifactName() + " generation is not yet available");
		
		return null;
	}
	
}
