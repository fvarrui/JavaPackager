package io.github.fvarrui.javapackager.maven;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates a DEB package file including all app folder's content only for 
 * GNU/Linux so app could be easily distributed on Maven context
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

		File assetsFolder = linuxPackager.getAssetsFolder();
		String name = linuxPackager.getName();
		File appFolder = linuxPackager.getAppFolder();
		File outputDirectory = linuxPackager.getOutputDirectory();
		String version = linuxPackager.getVersion();
		boolean bundleJre = linuxPackager.getBundleJre();
		String jreDirectoryName = linuxPackager.getJreDirectoryName();
		File executable = linuxPackager.getExecutable();

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
		
		// creates plugin config
		List<Element> dataSet = new ArrayList<>();
		
		/* app folder files, except executable file and jre/bin/java */
		dataSet.add(element("data", 
				element("type", "directory"),
				element("src", appFolder.getAbsolutePath()),
				element("mapper", 
						element("type", "perm"),
						element("prefix", "/opt/" + name)
				),
				element("excludes", executable.getName() + (bundleJre ? "," + jreDirectoryName + "/bin/java" : ""))
		));
		
		/* executable */
		dataSet.add(element("data", 
				element("type", "file"),
				element("src", appFolder.getAbsolutePath() + "/" + name),
				element("mapper", 
						element("type", "perm"), 
						element("filemode", "755"),
						element("prefix", "/opt/" + name)
				)
		));
		
		/* desktop file */
		dataSet.add(element("data", 
				element("type", "file"),
				element("src", desktopFile.getAbsolutePath()),
				element("mapper", 
						element("type", "perm"),
						element("prefix", "/usr/share/applications")
				)
		));
		
		/* java binary file */
		if (bundleJre)
			dataSet.add(element("data", 
					element("type", "file"),
					element("src", appFolder.getAbsolutePath() + "/" + jreDirectoryName + "/bin/java"),
					element("mapper", 
							element("type", "perm"), 
							element("filemode", "755"),
							element("prefix", "/opt/" + name + "/" + jreDirectoryName + "/bin")
					)
			));
		
		/* symbolic link in /usr/local/bin to app binary */
		dataSet.add(element("data", 
				element("type", "link"),
				element("linkTarget", "/opt/" + name + "/" + name),
				element("linkName", "/usr/local/bin/" + name),
				element("symlink", "true"), 
				element("mapper", 
						element("type", "perm"),
						element("filemode", "777")
				)
		));
		
		// invokes plugin to generate deb package
		executeMojo(
				plugin(
						groupId("org.vafer"), 
						artifactId("jdeb"), 
						version("1.8")
				), 
				goal("jdeb"), 
				configuration(
						element("controlDir", controlFile.getParentFile().getAbsolutePath()),
						element("deb", outputDirectory.getAbsolutePath() + "/" + debFile.getName()),
						element("dataSet", dataSet.toArray(new Element[dataSet.size()]))
				),
				Context.getMavenContext().getEnv()
			);
		
		return debFile;
	}
	
}
