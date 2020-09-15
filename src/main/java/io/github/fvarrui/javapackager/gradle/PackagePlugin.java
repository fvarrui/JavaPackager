package io.github.fvarrui.javapackager.gradle;

import javax.inject.Inject;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.internal.impldep.org.eclipse.jgit.annotations.NonNull;
import org.gradle.internal.reflect.Instantiator;

import io.github.fvarrui.javapackager.packagers.Context;

public class PackagePlugin implements Plugin<Project> {

	public static final String GROUP_NAME = "JavaPackager";
	public static final String SETTINGS_EXT_NAME = "javaPackager";

	@NonNull final Instantiator instantiator;

    @Inject
    public PackagePlugin(@NonNull Instantiator instantiator) {
        this.instantiator = instantiator;
    }

	@Override
	public void apply(Project project) {

		Context.setContext(new GradleContext(project));
		
		project.getPluginManager().apply("java");
		project.getPluginManager().apply("edu.sc.seis.launch4j");		
		
//		project.getExtensions().create(SETTINGS_EXT_NAME, PackagePluginExtension.class, instantiator, project);
//		project.getTasks().create(PackageTask.PACKAGE_TASK_NAME, PackageTask.class).dependsOn("build");

	}

}
