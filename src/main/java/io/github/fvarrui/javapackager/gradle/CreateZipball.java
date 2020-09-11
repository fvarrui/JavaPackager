package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.UUID;

import org.gradle.api.tasks.bundling.Zip;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFunction;

public class CreateZipball implements PackagerFunction {
	
	@Override
	public File apply(Packager packager) {
		
		String name = packager.getName();
		String version = packager.getVersion();
		Platform platform = packager.getPlatform();
		File outputDirectory = packager.getOutputDirectory();
		File appFolder = packager.getAppFolder();
		File executable = packager.getExecutable();
		
		File zipFile = new File(outputDirectory, name + "-" + version + "-" + platform + ".zip");

		Zip zipExcludingBinariesTask = createZipTask();
		zipExcludingBinariesTask.setProperty("archiveName", zipFile.getName());
		zipExcludingBinariesTask.setProperty("destinationDirectory", zipFile.getParentFile());
		zipExcludingBinariesTask.from(appFolder.getParentFile());
		zipExcludingBinariesTask.include(appFolder.getName() + "/**");
		zipExcludingBinariesTask.exclude(appFolder.getName() + "/" + executable.getName(), appFolder.getName() + "/jre/bin/*");
		zipExcludingBinariesTask.getActions().forEach(action -> action.execute(zipExcludingBinariesTask));

		Zip zipIncludingBinariesTask = createZipTask();
		zipIncludingBinariesTask.setProperty("archiveName", zipFile.getName());
		zipIncludingBinariesTask.setProperty("destinationDirectory", zipFile.getParentFile());
		zipIncludingBinariesTask.from(appFolder.getParentFile());
		zipIncludingBinariesTask.exclude(appFolder.getName() + "/**");
		zipIncludingBinariesTask.include(appFolder.getName() + "/" + executable.getName(), appFolder.getName() + "/jre/bin/*");
		zipIncludingBinariesTask.setFileMode(0755);
		zipIncludingBinariesTask.getActions().forEach(action -> action.execute(zipIncludingBinariesTask));

		return zipFile;
	}
	
	private Zip createZipTask() {
		return Context.getGradleContext().getProject().getTasks().create("createZipball_" + UUID.randomUUID(), Zip.class);
	}

}
