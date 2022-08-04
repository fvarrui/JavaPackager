package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Jar;

import io.github.fvarrui.javapackager.model.Manifest;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Creates a runnable jar file from sources on Maven context
 */
public class CreateRunnableJar extends ArtifactGenerator<Packager> {
	
	public CreateRunnableJar() {
		super("Runnable JAR");
	}
	
	@Override
	protected File doApply(Packager packager) {

		String classifier = "runnable";
		String name = packager.task.getName();
		String version = packager.task.getVersion();
		String mainClass = packager.task.getMainClass();
		File outputDirectory = packager.task.getOutputDirectory();
		Project project = Context.getGradleContext().getProject();
		File libsFolder = packager.getLibsFolder();
		Manifest manifest = packager.task.getManifest();
		
		List<String> dependencies = new ArrayList<>();
		if (libsFolder != null && libsFolder.exists()) {
			dependencies = Arrays.asList(libsFolder.listFiles()).stream().map(f -> libsFolder.getName() + "/" + f.getName()).collect(Collectors.toList());
		}
		
		Jar jarTask = (Jar) project.getTasks().findByName("jar");
		jarTask.setProperty("archiveBaseName", name);
		jarTask.setProperty("archiveVersion", version);
		jarTask.setProperty("archiveClassifier", classifier);
		jarTask.setProperty("destinationDirectory", outputDirectory);
		jarTask.getManifest().getAttributes().put("Created-By", "Gradle " + Context.getGradleContext().getProject().getGradle().getGradleVersion());
		jarTask.getManifest().getAttributes().put("Built-By", System.getProperty("user.name"));
		jarTask.getManifest().getAttributes().put("Build-Jdk", System.getProperty("java.version"));
		jarTask.getManifest().getAttributes().put("Class-Path", StringUtils.join(dependencies, " "));
		jarTask.getManifest().getAttributes().put("Main-Class", mainClass);
		if (manifest != null) {
			jarTask.getManifest().attributes(manifest.getAdditionalEntries());
			manifest.getSections().stream().forEach(s -> jarTask.getManifest().attributes(s.getEntries(), s.getName()));
		}

		jarTask.getActions().forEach(action -> action.execute(jarTask));

		return jarTask.getArchiveFile().get().getAsFile();
		
	}
	
}
