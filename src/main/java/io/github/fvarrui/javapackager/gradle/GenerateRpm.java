package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.redline_rpm.Builder;
import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.Os;
import org.redline_rpm.header.RpmType;
import org.redline_rpm.payload.Directive;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.FileUtils;
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

//		LinuxPackager linuxPackager = (LinuxPackager) packager;
//
//		File appFolder = linuxPackager.getAppFolder();
//		String name = linuxPackager.getName();
//		String version = linuxPackager.getVersion();
//		String description = linuxPackager.getDescription();
//		String organizationName = linuxPackager.getOrganizationName();
//		File outputDirectory = linuxPackager.getOutputDirectory();
//
//		Builder builder = new Builder();
//		builder.setType(RpmType.BINARY);
//		builder.setPlatform(Architecture.X86_64, Os.LINUX);
//		builder.setPackage(name, version, "1");
//		builder.setPackager(organizationName);
//		builder.setDescription(description);
//		builder.setPrefixes("/opt/" + name);
//
//		// TODO add directories tree and all app files
//		// builder.addDirectory(appFolder.getAbsolutePath());
//		// builder.addFile("HelloWorldMaven/HelloWorldMaven", new File(appFolder,
//		// "HelloWorldMaven"), 0755);
//
//		builder.build(outputDirectory);
//
//		File rpm = new File(outputDirectory, name + "-" + version + "-1.x86_64.rpm");
//		if (rpm.exists()) {
//			File rpmOutput = new File(outputDirectory, name + "_" + version + ".rpm");
//			FileUtils.rename(rpm, rpmOutput.getName());
//			return rpmOutput;
//		}
//
//		return null;
	}

}
