package io.github.fvarrui.javapackager.maven;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.model.Manifest;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.MojoExecutorUtils;

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
		String name = packager.getName();
		String version = packager.getVersion();
		String mainClass = packager.getMainClass();
		File outputDirectory = packager.getOutputDirectory();
		ExecutionEnvironment env = Context.getMavenContext().getEnv();
		Manifest manifest = packager.getManifest();
		
		List<Element> archive = new ArrayList<>();
		archive.add(
			element("manifest", 
				element("addClasspath", "true"),
				element("classpathPrefix", "libs/"),
				element("mainClass", mainClass),
				element("useUniqueVersions", "false")
			)
		);
		if (manifest != null) {
			
			archive.add(MojoExecutorUtils.mapToElement("manifestEntries", manifest.getAdditionalEntries()));

			List<Element> manifestSections = 
					manifest
						.getSections()
						.stream()
						.map(s -> element("manifestSection", 
								element("Name", s.getName()),
								MojoExecutorUtils.mapToElement("manifestEntries", s.getEntries())
							))
						.collect(Collectors.toList());
			
			archive.add(element("manifestSections", manifestSections.toArray(new Element[manifestSections.size()])));
			
		}

		try {
			
			executeMojo(
					plugin(
							groupId("org.apache.maven.plugins"),
							artifactId("maven-jar-plugin"), 
							version("3.3.0")
					),
					goal("jar"),
					configuration(
							element("classifier", classifier),
							element("archive", archive.toArray(new Element[archive.size()])),
							element("outputDirectory", outputDirectory.getAbsolutePath())
					),
					env
			);
			
		} catch (MojoExecutionException e) {

			Logger.error("Runnable jar creation failed! " + e.getMessage());
			throw new RuntimeException(e);
			
		}
		
		// gets build.finalName value
		String finalName = Context.getMavenContext().getEnv().getMavenProject().getBuild().getFinalName();
		
		// creates file pointing to generated jar file
		File finalJarFile = new File(outputDirectory, finalName + "-" + classifier + ".jar");
		
		// creates desired output jar file 
		File jarFile = new File(outputDirectory, name + "-" + version + "-" + classifier + ".jar");
		
		// renames generated jar to desired one if they are different
		if (!finalJarFile.equals(jarFile)) {
			FileUtils.rename(finalJarFile, jarFile.getName());
		}
		
		return jarFile;
		
	}
	
}
