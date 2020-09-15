package io.github.fvarrui.javapackager.model;

import java.io.File;
import java.io.Serializable;

import io.github.fvarrui.javapackager.packagers.Packager;

public class LinuxConfig implements Serializable {
	private static final long serialVersionUID = -1238166997019141904L;
	
	private boolean generateDeb = true;
	private boolean generateRpm = true;
	private File pngFile;
	private File xpmFile;

	public boolean isGenerateDeb() {
		return generateDeb;
	}

	public void setGenerateDeb(boolean generateDeb) {
		this.generateDeb = generateDeb;
	}

	public boolean isGenerateRpm() {
		return generateRpm;
	}

	public void setGenerateRpm(boolean generateRpm) {
		this.generateRpm = generateRpm;
	}

	public File getPngFile() {
		return pngFile;
	}

	public void setPngFile(File pngFile) {
		this.pngFile = pngFile;
	}

	public File getXpmFile() {
		return xpmFile;
	}

	public void setXpmFile(File xpmFile) {
		this.xpmFile = xpmFile;
	}

	@Override
	public String toString() {
		return "LinuxConfig [generateDeb=" + generateDeb + ", generateRpm=" + generateRpm + ", pngFile=" + pngFile
				+ ", xpmFile=" + xpmFile + "]";
	}

	/**
	 * Tests GNU/Linux specific config and set defaults if not specified
	 * 
	 * @param packager Packager
	 */
	public void setDefaults(Packager packager) {
		// nothing
	}

}
