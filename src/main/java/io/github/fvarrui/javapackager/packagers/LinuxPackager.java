package io.github.fvarrui.javapackager.packagers;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;

import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public class LinuxPackager extends Packager {
	
	/**
	 * Creates a RPM package file including all app folder's content only for 
	 * GNU/Linux so app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private File generateRpmPackage() throws MojoExecutionException {

		Logger.info("Generating RPM package...");

		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, this);
		FileUtils.copyFileToFolder(desktopFile, appFolder);

		// determines xpm icon file location or takes default one
		File xpmIcon = new File(iconFile.getParentFile(), FilenameUtils.removeExtension(iconFile.getName()) + ".xpm");
		if (!xpmIcon.exists()) {
			FileUtils.copyResourceToFile("/linux/default-icon.xpm", xpmIcon);
		}

		// generated rpm file
		File rpmFile = new File(outputDirectory, name + "_" + version + ".rpm");
		
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
														element("excludes", 
																element("exclude", name),
																element("exclude", jreDirectoryName + "/bin/java")
														)
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
														element("includes", 
																element("include", name),
																element("include", jreDirectoryName + "/bin/java")
														)
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
				env);

		Logger.info("RPM package generated! " + rpmFile.getAbsolutePath());

		return rpmFile;
	}
	
	/**
	 * Creates a DEB package file including all app folder's content only for 
	 * GNU/Linux so app could be easily distributed
	 * 
	 * @throws MojoExecutionException
	 */
	private File generateDebPackage() throws MojoExecutionException {

		Logger.info("Generating DEB package ...");

		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, this);

		// generates deb control file from velocity template
		File controlFile = new File(assetsFolder, "control");
		VelocityUtils.render("linux/control.vtl", controlFile, this);

		// generated deb file
		File debFile = new File(outputDirectory, name + "_" + version + ".deb");

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
						element("dataSet",
								/* app folder files, except executable file and jre/bin/java */
								element("data", 
										element("type", "directory"),
										element("src", appFolder.getAbsolutePath()),
										element("mapper", 
												element("type", "perm"),
												element("prefix", "/opt/" + name)
										),
										element("excludes", executable.getName() + "," + "jre/bin/java")
								),
								/* executable */
								element("data", 
										element("type", "file"),
										element("src", appFolder.getAbsolutePath() + "/" + name),
										element("mapper", 
												element("type", "perm"), 
												element("filemode", "755"),
												element("prefix", "/opt/" + name)
										)
								),
								/* desktop file */
								element("data", 
										element("type", "file"),
										element("src", desktopFile.getAbsolutePath()),
										element("mapper", 
												element("type", "perm"),
												element("prefix", "/usr/share/applications")
										)
								),
								/* java binary file */
								element("data", 
										element("type", "file"),
										element("src", appFolder.getAbsolutePath() + "/jre/bin/java"),
										element("mapper", 
												element("type", "perm"), 
												element("filemode", "755"),
												element("prefix", "/opt/" + name + "/jre/bin")
										)
								),
								/* symbolic link in /usr/local/bin to app binary */
								element("data", 
										element("type", "link"),
										element("linkTarget", "/opt/" + name + "/" + name),
										element("linkName", "/usr/local/bin/" + name),
										element("symlink", "true"), 
										element("mapper", 
												element("type", "perm"),
												element("filemode", "777")
										)
								)
						)
				),
				env);
		
		Logger.info("DEB package generated! " + debFile.getAbsolutePath());
		
		return debFile;
	}


	/**
	 * Creates a GNU/Linux app file structure with native executable
	 * 
	 * @throws MojoExecutionException
	 */	
	@Override
	public File doCreateApp() throws MojoExecutionException {
		
		Logger.append("Creating GNU/Linux executable ...");

		// generates startup.sh script to boot java app
		File startupFile = new File(assetsFolder, "startup.sh");
		VelocityUtils.render("linux/startup.sh.vtl", startupFile, this);
		Logger.info("Startup script generated in " + startupFile.getAbsolutePath());

		// concats linux startup.sh script + generated jar in executable (binary)
		FileUtils.concat(executable, startupFile, jarFile);

		// sets execution permissions
		executable.setExecutable(true, false);
		
		Logger.subtract("GNU/Linux executable created in " + executable.getAbsolutePath() + "!");
		
		return appFolder;
	}

	@Override
	public void doGenerateInstallers(List<File> installers) throws MojoExecutionException {

		if (linuxConfig.isGenerateDeb()) {
			File debFile = generateDebPackage();
			installers.add(debFile);			
		}
		
		if (linuxConfig.isGenerateRpm()) {
			File rpmFile = generateRpmPackage();
			installers.add(rpmFile);
		}
		
	}

	@Override
	protected void createSpecificAppStructure() throws MojoExecutionException {

		this.executableDestinationFolder = appFolder;
		this.jarFileDestinationFolder = appFolder;
		this.jreDestinationFolder = new File(appFolder, jreDirectoryName);
		this.resourcesDestinationFolder = appFolder;
		
	}
	
}
