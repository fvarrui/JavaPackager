package io.github.fvarrui.javapackager.model;

import java.io.File;
import java.io.Serializable;

import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * JavaPackager GNU/Linux specific configuration
 */
public class LinuxConfig implements Serializable {
	private static final long serialVersionUID = -1238166997019141904L;

	private boolean generateDeb = true;
	private boolean generateRpm = true;
	private boolean generateAppImage = true;
	private File pngFile;
	private boolean wrapJar = true;

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

	public boolean isGenerateAppImage() {
		return generateAppImage;
	}

	public void setGenerateAppImage(boolean generateAppImage) {
		this.generateAppImage = generateAppImage;
	}

	public File getPngFile() {
		return pngFile;
	}

	public void setPngFile(File pngFile) {
		this.pngFile = pngFile;
	}

	public boolean isWrapJar() {
		return wrapJar;
	}

	public void setWrapJar(boolean wrapJar) {
		this.wrapJar = wrapJar;
	}

	@Override
	public String toString() {
		return "LinuxConfig [generateDeb=" + generateDeb + ", generateRpm=" + generateRpm + ", generateAppImage="
				+ generateAppImage + ", pngFile=" + pngFile + ", wrapJar=" + wrapJar + "]";
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
