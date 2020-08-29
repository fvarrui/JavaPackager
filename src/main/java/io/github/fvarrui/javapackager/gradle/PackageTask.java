package io.github.fvarrui.javapackager.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;

import io.github.fvarrui.javapackager.utils.Logger;

public class PackageTask extends DefaultTask {
	
	public PackageTask() {
		super();
		
		setGroup(PackagePlugin.GROUP_NAME);
		setDescription("Packages the application as a native Windows, Mac OS X or GNU/Linux executable and creates an installer");
		
		GradleContext.setLogger(getLogger());
				
	}

	@TaskAction
	public void doPackage() {
		
		PackagePluginExtension settings = (PackagePluginExtension) getProject().getExtensions().findByName(PackagePlugin.SETTINGS_EXT_NAME);
		Logger.warn(settings.toString());
		
		Jar jarTask = (Jar) getProject().getTasks().findByName("jar");
		jarTask.getManifest().getAttributes().put("Main-Class", settings.getMainClass());
		jarTask.getActions().forEach(action -> action.execute(jarTask));
		// find out how to set destination dir
		
		System.out.println(jarTask.getArchiveFile().get().getAsFile());
		System.out.println(getProject().getBuildDir());
		
		Logger.error("'An error log message.'");
		Logger.warn("'A warning log message.'");
		Logger.info("'An info log message.'");
	}
	
}
