package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.regex.Pattern;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates a Snap package file including all app folder's content only for 
 * GNU/Linux so app could be easily distributed
 */
public class GenerateSnap extends ArtifactGenerator<LinuxPackager> {

	public GenerateSnap() {
		super("Snap package");
	}
	
	@Override
	public boolean skip(LinuxPackager packager) {
		
		if (!packager.getLinuxConfig().isGenerateSnap()) {
			return true;
		}
		
		if (!packager.getPlatform().isCurrentPlatform() && !packager.isForceInstaller()) {
			Logger.warn(getArtifactName() + " cannot be generated due to the target platform (" + packager.getPlatform() + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return true;
		}
		
		return false;
	}
	
	@Override
	protected File doApply(LinuxPackager packager) throws Exception {
		
		File outputDirectory = packager.getOutputDirectory();

		File snapFolder = new File(outputDirectory, "snap");
		
		// generates snapcraft.yaml file from velocity template
		File snapcraftFile = new File(snapFolder, "snapcraft.yaml");
		VelocityUtils.render("linux/snapcraft.yaml.vtl", snapcraftFile, packager);
		Logger.info("Snapcraft config file rendered in " + snapcraftFile.getAbsolutePath());

		// create snap
		String result = CommandUtils.executeOnDirectory(outputDirectory, "snapcraft", "--use-lxd");

		// finds snap filename on result
		Pattern p = Pattern.compile(".*Created snap package (.*)");
		return  new File(outputDirectory, p.matcher(result).group(1));

	}
	
}
