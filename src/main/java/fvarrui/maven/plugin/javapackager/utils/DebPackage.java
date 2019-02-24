package fvarrui.maven.plugin.javapackager.utils;

public class DebPackage {

	private String packageName;
	private String version;
	private String maintainerName;
	private String maintainerEmail;
	private String description;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMaintainerName() {
		return maintainerName;
	}

	public void setMaintainerName(String maintainerName) {
		this.maintainerName = maintainerName;
	}

	public String getMaintainerEmail() {
		return maintainerEmail;
	}

	public void setMaintainerEmail(String maintainerEmail) {
		this.maintainerEmail = maintainerEmail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
