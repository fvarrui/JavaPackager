package io.github.fvarrui.javapackager.packagers;

import java.io.File;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import io.github.fvarrui.javapackager.utils.XMLUtils;

/**
 * Creates an MSI file including all app folder's content only for Windows so app
 * could be easily distributed
 */
public class GenerateMsm extends ArtifactGenerator<WindowsPackager> {

	public GenerateMsm() {
		super("MSI merge module");
	}

	@Override
	public boolean skip(WindowsPackager packager) {
		
		if (!packager.getWinConfig().isGenerateMsm() && !packager.getWinConfig().isGenerateMsi()) {
			return true;
		}
		
		if (!packager.getPlatform().isCurrentPlatform() && !packager.isForceInstaller()) {
			Logger.warn(getArtifactName() + " cannot be generated due to the target platform (" + packager.getPlatform() + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return true;
		}
		
		return false;
	}

	@Override
	protected File doApply(WindowsPackager packager) throws Exception {
		
		if (packager.getMsmFile() != null) {
			return packager.getMsmFile();
		}

		File assetsFolder = packager.getAssetsFolder();
		String name = packager.getName();
		File outputDirectory = packager.getOutputDirectory();
		String version = packager.getVersion();
		
		// generates WXS file from velocity template
		File wxsFile = new File(assetsFolder, name + ".msm.wxs");
		VelocityUtils.render("windows/msm.wxs.vtl", wxsFile, packager);
		Logger.info("WXS file generated in " + wxsFile + "!");

		// prettify wxs
		XMLUtils.prettify(wxsFile);

		// candle wxs file
		Logger.info("Compiling file " + wxsFile);
		File wixobjFile = new File(assetsFolder, name + ".msm.wixobj");
		CommandUtils.execute("candle", "-out", wixobjFile, wxsFile);
		Logger.info("WIXOBJ file generated in " + wixobjFile + "!");

		// lighting wxs file
		Logger.info("Linking file " + wixobjFile);
		File msmFile = new File(outputDirectory, name + "_" + version + ".msm");
		CommandUtils.execute("light", "-spdb", "-out", msmFile, wixobjFile);

		// setup file
		if (!msmFile.exists()) {
			throw new Exception("MSI installer file generation failed!");
		}
		
		packager.setMsmFile(msmFile);
		
		return msmFile;
	}

}
