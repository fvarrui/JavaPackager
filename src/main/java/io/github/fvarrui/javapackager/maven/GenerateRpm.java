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

import org.apache.commons.io.FilenameUtils;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a RPM package file including all app folder's content only for 
 * GNU/Linux so app could be easily distributed on Maven context
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

		String name = linuxPackager.getName();
		File appFolder = linuxPackager.getAppFolder();
		File iconFile = linuxPackager.getIconFile();
		File outputDirectory = linuxPackager.getOutputDirectory();
		String version = linuxPackager.getVersion();
		boolean bundleJre = linuxPackager.getBundleJre();
		String jreDirectoryName = linuxPackager.getJreDirectoryName();
		String organizationName = linuxPackager.getOrganizationName();
		File desktopFile = linuxPackager.getDesktopFile();
		File mimeXmlFile = linuxPackager.getMimeXmlFile();
		
		// copies desktop file to app
		FileUtils.copyFileToFolder(desktopFile, appFolder);

		// determines xpm icon file location or takes default one
		File xpmIcon = new File(iconFile.getParentFile(), FilenameUtils.removeExtension(iconFile.getName()) + ".xpm");
		if (!xpmIcon.exists()) {
			FileUtils.copyResourceToFile("/linux/default-icon.xpm", xpmIcon);
		}

		// generated rpm file
		File rpmFile = new File(outputDirectory, name + "_" + version + ".rpm");
		
		// creates plugin config
		
		List<Element> includes = new ArrayList<>();
		includes.add(element("include", name));

		List<Element> excludes = new ArrayList<>();
		excludes.add(element("exclude", name));
		
		if (bundleJre) {
			includes.add(element("include", jreDirectoryName + "/bin/java"));
			excludes.add(element("exclude", jreDirectoryName + "/bin/java"));
		}
		
		// creates mappings
		List<Element> mappings = new ArrayList<>();
		
		/* app folder files, except executable file and jre/bin/java */
		mappings.add(
			element("mapping", 
				element("directory", "/opt/" + name),
				element("sources", 
						element("source", 
								element("location", appFolder.getAbsolutePath()),
								element("excludes", excludes.toArray(new Element[0]))
						)
				)
			)
		);

		/* app executable and java binary file */
		mappings.add(
			element("mapping", 
					element("directory", "/opt/" + name),
					element("filemode", "755"),
					element("sources",
							element("source", 
									element("location", appFolder.getAbsolutePath()),
									element("includes", includes.toArray(new Element[0]))
							)
					)
			)
		);
		
		/* desktop file */
		mappings.add(
			element("mapping", 
					element("directory", "/usr/share/applications"),
					element("sources",
							element("source", 
									element("location", desktopFile.getName())
							)
					)
			)
		);
		
		/* mime types file */
		if (packager.isThereFileAssociations()) {
			mappings.add(
				element("mapping", 
						element("directory", "/usr/share/mime/applications"),
						element("sources",
								element("source", 
										element("location", mimeXmlFile.getName())
								)
						)
				)
			);
		}
		
		/* symbolic link in /usr/local/bin to app binary */
		mappings.add(
			element("mapping", 
					element("directory", "/usr/local/bin"),
					element("sources", 
							element("softlinkSource", 
									element("location", "/opt/" + name + "/" + name)
							)
					)
			)
		);
		
		// invokes plugin to generate deb package
		executeMojo(
				plugin(
						groupId("org.codehaus.mojo"), 
						artifactId("rpm-maven-plugin"), 
						version("2.2.0")
				),
				goal("rpm"), 
				configuration(
						element("packager", organizationName),
						element("group", "Application"),
						element("icon", xpmIcon.getAbsolutePath()),
						element("autoRequires", "false"),
						element("needarch", "true"),
						element("defaultDirmode", "755"),
						element("defaultFilemode", "644"),
						element("defaultUsername", "root"),
						element("defaultGroupname", "root"),
						element("copyTo", rpmFile.getAbsolutePath()),
						element("mappings", mappings.toArray(new Element[0])
						)
				),
				Context.getMavenContext().getEnv()
			);

		Logger.infoUnindent("RPM package generated! " + rpmFile.getAbsolutePath());

		return rpmFile;
	}
	
}
