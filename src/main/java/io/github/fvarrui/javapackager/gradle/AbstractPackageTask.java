package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * Abstract packaging task for Gradle 
 */
public abstract class AbstractPackageTask extends DefaultTask {
	
	private List<File> outputFiles;
	
	@OutputFiles
	public List<File> getOutputFiles() {
		return outputFiles != null ? outputFiles : new ArrayList<>();
	}	
		
	/**
	 * Task constructor
	 */
	public AbstractPackageTask() {
		super();
		setGroup(PackagePlugin.GROUP_NAME);
		setDescription("Packages the application as a native Windows, MacOS or GNU/Linux executable and creates an installer");
		getOutputs().upToDateWhen(o -> false);
	}
	
	/**
	 * Packaging task action
	 * @throws Exception Throwed if something went wrong
	 */
	@TaskAction
	public void doPackage() throws Exception {
		
		Packager packager = createPackager();
		
		// generates app, installers and bundles
		File app = packager.createApp();
		List<File> installers = packager.generateInstallers();
		List<File> bundles = packager.createBundles();
		
		// sets generated files as output
		outputFiles = new ArrayList<>();
		outputFiles.add(app);
		outputFiles.addAll(installers);
		outputFiles.addAll(bundles);

	}
	
	/**
	 * Creates a platform specific packager
	 * @return Packager
	 * @throws Exception Throwed if something went wrong
	 */
	protected abstract Packager createPackager() throws Exception; 
	
}
