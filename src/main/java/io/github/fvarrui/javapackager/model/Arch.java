package io.github.fvarrui.javapackager.model;

import org.apache.commons.lang3.SystemUtils;

public enum Arch {
	aarch64("aarch64", "arm64", "AARCH64"), 
	x64("x64", "amd64", "X86_64"), 
	x86("x86", "i386", "i386");

	private String setup;
	private String deb;
	private String rpm;

	Arch(String setup, String deb, String rpm) {
		this.setup = setup;
		this.deb = deb;
		this.rpm = rpm;
	}

	public String getDeb() {
		return deb;
	}

	public String getRpm() {
		return rpm;
	}
	
	public String getSetup() {
		return setup;
	}
	
	public static Arch getDefault() {
		switch (SystemUtils.OS_ARCH) {
		case "x86":
		case "i386":
		case "i486":
		case "i586":
		case "i686": 
			return x86; 
		case "x86_64":
		case "amd64":
			return x64;
		case "aarch64":
			return aarch64;
		default:
		    throw new IllegalArgumentException("Unknown architecture " + SystemUtils.OS_ARCH);
		}
	}
	
}
