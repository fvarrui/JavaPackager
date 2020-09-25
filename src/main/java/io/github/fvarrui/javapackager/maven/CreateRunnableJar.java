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

import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a runnable jar file from sources on Maven context
 */
public class CreateRunnableJar extends ArtifactGenerator {
	
	public CreateRunnableJar() {
		super("Runnable JAR");
	}
	
	@Override
	public File apply(Packager packager) {
		
		String classifier = "runnable";
		String name = packager.getName();
		String version = packager.getVersion();
		String mainClass = packager.getMainClass();
		File outputDirectory = packager.getOutputDirectory();
		ExecutionEnvironment env = Context.getMavenContext().getEnv();

		File jarFile = new File(outputDirectory, name + "-" + version + "-" + classifier + ".jar");

		try {
			
			executeMojo(
					plugin(
							groupId("org.apache.maven.plugins"),
							artifactId("maven-jar-plugin"), 
							version("3.1.1")
					),
					goal("jar"),
					configuration(
							element("classifier", classifier),
							element("archive", 
									element("manifest", 
											element("addClasspath", "true"),
											element("classpathPrefix", "libs/"),
											element("mainClass", mainClass)
									)
							),
							element("outputDirectory", jarFile.getParentFile().getAbsolutePath()),
							element("finalName", name + "-" + version)
					),
					env);

		} catch (MojoExecutionException e) {

			Logger.error("Runnable jar creation failed! " + e.getMessage());
			throw new RuntimeException(e);
			
		}
		
		return jarFile;
	}
	
}
