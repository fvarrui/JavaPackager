package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Jar;

import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFunction;

/**
 * Creates a runnable jar file from sources
 */
public class CreateRunnableJar implements PackagerFunction {
	
	@Override
	public File apply(Packager packager) {

		String classifier = "runnable";
		String name = packager.getName();
		String version = packager.getVersion();
		String mainClass = packager.getMainClass();
		File outputDirectory = packager.getOutputDirectory();
		Project project = Context.getGradleContext().getProject();
		File libsFolder = packager.getLibsFolder();
		
		List<String> dependencies = new ArrayList<>();
		if (libsFolder != null && libsFolder.exists()) {
			dependencies = Arrays.asList(libsFolder.listFiles()).stream().map(f -> libsFolder.getName() + "/" + f.getName()).collect(Collectors.toList());
		}
		
		Jar jarTask = (Jar) project.getTasks().findByName("jar");
		jarTask.setProperty("archiveBaseName", name);
		jarTask.setProperty("archiveVersion", version);
		jarTask.setProperty("archiveClassifier", classifier);
		jarTask.setProperty("destinationDirectory", outputDirectory);
		jarTask.getManifest().getAttributes().put("Main-Class", mainClass);
		jarTask.getManifest().getAttributes().put("Class-Path", StringUtils.join(dependencies, " "));
		jarTask.getActions().forEach(action -> action.execute(jarTask));

		return jarTask.getArchiveFile().get().getAsFile();
		
	}
	
}
