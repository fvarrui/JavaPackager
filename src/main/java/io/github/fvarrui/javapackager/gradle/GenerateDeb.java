package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.UUID;

import com.netflix.gradle.plugins.deb.Deb;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

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
		
		File assetsFolder = linuxPackager.getAssetsFolder();
		String name = linuxPackager.getName();
		String description = linuxPackager.getDescription();
		File appFolder = linuxPackager.getAppFolder();
		File outputDirectory = linuxPackager.getOutputDirectory();
		String version = linuxPackager.getVersion();
		boolean bundleJre = linuxPackager.getBundleJre();
		String jreDirectoryName = linuxPackager.getJreDirectoryName();
		File executable = linuxPackager.getExecutable();
		String organizationName = linuxPackager.getOrganizationName();
		String organizationEmail = linuxPackager.getOrganizationEmail();

		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, linuxPackager);
		Logger.info("Desktop file rendered in " + desktopFile.getAbsolutePath());

		// generates deb control file from velocity template
		File controlFile = new File(assetsFolder, "control");
		VelocityUtils.render("linux/control.vtl", controlFile, linuxPackager);
		Logger.info("Control file rendered in " + controlFile.getAbsolutePath());

		// generated deb file
		File debFile = new File(outputDirectory, name + "_" + version + ".deb");

		Deb debTask = createDebTask();
		debTask.setProperty("archiveFileName", debFile.getName());
		debTask.setProperty("destinationDirectory", outputDirectory);
		debTask.setPackageName(name);
		debTask.setPackageDescription(description);
		debTask.setPackager(organizationName);
		debTask.setMaintainer(organizationName + (organizationEmail != null ? " <" + organizationEmail + ">" : ""));
		debTask.setPriority("optional");
		debTask.setArchStr("amd64");
		debTask.setDistribution("development");
		
		// installation destination
		debTask.into("/opt/" + name);
		
		// includes app folder files, except executable file and jre/bin/java
		debTask.from(appFolder, c -> {
			c.into(".");
			c.exclude(executable.getName());
			if (bundleJre) {
				c.exclude(jreDirectoryName + "/bin/java");
			}
		});
		
		// executable
		debTask.from(executable, c -> {
			c.into(".");
			c.setFileMode(0755);
		});

		// java binary file
		if (bundleJre) {
			debTask.from(new File(appFolder, jreDirectoryName + "/bin/java"), c -> {
				c.into(".");
				c.setFileMode(0755);
			});
		}

		// desktop file
		debTask.from(desktopFile, c -> {
			c.into("/usr/share/applications");
		});

		// symbolic link in /usr/local/bin to app binary
		debTask.link("/usr/local/bin/" + name, "/opt/" + name + "/" + name, 0777);

		return debFile;

	}
	
	private Deb createDebTask() {
		return Context.getGradleContext().getProject().getTasks().create("createDeb_" + UUID.randomUUID(), Deb.class);
	}
	
}
