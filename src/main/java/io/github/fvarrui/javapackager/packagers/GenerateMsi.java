package io.github.fvarrui.javapackager.packagers;

import static io.github.fvarrui.javapackager.utils.CommandUtils.execute;

import java.io.File;

import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import io.github.fvarrui.javapackager.utils.XMLUtils;

/**
 * Creates a MSI file including all app folder's content only for
 * Windows so app could be easily distributed
 */
public class GenerateMsi extends ArtifactGenerator {

	public GenerateMsi() {
		super("MSI installer");
	}
	
	@Override
	public File apply(Packager packager) throws Exception {
		WindowsPackager windowsPackager = (WindowsPackager) packager;
		
		if (!windowsPackager.getWinConfig().isGenerateMsi()) {
			Logger.warn(getArtifactName() + " generation skipped by 'winConfig.generateMsi' property!");
			return null;
		}
		
		File msmFile = new GenerateMsm().apply(windowsPackager);
		Logger.info("MSM file generated in " + msmFile);

		File assetsFolder = windowsPackager.getAssetsFolder();
		String name = windowsPackager.getName();
		File outputDirectory = windowsPackager.getOutputDirectory();
		String version = windowsPackager.getVersion();
		
		// generates WXS file from velocity template
		File wxsFile = new File(assetsFolder, name + ".wxs");
		VelocityUtils.render("windows/wxs.vtl", wxsFile, windowsPackager);
		Logger.info("WXS file generated in " + wxsFile + "!");

		// pretiffy wxs
		XMLUtils.prettify(wxsFile);
	
		// candle wxs file
		Logger.info("Compiling file " + wxsFile);
		File wixobjFile = new File(assetsFolder, name + ".wixobj");
		execute("candle", "-out", wixobjFile, wxsFile);
		Logger.info("WIXOBJ file generated in " + wixobjFile +  "!");

		// lighting wxs file
		Logger.info("Linking file " + wixobjFile);
		File msiFile = new File(outputDirectory, name + "_" + version + ".msi");
		execute("light", "-spdb", "-out", msiFile, wixobjFile);

		// setup file
		if (!msiFile.exists()) {
			throw new Exception("MSI installer file generation failed!");
		}
		
		return msiFile;
	}
	
}
