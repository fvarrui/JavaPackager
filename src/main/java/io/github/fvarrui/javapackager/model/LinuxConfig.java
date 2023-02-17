package io.github.fvarrui.javapackager.model;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import io.github.fvarrui.javapackager.packagers.Packager;

/**
 * JavaPackager GNU/Linux specific configuration
 */
public class LinuxConfig implements Serializable {
	private static final long serialVersionUID = -1238166997019141904L;

	private List<String> categories;
	private boolean generateDeb = true;
	private boolean generateRpm = true;
	private boolean generateAppImage = true;
	private boolean generateSnap = false;
	private File pngFile;
	private boolean wrapJar = true;
	
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	public List<String> getCategories() {
		return categories;
	}

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

	public boolean isGenerateSnap() {
		return generateSnap;
	}
	
	public void setGenerateSnap(boolean generateSnap) {
		this.generateSnap = generateSnap;
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
		return "LinuxConfig [categories=" + categories + ", generateDeb=" + generateDeb + ", generateRpm=" + generateRpm
				+ ", generateAppImage=" + generateAppImage + ", generateSnap=" + generateSnap + ", pngFile=" + pngFile
				+ ", wrapJar=" + wrapJar + "]";
	}

	/**
	 * Checks GNU/Linux specific config and set defaults if not specified
	 * @param packager Packager
	 */
	public void setDefaults(Packager packager) {
		this.setCategories((categories == null || categories.isEmpty()) ? Arrays.asList("Utility") : categories);
	}

}
