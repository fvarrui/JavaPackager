package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.UUID;

import org.gradle.api.tasks.bundling.Zip;
import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.Os;

import com.netflix.gradle.plugins.rpm.Rpm;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
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
		
		LinuxPackager linuxPackager = (LinuxPackager) packager; 
		
		File appFolder = linuxPackager.getAppFolder();
		String name = linuxPackager.getName();
		String version = linuxPackager.getVersion();
		String description = linuxPackager.getDescription();
		String organizationName = linuxPackager.getOrganizationName();
		File outputDirectory = linuxPackager.getOutputDirectory();
		
		Rpm rpmTask = createTask();
		rpmTask.setPackageName(name);
		rpmTask.setPackageDescription(description);
		rpmTask.setRelease("1");
		rpmTask.setEpoch(0);
		rpmTask.setArch(Architecture.X86_64);
		rpmTask.setPackager(organizationName);
		rpmTask.setOs(Os.LINUX);
		rpmTask.into("/opt/" + name);
		rpmTask.from(appFolder);
		rpmTask.getActions().forEach(action -> action.execute(rpmTask));
		
		return new File(outputDirectory, name + "_" + version + ".rpm");
	}
	
	private Rpm createTask() {
		return Context.getGradleContext().getProject().getTasks().create("createRpm_" + UUID.randomUUID(), Rpm.class);
	}
	
}
