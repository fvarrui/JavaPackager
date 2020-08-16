package io.github.fvarrui.javapackager.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class PackageTask extends DefaultTask {
	
	public PackageTask() {
		super();
		setGroup(PackagePlugin.GROUP_NAME);
		setDescription("Packages the application as a native Windows, Mac OS X or GNU/Linux executable and creates an installer");
	}

	@TaskAction
	public void doPackage() {
		
		PackagePluginExtension settings = (PackagePluginExtension) getProject().getExtensions().findByName(PackagePlugin.SETTINGS_EXT_NAME);
		System.out.println(settings);
		
	}
	
}
