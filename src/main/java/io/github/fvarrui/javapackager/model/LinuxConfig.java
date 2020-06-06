package io.github.fvarrui.javapackager.model;

import io.github.fvarrui.javapackager.packagers.Packager;

public class LinuxConfig {

	private boolean generateDeb = true;
	private boolean generateRpm = true;

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

	@Override
	public String toString() {
		return "[generateDeb=" + generateDeb + ", generateRpm=" + generateRpm + "]";
	}

	/**
	 * Tests GNU/Linux specific config and set defaults if not specified
	 * @param packager Packager
	 */
	public void setDefaults(Packager packager) {
		// nothing
	}
	
}
