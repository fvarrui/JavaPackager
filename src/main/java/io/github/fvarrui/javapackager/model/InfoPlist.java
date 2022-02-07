package io.github.fvarrui.javapackager.model;

public class InfoPlist {

	private String additionalEntries = "";
	private CFBundlePackageType bundlePackageType = CFBundlePackageType.BNDL;

	public String getAdditionalEntries() {
		return additionalEntries;
	}

	public void setAdditionalEntries(String additionalEntries) {
		this.additionalEntries = additionalEntries;
	}

	public CFBundlePackageType getBundlePackageType() {
		return bundlePackageType;
	}

	public void setBundlePackageType(CFBundlePackageType bundlePackageType) {
		this.bundlePackageType = bundlePackageType;
	}

	@Override
	public String toString() {
		return "InfoPlist [additionalEntries=" + additionalEntries + ", bundlePackageType=" + bundlePackageType + "]";
	}

}
