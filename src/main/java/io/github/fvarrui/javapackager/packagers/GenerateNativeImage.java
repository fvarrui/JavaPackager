package io.github.fvarrui.javapackager.packagers;

import io.github.fvarrui.javapackager.utils.Const;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;
import io.github.fvarrui.javapackager.utils.XMLUtils;

import java.io.File;
import java.util.Objects;

import static io.github.fvarrui.javapackager.utils.CommandUtils.execute;

/**
 * TODO
 */
public class GenerateNativeImage extends ArtifactGenerator<Packager> {

	public GenerateNativeImage() {
		super("Native Image");
	}

	@Override
	public boolean skip(Packager packager) {
		
		if (!packager.task.isNativeImage()) {
			return true;
		}
		
		if (!Objects.equals(packager.task.getJdkVendor(), Const.graalvm)) {
			Logger.warn(getArtifactName() + " cannot be generated because '"+Const.graalvm +"' was expected as jdkVendor, but provided '"+packager.task.getJdkVendor()+"'!");
			return true;
		}
		
		return false;		
	}
	
	@Override
	protected File doApply(Packager packager) throws Exception {

		File assetsFolder = packager.getAssetsFolder();
		String name = packager.task.getAppName();
		File outputDirectory = packager.task.getOutputDirectory();
		String version = packager.task.getVersion();
		
		//TODO
		
		return null;
	}
	
}
