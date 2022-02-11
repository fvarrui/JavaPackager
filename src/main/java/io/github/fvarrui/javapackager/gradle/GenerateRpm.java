package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a RPM package file including all app folder's content only for 
 * GNU/Linux so app could be easily distributed on Gradle context
 */
public class GenerateRpm extends ArtifactGenerator<LinuxPackager> {

	public GenerateRpm() {
		super("RPM package");
	}
	
	@Override
	public boolean skip(LinuxPackager packager) {
		return !packager.getLinuxConfig().isGenerateRpm() || !Platform.linux.isCurrentPlatform();
	}
	
	@Override
	protected File doApply(LinuxPackager packager) throws Exception {
		
		Logger.warn("Sorry! " + getArtifactName() + " generation is not yet available");

		return null;
	}
	
}
