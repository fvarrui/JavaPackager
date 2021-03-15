package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a RPM package file including all app folder's content only for 
 * GNU/Linux so app could be easily distributed on Gradle context
 */
public class GenerateRpm extends ArtifactGenerator {

	public GenerateRpm() {
		super("RPM package");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getLinuxConfig().isGenerateRpm();
	}
	
	@Override
	protected File doApply(Packager packager) throws Exception {
		
		Logger.warn("Sorry! " + getArtifactName() + " generation is not yet available");

		return null;
	}
	
}
