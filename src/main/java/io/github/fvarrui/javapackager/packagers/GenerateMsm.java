package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import io.github.fvarrui.javapackager.utils.XMLUtils;

/**
 * Creates a MSI file including all app folder's content only for
 * Windows so app could be easily distributed
 */
public class GenerateMsm extends ArtifactGenerator {

	public GenerateMsm() {
		super("MSI merge module");
	}
	
	@Override
	public File apply(Packager packager) throws Exception {
		WindowsPackager windowsPackager = (WindowsPackager) packager;
		
		if (windowsPackager.getMsmFile() != null) return windowsPackager.getMsmFile();
		
		if (!windowsPackager.getWinConfig().isGenerateMsm() && !windowsPackager.getWinConfig().isGenerateMsi()) {
			Logger.warn(getArtifactName() + " generation skipped by 'winConfig.generateMsm' property!");
			return null;
		}

		File assetsFolder = windowsPackager.getAssetsFolder();
		String name = windowsPackager.getName();
		File outputDirectory = windowsPackager.getOutputDirectory();
		String version = windowsPackager.getVersion();
		
		// generates WXS file from velocity template
		File wxsFile = new File(assetsFolder, name + ".msm.wxs");
		VelocityUtils.render("windows/msm.wxs.vtl", wxsFile, windowsPackager);
		Logger.info("WXS file generated in " + wxsFile + "!");

		// pretiffy wxs
		XMLUtils.prettify(wxsFile);
	
		// candle wxs file
		Logger.info("Compiling file " + wxsFile);
		File wixobjFile = new File(assetsFolder, name + ".msm.wixobj");
		CommandUtils.execute("candle", "-out", wixobjFile, wxsFile);
		Logger.info("WIXOBJ file generated in " + wixobjFile +  "!");

		// lighting wxs file
		Logger.info("Linking file " + wixobjFile);
		File msmFile = new File(outputDirectory, name + "_" + version + ".msm");
		CommandUtils.execute("light", "-spdb", "-out", msmFile, wixobjFile);

		// setup file
		if (!msmFile.exists()) {
			throw new Exception("MSI installer file generation failed!");
		}
		
		windowsPackager.setMsmFile(msmFile);
		
		return msmFile;
	}
	
}
