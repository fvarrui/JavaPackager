package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.WindowsExeCreationTool;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public abstract class AbstractCreateWindowsExe extends ArtifactGenerator<WindowsPackager> {

	private final File outputFolder;
	private File genericManifest;
	private File genericIcon;
	private File genericJar;
	private File genericExe;
	
	public AbstractCreateWindowsExe(WindowsExeCreationTool tool) {
		super(tool.toString());
		this.outputFolder = new File(Context.getContext().getBuildDir(), tool.toString());
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
	
	public File getOutputFolder() {
		return outputFolder;
	}

	/**
	 * Renames assets required for exe generation to avoid unsupported characters
	 * (chinese, e.g.)
	 * 
	 * @param packager Windows packager
	 * @throws Exception Something went wrong
	 */
	protected void createAssets(WindowsPackager packager) throws Exception {

		File manifestFile = packager.getManifestFile();
		File iconFile = packager.getIconFile();
		File jarFile = packager.getJarFile();

		FileUtils.mkdir(outputFolder);

		genericManifest = new File(outputFolder, "app.exe.manifest");
		genericIcon = new File(outputFolder, "app.ico");
		genericJar = new File(outputFolder, "app.jar");
		genericExe = new File(outputFolder, "app.exe");

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
