package io.github.fvarrui.javapackager.model;

public enum Arch {
	aarch64("arm64", "AARCH64"), 
	x64("amd64", "X86_64"), 
	x86("i386", "i386");

	private String deb;
	private String rpm;

	Arch(String deb, String rpm) {
		this.deb = deb;
		this.rpm = rpm;
	}

	public String getDeb() {
		return deb;
	}

	public String getRpm() {
		return rpm;
	}
	
}
