package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.redline_rpm.Builder;
import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.Os;
import org.redline_rpm.header.RpmType;

import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates a RPM package file including all app folder's content only for
 * GNU/Linux so app could be easily distributed on Gradle context
 */
public class GenerateRpm extends ArtifactGenerator<LinuxPackager> {

	public GenerateRpm() {
		super("RPM package");
	}

	@Override
	public boolean skip(LinuxPackager packager) {
		return !packager.getLinuxConfig().isGenerateRpm();
	}

	@Override
	protected File doApply(LinuxPackager packager) throws Exception {

		File appFolder = packager.getAppFolder();
		String name = packager.getName();
		String version = packager.getVersion().replaceAll("-", "_");
		String description = packager.getDescription();
		String organizationName = packager.getOrganizationName();
		File outputDirectory = packager.getOutputDirectory();
		File executable = packager.getExecutable();
		File assetsFolder = packager.getAssetsFolder();
		String jreDirectoryName = packager.getJreDirectoryName();
		Architecture arch = packager.getArch().toRpmArchitecture();
		File mimeXmlFile = packager.getMimeXmlFile();
		String installationPath = packager.getLinuxConfig().getInstallationPath();
		String appPath = installationPath + "/" + name;

		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, packager);
		Logger.info("Desktop file rendered in " + desktopFile.getAbsolutePath());
		
		// copies desktop file to app
		FileUtils.copyFileToFolder(desktopFile, appFolder);

		// creates RPM builder
		Builder builder = new Builder();
		builder.setType(RpmType.BINARY);
		builder.setPlatform(arch, Os.LINUX);
		builder.setPackage(name, version, "1");
		builder.setPackager(organizationName);
		builder.setDescription(description);
		builder.setPrefixes(installationPath);
		
		// list of files which needs execution permissions
		List<File> executionPermissions = new ArrayList<>();
		executionPermissions.add(executable);
		executionPermissions.add(new File(appFolder, jreDirectoryName + "/bin/java"));
		executionPermissions.add(new File(appFolder, jreDirectoryName + "/lib/jspawnhelper"));

		// add all app files
		addDirectory(builder, installationPath, appFolder, executionPermissions);

		// link to desktop file
		addLink(builder, "/usr/share/applications/" + desktopFile.getName(), appPath + "/" + desktopFile.getName());

		// copy and link to mime.xml file
		if (mimeXmlFile != null) {
			FileUtils.copyFileToFolder(mimeXmlFile, appFolder);
			addLink(builder, "/usr/share/mime/packages/" + mimeXmlFile.getName(), appPath + "/" + mimeXmlFile.getName());
		}
		
		// link to binary
		addLink(builder, "/usr/local/bin/" + executable.getName(), appPath + "/" + executable.getName());

		// add all app files
		addDirectory(builder, installationPath, appFolder, executionPermissions);
		
		// build RPM file
		builder.build(outputDirectory);

		// renames generated RPM file if created
		String suffix = "-1." + arch + ".rpm";
		File originalRpm = new File(outputDirectory, name + "-" + version + suffix);
		File rpm = null;
		if (originalRpm.exists()) {
			rpm = new File(outputDirectory, name + "_" + version + ".rpm");
			if (rpm.exists()) rpm.delete();
			FileUtils.rename(originalRpm, rpm.getName());
		}

		return rpm;
	}

	private void addLink(Builder builder, String path, String target) throws NoSuchAlgorithmException, IOException {
		Logger.info("Adding link '" + path + "' to RPM builder targeting '" + target + "'");		
		builder.addLink(path, target);
	}
	
	private void addFile(Builder builder, String rootPath, File file, int mode) throws NoSuchAlgorithmException, IOException {		
		String filePath = rootPath + "/" + file.getName();
		Logger.info("Adding file '" + file + "' to RPM builder as '" + filePath + "'");
		builder.addFile(filePath, file, mode);
	}
	
	private void addDirectory(Builder builder, String parentPath, File directory, List<File> executionPermissions) throws NoSuchAlgorithmException, IOException {
		String dirPath = parentPath + "/" + directory.getName();
		Logger.info("Adding directory '" + directory + "' to RPM builder as '" + dirPath + "'");
		builder.addDirectory(dirPath);
		for (File f : Objects.requireNonNull(directory.listFiles())) {
			if (f.isDirectory())
				addDirectory(builder, dirPath, f, executionPermissions);
			else {
				addFile(builder, dirPath, f, executionPermissions.contains(f) ? 0755 : 0644);
			}
		}
	}

}
