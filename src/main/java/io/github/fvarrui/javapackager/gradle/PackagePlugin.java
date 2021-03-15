package io.github.fvarrui.javapackager.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

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
		
		project.getExtensions().create(SETTINGS_EXT_NAME, PackagePluginExtension.class, project);
		project.getTasks().create(PACKAGE_TASK_NAME, DefaultPackageTask.class).dependsOn("build");

	}

}
