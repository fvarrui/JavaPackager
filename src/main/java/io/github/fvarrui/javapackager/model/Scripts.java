package io.github.fvarrui.javapackager.model;

import java.io.File;

public class Scripts {

	private File bootstrap;
	private File preInstall;
	private File postInstall;

	public File getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(File bootstrap) {
		this.bootstrap = bootstrap;
	}

	public File getPreInstall() {
		return preInstall;
	}

	public void setPreInstall(File preInstall) {
		this.preInstall = preInstall;
	}

	public File getPostInstall() {
		return postInstall;
	}

	public void setPostInstall(File postInstall) {
		this.postInstall = postInstall;
	}

	@Override
	public String toString() {
		return "Scripts [bootstrap=" + bootstrap + ", preInstall=" + preInstall + ", postInstall=" + postInstall + "]";
	}

}
