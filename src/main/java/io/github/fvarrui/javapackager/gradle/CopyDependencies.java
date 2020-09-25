package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;

/**
 * Copies all dependencies to app folder on Maven context
 */
public class CopyDependencies extends ArtifactGenerator {
	
	public Copy copyLibsTask;
	
	public CopyDependencies() {
		super("Dependencies");
	}
	
	@Override
	public File apply(Packager packager) {
		
		File libsFolder = new File(packager.getJarFileDestinationFolder(), "libs");
		Project project = Context.getGradleContext().getProject();
	
		copyLibsTask = (Copy) project.getTasks().findByName("copyLibs");
		if (copyLibsTask == null) {
			copyLibsTask = project.getTasks().create("copyLibs", Copy.class);
		}
		copyLibsTask.from(project.getConfigurations().getByName("default"));
		copyLibsTask.into(project.file(libsFolder));
		copyLibsTask.getActions().forEach(action -> action.execute(copyLibsTask));
		
		return libsFolder;
	}

}
