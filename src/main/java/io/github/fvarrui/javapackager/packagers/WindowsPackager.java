package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import net.jsign.WindowsSigner;
import org.apache.commons.lang3.StringUtils;

import io.github.fvarrui.javapackager.model.Arch;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

/**
 * Packager for Windows
 */
public class WindowsPackager extends Packager {
	
	private File manifestFile;
	private File msmFile;
	
	public File getManifestFile() {
		return manifestFile;
	}
	
	public File getMsmFile() {
		return msmFile;
	}

	public void setMsmFile(File msmFile) {
		this.msmFile = msmFile;
	}
	
	public WindowsPackager() {		
		super();
		platform(Platform.windows);
	}

	@Override
	public void doInit() throws Exception {
		
		// sets default system architecture 
		if (getArch() != Arch.x64 && getArch() != Arch.x86) {
			if (Platform.windows.isCurrentPlatform())
				arch(Arch.getDefault());
			else
				arch(Arch.x64);
		}
		
		// sets windows config default values
		this.winConfig.setDefaults(this);
		
	}

	@Override
	protected void doCreateAppStructure() throws Exception {

		// sets common folders
		this.executableDestinationFolder = appFolder;
		this.jarFileDestinationFolder = appFolder;
		this.jreDestinationFolder = new File(appFolder, jreDirectoryName);
		this.resourcesDestinationFolder = appFolder;

	}
	
	/**
	 * Creates a Windows app file structure with native executable
	 */
	@Override
	public File doCreateApp() throws Exception {
		
		Logger.infoIndent("Creating windows EXE ... with " + getWinConfig().getExeCreationTool());

		// generates manifest file to require administrator privileges from velocity template
		manifestFile = new File(assetsFolder, name + ".exe.manifest");
		VelocityUtils.render("windows/exe.manifest.vtl", manifestFile, this);
		Logger.info("Exe manifest file generated in " + manifestFile.getAbsolutePath() + "!");

		// sets executable file
		executable = new File(appFolder, name + ".exe");
		
		// process classpath
		if (classpath != null) {
			classpaths = Arrays.asList(classpath.split("[;:]"));
			if (!isUseResourcesAsWorkingDir()) {
				classpaths = classpaths.stream().map(cp -> new File(cp).isAbsolute() ? cp : "%EXEDIR%/" + cp).collect(Collectors.toList());
			}
			classpath = StringUtils.join(classpaths, ";");
		}
		
		// invokes Windows exe artifact generator (building tool dependant)
		executable = Context.getContext().createWindowsExe(this);

		// signs the executable
		WindowsSigner.sign(executable, getDisplayName(), getUrl(), getWinConfig().getSigning());

		Logger.infoUnindent("Windows EXE file created in " + executable + "!");		
		
		return appFolder;
	}
	
}
