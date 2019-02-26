package fvarrui.maven.plugin.javapackager.utils;

import java.net.URL;

public class App {

	private String name;
	private String version;
	private String description;
	private URL url;
	private String organizationName;
	private String organizationEmail;
	private String license;
	private Boolean administratorRequired;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationEmail() {
		return organizationEmail;
	}

	public void setOrganizationEmail(String organizationEmail) {
		this.organizationEmail = organizationEmail;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Boolean isAdministratorRequired() {
		return administratorRequired;
	}

	public void setAdministratorRequired(Boolean administratorRequired) {
		this.administratorRequired = administratorRequired;
	}

}
