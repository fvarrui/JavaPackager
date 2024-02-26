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

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Creates tarball (tar.gz file) on Maven context 
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
	protected File doApply(Packager packager) {
		
		File assetsFolder = packager.getAssetsFolder();
		Platform platform = packager.getPlatform();
		File outputDirectory = packager.getOutputDirectory(); 

		try {

			// generate assembly.xml file 
			File assemblyFile = new File(assetsFolder, "assembly-tarball-" + platform + ".xml");
			VelocityUtils.render(platform + "/assembly.xml.vtl", assemblyFile, packager);
			
			// output file format
			String format = "tar.gz";
			
			// invokes plugin to assemble tarball
			executeMojo(
					plugin(
							groupId("org.apache.maven.plugins"), 
							artifactId("maven-assembly-plugin"), 
							version("3.1.1")
					),
					goal("single"),
					configuration(
							element("outputDirectory", outputDirectory.getAbsolutePath()),
							element("formats", element("format", format)),
							element("descriptors", element("descriptor", assemblyFile.getAbsolutePath())),
							element("appendAssemblyId", "false")
					),
					Context.getMavenContext().getEnv()
				);

			// get generated filename
			String finalName = Context.getMavenContext().getEnv().getMavenProject().getBuild().getFinalName();
			File finalFile = new File(outputDirectory, finalName + "." + format);

			// get desired file name
			String tarName = packager.getTarballName() != null ? packager.getTarballName() : finalName + "-" + platform;
			File tarFile = new File(outputDirectory, tarName + "." + format);
			
			// rename generated to desired
			finalFile.renameTo(tarFile);
			
			return tarFile;
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
			
		}

	}

}
