package io.github.fvarrui.javapackager.model;

import org.apache.commons.lang3.SystemUtils;
import org.redline_rpm.header.Architecture;

public enum Arch {
	aarch64, 
	x64, 
	x86;
	
	public static Arch getArch(String archString) {
		switch (archString) {
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
		    throw new IllegalArgumentException("Unknown architecture " + archString);
		}
	}
	
	public static Arch getDefault() {
		return getArch(SystemUtils.OS_ARCH);
	}
	
	public Architecture toRpmArchitecture() {		
		switch (this) {
		case aarch64: return Architecture.AARCH64;
		case x64: return Architecture.X86_64;
		case x86: return Architecture.I386;
		default: return null;
		}
	}
	
}