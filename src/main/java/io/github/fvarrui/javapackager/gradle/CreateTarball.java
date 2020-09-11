package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Tar;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFunction;

public class CreateTarball implements PackagerFunction {
	
	private Tar tarTask;

	@Override
	public File apply(Packager packager) {
		
		String name = packager.getName();
		String version = packager.getVersion();
		Platform platform = packager.getPlatform();
		File outputDirectory = packager.getOutputDirectory();
		File appFolder = packager.getAppFolder();
		Project project = Context.getGradleContext().getProject();
		
		File zipFile = new File(outputDirectory, name + "-" + version + "-" + platform + ".tar.gz");

		tarTask = (Tar) project.getTasks().findByName("createTarball");
		if (tarTask == null) {
			tarTask = project.getTasks().create("createTarball", Tar.class);
		}
		tarTask.from(appFolder);
		tarTask.include("*", "*/*");
		tarTask.setProperty("archiveName", zipFile.getName());
		tarTask.setProperty("destinationDir", zipFile.getParentFile());		
		tarTask.getActions().forEach(action -> action.execute(tarTask));

		return zipFile;
	}

}
