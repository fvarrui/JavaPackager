package io.github.fvarrui.javapackager.packagers;

import static io.github.fvarrui.javapackager.utils.CommandUtils.execute;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import io.github.fvarrui.javapackager.utils.XMLUtils;
import net.jsign.WindowsSigner;
import org.jetbrains.annotations.NotNull;

/**
 * Creates an MSI file including all app folder's content only for
 * Windows so app could be easily distributed
 */
public class GenerateMsi extends ArtifactGenerator<WindowsPackager> {

	public GenerateMsi() {
		super("MSI installer");
	}
	
	@Override
	public boolean skip(WindowsPackager packager) {
		
		if (!packager.getWinConfig().isGenerateMsi()) {
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
		
		File msmFile = new GenerateMsm().doApply(packager);
		Logger.info("MSM file generated in " + msmFile);

		File assetsFolder = packager.getAssetsFolder();
		String name = packager.getName();
		File outputDirectory = packager.getOutputDirectory();
		String version = packager.getVersion();

		// generates WXS file from velocity template
		File wxsFile = new File(assetsFolder, name + ".wxs");
		VelocityUtils.render("windows/wxs.vtl", wxsFile, packager);
		Logger.info("WXS file generated in " + wxsFile + "!");

		// prettify wxs
		XMLUtils.prettify(wxsFile);
	
		// candle wxs file
		Logger.info("Compiling file " + wxsFile);
		File wixobjFile = new File(assetsFolder, name + ".wixobj");
		CommandUtils.execute("candle", "-out", wixobjFile, wxsFile);
		Logger.info("WIXOBJ file generated in " + wixobjFile +  "!");

		// lighting wxs file
		Logger.info("Linking file " + wixobjFile);
		File msiFile = new File(outputDirectory, name + "_" + version + ".msi");

		//Search custom images
		File wixImages = new File(outputDirectory.getPath()+"/"+name+"/wix-images");
		if(wixImages.exists()){
			Logger.info("WIX IMAGES EXISTS FOR MSI");
			List<String> lightArguments = getLightArguments(wixImages);
			CommandUtils.execute("light", "-sw1076", "-spdb",lightArguments.get(0),lightArguments.get(1),lightArguments.get(2),lightArguments.get(3),lightArguments.get(4),lightArguments.get(5),lightArguments.get(6),lightArguments.get(7), "-out", msiFile, wixobjFile);
		}
		else{
			CommandUtils.execute("light", "-sw1076", "-spdb", "-out", msiFile, wixobjFile);
		}

		// setup file
		if (!msiFile.exists()) {
			throw new Exception("MSI installer file generation failed!");
		}

		// sign installer
		WindowsSigner.sign(msiFile, packager.getDisplayName(), packager.getUrl(), packager.getWinConfig().getSigning());

		return msiFile;
	}

		@NotNull
		private static List<String> getLightArguments(File wixImages) {
			List<String> lightArguments = new ArrayList<>();
			lightArguments.add("-ext");
			lightArguments.add("WixUIExtension");
			lightArguments.add("-ext");
			lightArguments.add("WixUtilExtension");
			lightArguments.add("-dWixUIBannerBmp="+ wixImages.getPath()+"\\WixUIBannerBmp.bmp");
			lightArguments.add("-dWixUIDialogBmp="+ wixImages.getPath()+"\\WixUIDialogBmp.bmp");
			lightArguments.add("-dWixUIExclamationIco="+ wixImages.getPath()+"\\WixUIExclamationIco.ico");
			lightArguments.add("-dWixUIInfoIco="+ wixImages.getPath()+"\\WixUIInfoIco.ico");
			lightArguments.add("-dWixUINewIco="+ wixImages.getPath()+"\\WixUINewIco.ico");
			lightArguments.add("-dWixUIUpIco="+ wixImages.getPath()+"\\WixUIUpIco.ico");
			return lightArguments;
		}
	
}
