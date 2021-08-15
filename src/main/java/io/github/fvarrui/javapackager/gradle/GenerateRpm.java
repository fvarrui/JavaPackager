package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import org.redline_rpm.Builder;
import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.Os;
import org.redline_rpm.header.RpmType;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
import io.github.fvarrui.javapackager.packagers.Packager;

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
		
		LinuxPackager linuxPackager = (LinuxPackager) packager; 
		
		File appFolder = linuxPackager.getAppFolder();
		String name = linuxPackager.getName();
		String version = linuxPackager.getVersion();
		String description = linuxPackager.getDescription();
		String organizationName = linuxPackager.getOrganizationName();
		File outputDirectory = linuxPackager.getOutputDirectory();
		
		Builder builder = new Builder();
		builder.setType(RpmType.BINARY);
		builder.setPlatform(Architecture.X86_64, Os.LINUX);
		builder.setPackage(name, version, "1");
		builder.setPackager(organizationName);
		builder.setDescription(description);
		builder.setPrefixes("/opt/" + name);
		builder.addDirectory(appFolder.getAbsolutePath());
		builder.build(outputDirectory);
		
		return new File(outputDirectory, name + "_" + version + ".rpm");
	}
	
}
