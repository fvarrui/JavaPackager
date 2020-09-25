package io.github.fvarrui.javapackager.gradle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import io.github.fvarrui.javapackager.packagers.Packager;

public abstract class AbstractPackageTask extends DefaultTask {
	
	@OutputFiles
	private List<File> outputFiles;
	
	public List<File> getOutputFiles() {
		return outputFiles != null ? outputFiles : new ArrayList<>();
	}	
		
	// ================
	// task constructor
	// ================
	
	public AbstractPackageTask() {
		super();
		setGroup(PackagePlugin.GROUP_NAME);
		setDescription("Packages the application as a native Windows, Mac OS X or GNU/Linux executable and creates an installer");
	}
	
	// ===========
	// task action
	// ===========
	
	@TaskAction
	public void doPackage() {
		
		try {
			
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
			
		} catch (Exception e) {

			throw new RuntimeException(e.getMessage(), e);
			
		}

	}
	
	protected abstract Packager createPackager() throws Exception; 
	
}
