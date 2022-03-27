package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
		
		// generates desktop file from velocity template
		File desktopFile = new File(assetsFolder, name + ".desktop");
		VelocityUtils.render("linux/desktop.vtl", desktopFile, packager);
		Logger.info("Rendering desktop file to " + desktopFile.getAbsolutePath());
		
		// copies desktop file to app
		FileUtils.copyFileToFolder(desktopFile, appFolder);

		Builder builder = new Builder();
		builder.setType(RpmType.BINARY);
		builder.setPlatform(Architecture.X86_64, Os.LINUX);
		builder.setPackage(name, version, "1");
		builder.setPackager(organizationName);
		builder.setDescription(description);
		builder.setPrefixes("opt");
		
		// list of files which needs execution permissions
		List<File> executionPermissions = new ArrayList<>();
		executionPermissions.add(executable);
		executionPermissions.add(new File(appFolder, jreDirectoryName + "/bin/java"));
		executionPermissions.add(new File(appFolder, jreDirectoryName + "/lib/jspawnhelper"));

		// add all app files
		addDirectoryTree(builder, "/opt", appFolder, executionPermissions);

		// link to desktop file
		builder.addLink("/usr/share/applications/" + desktopFile.getName(), "/opt/" + name + "/" + desktopFile.getName());

		// link to binary
		builder.addLink("/usr/local/bin/" + executable.getName(), "/opt/" + name + "/" + executable.getName());

		builder.build(outputDirectory);

		File originalRpm = new File(outputDirectory, name + "-" + version + "-1.x86_64.rpm");
		File rpm = null;
		if (originalRpm.exists()) {
			rpm = new File(outputDirectory, name + "_" + version + ".rpm");
			if (rpm.exists()) rpm.delete();
			FileUtils.rename(originalRpm, rpm.getName());
		}

		return rpm;
	}
	
	private void addDirectoryTree(Builder builder, String parentPath, File root, List<File> executionPermissions) throws NoSuchAlgorithmException, IOException {
		String rootPath = parentPath + "/" + root.getName();
		builder.addDirectory(rootPath);
		for (File f : root.listFiles()) {
			if (f.isDirectory())
				addDirectoryTree(builder, parentPath + "/" + root.getName(), f, executionPermissions);
			else {
				builder.addFile(rootPath + "/" + f.getName(), f, executionPermissions.contains(f) ? 0755 : 0644);
			}
		}
	}

}
