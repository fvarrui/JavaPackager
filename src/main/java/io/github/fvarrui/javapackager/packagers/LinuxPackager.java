package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import io.github.fvarrui.javapackager.PackageTask;
import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Packager for GNU/Linux 
 */
public class LinuxPackager extends Packager {
	
	private File desktopFile;
	private File mimeXmlFile = null;
	
	public LinuxPackager(PackageTask task) {
		super(task);
		installerGenerators.addAll(Context.getContext().getInstallerGenerators(Platform.linux));
	}
	
	public File getDesktopFile() {
		return desktopFile;
	}
	
	public File getMimeXmlFile() {
		return mimeXmlFile;
	}

	@Override
	public void doInit() throws Exception {

		// sets linux config default values
		task.getLinuxConfig().setDefaults(this);
		
	}
	
	@Override
	protected void doCreateAppStructure() throws Exception {

		// sets common folders
		this.executableDestinationFolder = appFolder;
		this.jarFileDestinationFolder = appFolder;
		this.jreDestinationFolder = new File(appFolder, task.getJreDirectoryName());
		this.resourcesDestinationFolder = appFolder;
	
	}
	
	/**
	 * Creates a GNU/Linux app folder with native executable
	 */	
	@Override
	public File doCreateApp() throws Exception {
		
		Logger.infoIndent("Creating GNU/Linux executable ...");

		// sets executable file
		this.executable = new File(appFolder, task.getAppName());
		
		// process classpath
		if (task.getClasspath() != null) {
			classpaths = Arrays.asList(task.getClasspath().split("[:;]"));
			if (!task.isUseResourcesAsWorkingDir()) {
				classpaths = classpaths.stream().map(cp -> new File(cp).isAbsolute() ? cp : "$SCRIPTPATH/" + cp).collect(Collectors.toList());
			}
			task.classpath(StringUtils.join(classpaths, ":"));
		}
		
		// generates desktop file from velocity template
		desktopFile = new File(assetsFolder, task.getAppName() + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, this);
		Logger.info("Rendering desktop file to " + desktopFile.getAbsolutePath());

		// generates mime.xml file from velocity template
		if (task.isThereFileAssociations()) {
			mimeXmlFile = new File(assetsFolder, task.getAppName() + ".xml");
			VelocityUtils.render("linux/mime.xml.vtl", mimeXmlFile, this);
			Logger.info("Rendering mime.xml file to " + mimeXmlFile.getAbsolutePath());
		}

		// generates startup.sh script to boot java app
		File startupFile = new File(assetsFolder, "startup.sh");
		VelocityUtils.render("linux/startup.sh.vtl", startupFile, this);
		Logger.info("Startup script generated in " + startupFile.getAbsolutePath());

		// concats linux startup.sh script + generated jar in executable (binary)
		if (task.getLinuxConfig().isWrapJar())
			FileUtils.concat(executable, startupFile, jarFile);
		else {
			FileUtils.copyFileToFile(startupFile, executable);
			FileUtils.copyFileToFolder(jarFile, appFolder);
		}

		// sets execution permissions
		executable.setExecutable(true, false);
		
		Logger.infoUnindent("GNU/Linux executable created in " + executable.getAbsolutePath() + "!");
		
		return appFolder;
	}

}