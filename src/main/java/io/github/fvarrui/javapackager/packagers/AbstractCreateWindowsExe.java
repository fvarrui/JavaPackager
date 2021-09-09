package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public abstract class AbstractCreateWindowsExe extends WindowsArtifactGenerator {

	private File launch4jFolder;
	private File genericManifest;
	private File genericIcon;
	private File genericJar;
	private File genericExe;
	
	public AbstractCreateWindowsExe() {
		super("Windows EXE");
	}

	public File getLaunch4jFolder() {
		return launch4jFolder;
	}

	public void setLaunch4jFolder(File launch4jFolder) {
		this.launch4jFolder = launch4jFolder;
	}

	public File getGenericManifest() {
		return genericManifest;
	}

	public void setGenericManifest(File genericManifest) {
		this.genericManifest = genericManifest;
	}

	public File getGenericIcon() {
		return genericIcon;
	}

	public void setGenericIcon(File genericIcon) {
		this.genericIcon = genericIcon;
	}

	public File getGenericJar() {
		return genericJar;
	}

	public void setGenericJar(File genericJar) {
		this.genericJar = genericJar;
	}

	public File getGenericExe() {
		return genericExe;
	}

	public void setGenericExe(File genericExe) {
		this.genericExe = genericExe;
	}	

	/**
	 * Renames assets required for launch4j to avoid unsupported characters
	 * (chinese, e.g.)
	 * 
	 * @param packager Windows packager
	 * @throws Exception Something went wrong
	 */
	protected void createAssets(WindowsPackager packager) throws Exception {

		File manifestFile = packager.getManifestFile();
		File iconFile = packager.getIconFile();
		File jarFile = packager.getJarFile();

		File launch4j = new File(Context.getContext().getBuildDir(), "launch4j");
		FileUtils.mkdir(launch4j);

		genericManifest = new File(launch4j, "app.exe.manifest");
		genericIcon = new File(launch4j, "app.ico");
		genericJar = new File(launch4j, "app.jar");
		genericExe = new File(launch4j, "app.exe");

		FileUtils.copyFileToFile(manifestFile, genericManifest);
		FileUtils.copyFileToFile(iconFile, genericIcon);
		FileUtils.copyFileToFile(jarFile, genericJar);

	}
	
	/**
	 * Creates bootstrap script if needed 
	 * @param packager
	 * @return
	 * @throws Exception
	 */
	protected File createBootstrapScript(WindowsPackager packager) throws Exception {
		File executable = packager.getExecutable(); 
		
		if (FileUtils.exists(packager.getScripts().getBootstrap())) {

			// generates startup VBS script file
			File vbsFile = new File(packager.getAppFolder(), packager.getName() + ".vbs");
			VelocityUtils.render(Platform.windows + "/startup.vbs.vtl", vbsFile, packager);
			executable = vbsFile;

		}
		
		return executable;
	}

}
