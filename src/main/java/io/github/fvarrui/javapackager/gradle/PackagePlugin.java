package io.github.fvarrui.javapackager.gradle;

import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.GradlePackageTask;
import io.github.fvarrui.javapackager.PackageTask;
import io.github.fvarrui.javapackager.packagers.Context;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.UUID;

/**
 * JavaPackager Gradle plugin
 */
public class PackagePlugin implements Plugin<Project> {

	public static final String GROUP_NAME = "JavaPackager";
	public static final String SETTINGS_EXT_NAME = "javapackager";
	public static final String PACKAGE_TASK_NAME = "package";
	public static PackageTask GLOBAL_EXTENSION;

	@Override
	public void apply(Project project) {

		Context.setContext(new GradleContext(project));
		
		project.getPluginManager().apply("java");
		project.getPluginManager().apply("edu.sc.seis.launch4j");

		GLOBAL_EXTENSION = project.getExtensions().create(SETTINGS_EXT_NAME, PackageTask.class);
		GradlePackageTask task = (GradlePackageTask) project.getTasks().create(PACKAGE_TASK_NAME, GradlePackageTask.class).dependsOn("build");
		task.getExtensions().add(SETTINGS_EXT_NAME, GLOBAL_EXTENSION);

		Context.getGradleContext().setLibraryTask(project.getTasks().create("launch4j_" + UUID.randomUUID(), Launch4jLibraryTask.class));
	}

}
