package io.github.fvarrui.javapackager.packagers;

import io.github.fvarrui.javapackager.utils.Const;
import io.github.fvarrui.javapackager.utils.GraalVM;
import io.github.fvarrui.javapackager.utils.Logger;

import java.io.File;
import java.util.Objects;

public class GenerateSharedLibrary extends ArtifactGenerator<Packager> {

	public GenerateSharedLibrary() {
		super("Shared library");
	}

	@Override
	public boolean skip(Packager packager) {
		
		if (!packager.task.isSharedLibrary()) {
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
		File outputDirectory = packager.task.getOutputDirectory();
		GraalVM graalVM = new GraalVM(packager.task.getJdkPath());
		return graalVM.generateSharedLibrary(packager.task.getPlatform(), outputDirectory);
	}
	
}
