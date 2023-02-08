package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.UUID;

import org.gradle.api.tasks.bundling.Zip;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.MacPackager;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Creates zipball (zip file)  on Gradle context
 */
public class CreateZipball extends ArtifactGenerator<Packager> {
	
	public CreateZipball() {
		super("Zipball");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getCreateZipball();
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

		String zipFileName = packager.getZipballName() != null ? packager.getZipballName() : name + "-" + version + "-" + platform + ".zip";
		File zipFile = new File(outputDirectory, zipFileName);

		Zip zipTask = createZipTask();
		zipTask.setProperty("archiveFileName", zipFile.getName());
		zipTask.setProperty("destinationDirectory", outputDirectory);
		
		// if zipball is for windows platform
		if (Platform.windows.equals(platform)) {
			
			zipTask.from(appFolder.getParentFile(), copySpec -> {
				copySpec.include(appFolder.getName() + "/**");
			});
			
		}
		
		// if zipball is for linux platform
		else if (Platform.linux.equals(platform)) {
			
			zipTask.from(appFolder.getParentFile(), copySpec -> {
				copySpec.include(appFolder.getName() + "/**");
				copySpec.exclude(appFolder.getName() + "/" + executable.getName());
				copySpec.exclude(appFolder.getName() + "/" + jreDirectoryName + "/bin/*");
				copySpec.exclude(appFolder.getName() + "/scripts/*");
			});
			zipTask.from(appFolder.getParentFile(), copySpec -> {
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
			
			zipTask.from(appFolder, copySpec -> {
				copySpec.include(appFile.getName() + "/**");
				copySpec.exclude(appFile.getName() + "/Contents/MacOS/" + executable.getName());
				copySpec.exclude(appFile.getName() + "/Contents/MacOS/universalJavaApplicationStub");
				copySpec.exclude(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");
				copySpec.exclude(appFile.getName() + "/Contents/Resources/scripts/*");				
			});
			zipTask.from(appFolder, copySpec -> {
				copySpec.include(appFile.getName() + "/Contents/MacOS/" + executable.getName());
				copySpec.include(appFile.getName() + "/Contents/MacOS/universalJavaApplicationStub");
				copySpec.include(appFile.getName() + "/Contents/PlugIns/" + jreDirectoryName + "/Contents/Home/bin/*");
				copySpec.include(appFile.getName() + "/Contents/Resources/scripts/*");				
				copySpec.setFileMode(0755);
			});
			
		}
		
		zipTask.getActions().forEach(action -> action.execute(zipTask));

		return zipFile;
	}
	
	private Zip createZipTask() {
		return Context.getGradleContext().getProject().getTasks().create("createZipball_" + UUID.randomUUID(), Zip.class);
	}

}
