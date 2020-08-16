package io.github.fvarrui.javapackager.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PackagePlugin implements Plugin<Project> {
	
	public static final String GROUP_NAME = "JavaPackager";
	public static final String SETTINGS_EXT_NAME = "javaPackagerSettings";
	public static final String TASK_NAME = "package";

	@Override
	public void apply(Project project) {
		project.getExtensions().create(SETTINGS_EXT_NAME, PackagePluginExtension.class);
        project.getTasks().create(TASK_NAME, PackageTask.class);
	}
	
}
