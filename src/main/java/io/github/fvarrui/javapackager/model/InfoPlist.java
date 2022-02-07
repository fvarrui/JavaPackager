package io.github.fvarrui.javapackager.model;

public class InfoPlist {

	private String additionalEntries = "";

	public String getAdditionalEntries() {
		return additionalEntries;
	}

	public void setAdditionalEntries(String additionalEntries) {
		this.additionalEntries = additionalEntries;
	}

	@Override
	public String toString() {
		return "InfoPlist [additionalEntries=" + additionalEntries + "]";
	}

}
