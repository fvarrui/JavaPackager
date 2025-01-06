package io.github.fvarrui.javapackager.gradle;

import java.util.UUID;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask;
import io.github.fvarrui.javapackager.packagers.Context;

/**
 * JavaPackager Gradle plugin
 */
public class PackagePlugin implements Plugin<Project> {

	public static final String GROUP_NAME = "JavaPackager";
	public static final String SETTINGS_EXT_NAME = "javapackager";
	public static final String PACKAGE_TASK_NAME = "package";	

	@Override
	public void apply(Project project) {
		Context.setContext(new GradleContext(project));
		
		project.getPluginManager().apply("java");
		project.getPluginManager().apply("edu.sc.seis.launch4j");		
		
		PackagePluginExtension extension = project.getExtensions().create(SETTINGS_EXT_NAME, PackagePluginExtension.class, project);
		project.getTasks().create(PACKAGE_TASK_NAME, PackageTask.class).dependsOn("build");

		Context.getGradleContext().setLibraryTask(project.getTasks().create("launch4j_" + UUID.randomUUID(), Launch4jLibraryTask.class));

		Context.getGradleContext().setPackagePluginExtension(extension);

		// Pass along custom gradle types set in the extension to the task in this manor so that gradle's automatic
		// task dependency magic can occur, eg. you can specify a task or configuration as an input and gradle will
		// run it for us
		project.afterEvaluate(p -> {
            project.getTasks().withType(PackageTask.class, packageTask -> {
				if (packageTask.getAdditionalResourceCollection() == null) {
					packageTask.setAdditionalResourceCollection(extension.getAdditionalResourceCollection());
				}

				if (packageTask.getRunnableJar() == null &&
						packageTask.getRunnableJarSource() == null && extension.getRunnableJar() == null) {
					packageTask.setRunnableJar(extension.getRunnableJarSource());
				}
			});
		});

	}

}
