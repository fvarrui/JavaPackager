package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.UUID;

import org.gradle.api.tasks.bundling.Compression;
import org.gradle.api.tasks.bundling.Tar;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.MacPackager;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Creates tarball (tar.gz file) on Gradle context 
 */
public class CreateTarball extends ArtifactGenerator<Packager> {
	
	public CreateTarball() {
		super("Tarball");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getCreateTarball();
	}
	
	@Override
	protected File doApply(Packager packager) throws Exception {
		
		String name = packager.getName();
		String version = packager.getVersion();
		Platform platform = packager.getPlatform();
		File outputDirectory = packager.getOutputDirectory();
		File appFolder = packager.getAppFolder();
		File executable = packager.getExecutable();
		String jreDirectoryName = packager.getJreDirectoryName();

		// tgz file name
		String finalName = packager.getTarballName() != null ? packager.getTarballName() : name + "-" + version + "-" + platform;
		String format = ".tar.gz";
		File tarFile = new File(outputDirectory, finalName + format);

		Tar tarTask = createTarTask();
		tarTask.setProperty("archiveFileName", tarFile.getName());
		tarTask.setProperty("destinationDirectory", outputDirectory);
		tarTask.setCompression(Compression.GZIP);
		
		// if zipball is for windows platform
		if (Platform.windows.equals(platform)) {
			
			tarTask.from(appFolder.getParentFile(), copySpec -> {
				copySpec.include(appFolder.getName() + "/**");
			});
			
		}
		
		// if zipball is for linux platform
		else if (Platform.linux.equals(platform)) {
			
			tarTask.from(appFolder.getParentFile(), copySpec -> {
				copySpec.include(appFolder.getName() + "/**");
				copySpec.exclude(appFolder.getName() + "/" + executable.getName());
				copySpec.exclude(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
				copySpec.exclude(appFolder.getName() + "/scripts/*");
			});
			tarTask.from(appFolder.getParentFile(), copySpec -> {
				copySpec.include(appFolder.getName() + "/" + executable.getName());
				copySpec.include(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
				copySpec.include(appFolder.getName() + "/scripts/*");
				copySpec.setFileMode(0755);
			});
			
		}
		
		// if zipball is for macos platform
		else if (Platform.mac.equals(platform)) {
			
			MacPackager macPackager = (MacPackager) packager;
			File appFile = macPackager.getAppFile();
			
			tarTask.from(appFolder, copySpec -> {
				copySpec.include(appFile.getName() + "/**");
				copySpec.exclude(appFile.getName() + "/Contents/MacOS/" + executable.getName());
				copySpec.exclude(appFile.getName() + "/Contents/MacOS/universalJavaApplicationStub");
				copySpec.exclude(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");
				copySpec.exclude(appFile.getName() + "/Contents/Resources/scripts/*");
				
			});
			tarTask.from(appFolder, copySpec -> {
				copySpec.include(appFile.getName() + "/Contents/MacOS/" + executable.getName());
				copySpec.include(appFile.getName() + "/Contents/MacOS/universalJavaApplicationStub");
				copySpec.include(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");
				copySpec.include(appFile.getName() + "/Contents/Resources/scripts/*");
				copySpec.setFileMode(0755);
			});
			
		}
		
		tarTask.getActions().forEach(action -> action.execute(tarTask));

		return tarFile;
	}
	
	private Tar createTarTask() {
		return Context.getGradleContext().getProject().getTasks().create("createTarball_" + UUID.randomUUID(), Tar.class);
	}

}
