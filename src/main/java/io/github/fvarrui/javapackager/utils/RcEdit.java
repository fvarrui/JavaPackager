package io.github.fvarrui.javapackager.utils;

import java.io.File;

import static io.github.fvarrui.javapackager.utils.CommandUtils.execute;

public class RcEdit {
	
	private File rcedit;
	
	public RcEdit(File outputDir) throws Exception {
		rcedit = new File(outputDir, "rcedit.exe");
		if (!rcedit.exists()) {
			FileUtils.copyResourceToFile("/windows/rcedit-x64.exe", rcedit);
		}
	}
	
	public RcEdit() throws Exception {
		this(new File(System.getProperty("java.io.tmpdir")));
	}

	private void setExeMetadata(File executable, String option, String key, Object value) throws Exception {		
		execute(rcedit, executable, option, key, value);
	}

	private void setExeMetadata(File executable, String option, Object value) throws Exception {		
		execute(rcedit, executable, option, value);
	}
	
	public void setIcon(File executable, File icon) throws Exception {
		setExeMetadata(executable, "--set-icon", icon);
	}
	
	public void setManifest(File executable, File manifest) throws Exception {
		setExeMetadata(executable, "--application-manifest", manifest);
	}

	public void setFileVersion(File executable, String fileVersion) throws Exception {
		setExeMetadata(executable, "--set-file-version", fileVersion);
	}

	public void setProductVersion(File executable, String productVersion) throws Exception {
		setExeMetadata(executable, "--set-product-version", productVersion);
	}

	public void setVersionString(File executable, String key, String value) throws Exception {
		setExeMetadata(executable, "--set-version-string", key, value);
	}
	
}
