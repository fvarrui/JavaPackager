package io.github.fvarrui.javapackager.gradle;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

import io.github.fvarrui.javapackager.packagers.ArtifactGenerator;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Copies all dependencies to app folder on Gradle context
 */
public class CopyDependencies extends ArtifactGenerator<Packager> {
	
	public Copy copyLibsTask;
	
	public CopyDependencies() {
		super("Libs folder");
	}
	
	@Override
	public boolean skip(Packager packager) {
		return !packager.getCopyDependencies();
	}
	
	@Override
	protected File doApply(Packager packager) {
		
		File libsFolder = new File(packager.getJarFileDestinationFolder(), "libs");
		Project project = Context.getGradleContext().getProject();
	
		copyLibsTask = (Copy) project.getTasks().findByName("copyLibs");
		if (copyLibsTask == null) {
			copyLibsTask = project.getTasks().create("copyLibs", Copy.class);
		}
		copyLibsTask.setDuplicatesStrategy(Context.getGradleContext().getDuplicatesStrategy());
		copyLibsTask.from(project.getConfigurations().getByName("runtimeClasspath"));
		copyLibsTask.into(project.file(libsFolder));
		copyLibsTask.getActions().forEach(action -> action.execute(copyLibsTask));
		
		return libsFolder;
	}

}
