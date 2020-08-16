package io.github.fvarrui.javapackager.packagers;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import static org.apache.commons.collections4.CollectionUtils.addIgnoreNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import io.github.fvarrui.javapackager.maven.MavenContext;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public class LinuxPackager extends Packager {
	

	@Override
	public void doInit() throws Exception {

		// sets linux config default values
		this.linuxConfig.setDefaults(this);
		
	}
	
	@Override
	protected void doCreateAppStructure() throws Exception {

		// sets common folders
		this.executableDestinationFolder = appFolder;
		this.jarFileDestinationFolder = appFolder;
		this.jreDestinationFolder = new File(appFolder, jreDirectoryName);
		this.resourcesDestinationFolder = appFolder;
	
	}
	
	/**
	 * Creates a GNU/Linux app folder with native executable
	 */	
	@Override
	public File doCreateApp() throws Exception {
		
		Logger.infoIndent("Creating GNU/Linux executable ...");

		// sets executable file
		this.executable = new File(appFolder, name);
		
		// generates startup.sh script to boot java app
		File startupFile = new File(assetsFolder, "startup.sh");
		VelocityUtils.render("linux/startup.sh.vtl", startupFile, this);
		Logger.info("Startup script generated in " + startupFile.getAbsolutePath());

		// concats linux startup.sh script + generated jar in executable (binary)
		FileUtils.concat(executable, startupFile, jarFile);

		// sets execution permissions
		executable.setExecutable(true, false);
		
		Logger.infoUnindent("GNU/Linux executable created in " + executable.getAbsolutePath() + "!");
		
		return appFolder;
	}

	@Override
	public void doGenerateInstallers(List<File> installers) throws Exception {

		addIgnoreNull(installers, generateDebPackage());

		addIgnoreNull(installers, generateRpmPackage());
		
	}

	/**
	 * Creates a RPM package file including all app folder's content only for 
	 * GNU/Linux so app could be easily distributed
	 */
	private File generateRpmPackage() throws Exception {
		if (!linuxConfig.isGenerateRpm()) return null;

		Logger.infoIndent("Generating RPM package...");

		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, this);
		Logger.info("Rendering desktop file to " + desktopFile.getAbsolutePath());
		
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
		
		// invokes plugin to generate deb package
		executeMojo(
				plugin(
						groupId("org.codehaus.mojo"), 
						artifactId("rpm-maven-plugin"), 
						version("2.2.0")
				),
				goal("rpm"), 
				configuration(
						element("license", getLicenseName()),
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
						element("mappings",
								/* app folder files, except executable file and jre/bin/java */
								element("mapping", 
										element("directory", "/opt/" + name),
										element("sources", 
												element("source", 
														element("location", appFolder.getAbsolutePath()),
														element("excludes", excludes.toArray(new Element[excludes.size()]))
												)
										)
								),
								/* app executable and java binary file */
								element("mapping", 
										element("directory", "/opt/" + name),
										element("filemode", "755"),
										element("sources",
												element("source", 
														element("location", appFolder.getAbsolutePath()),
														element("includes", includes.toArray(new Element[includes.size()]))
												)
										)
								),
								/* desktop file */
								element("mapping", 
										element("directory", "/usr/share/applications"),
										element("sources",
												element("softlinkSource", 
														element("location", "/opt/" + name + "/" + desktopFile.getName())
												)
										)
								),
								/* symbolic link in /usr/local/bin to app binary */
								element("mapping", 
										element("directory", "/usr/local/bin"),
										element("sources", 
												element("softlinkSource", 
														element("location", "/opt/" + name + "/" + name)
												)
										)
								)
						)
				),
				MavenContext.getEnv());

		Logger.infoUnindent("RPM package generated! " + rpmFile.getAbsolutePath());

		return rpmFile;
	}
	
	/**
	 * Creates a DEB package file including all app folder's content only for 
	 * GNU/Linux so app could be easily distributed
	 */
	private File generateDebPackage() throws Exception {
		if (!linuxConfig.isGenerateDeb()) return null;

		Logger.infoIndent("Generating DEB package ...");

		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, this);
		Logger.info("Desktop file rendered in " + desktopFile.getAbsolutePath());

		// generates deb control file from velocity template
		File controlFile = new File(assetsFolder, "control");
		VelocityUtils.render("linux/control.vtl", controlFile, this);
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
						version("1.7")
				), 
				goal("jdeb"), 
				configuration(
						element("controlDir", controlFile.getParentFile().getAbsolutePath()),
						element("deb", outputDirectory.getAbsolutePath() + "/" + debFile.getName()),
						element("dataSet", dataSet.toArray(new Element[dataSet.size()]))
				),
				MavenContext.getEnv());
		
		Logger.infoUnindent("DEB package generated! " + debFile.getAbsolutePath());
		
		return debFile;
	}
	
}