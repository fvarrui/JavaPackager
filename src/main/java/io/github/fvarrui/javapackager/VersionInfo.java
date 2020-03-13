package io.github.fvarrui.javapackager;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

public class VersionInfo {

	@Parameter(defaultValue = "${organizationName}", required = false)
	private String companyName;

	@Parameter(required = false)
	private String copyright;

	@Parameter(defaultValue = "${description}", required = false)
	private String fileDescription;

	@Parameter(required = false)
	private String fileVersion;

	@Parameter(defaultValue = "${name}", required = false)
	private String internalName;

	@Parameter(required = false)
	private String language;

	@Parameter(defaultValue = "${name}.exe", required = false)
	private String originalFilename;

	@Parameter(defaultValue = "${name}", required = false)
	private String productName;

	@Parameter(required = false)
	private String productVersion;

	@Parameter(required = false)
	private String trademarks;

	@Parameter(defaultValue = "${version}", required = false)
	private String txtFileVersion;

	@Parameter(defaultValue = "${version}", required = false)
	private String txtProductVersion;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	public String getTrademarks() {
		return trademarks;
	}

	public void setTrademarks(String trademarks) {
		this.trademarks = trademarks;
	}

	public String getTxtFileVersion() {
		return txtFileVersion;
	}

	public void setTxtFileVersion(String txtFileVersion) {
		this.txtFileVersion = txtFileVersion;
	}

	public String getTxtProductVersion() {
		return txtProductVersion;
	}

	public void setTxtProductVersion(String txtProductVersion) {
		this.txtProductVersion = txtProductVersion;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	@Override
	public String toString() {
		return "VersionInfo ["
				+ "companyName=" + companyName + ", "
				+ "copyright=" + copyright + ", "
				+ "fileDescription=" + fileDescription + ", "
				+ "fileVersion=" + fileVersion + ", "
				+ "internalName=" + internalName + ", "
				+ "language=" + language + ", "
				+ "originalFilename=" + originalFilename + ", "
				+ "productName=" + productName + ", "
				+ "productVersion=" + productVersion + ", "
				+ "trademarks=" + trademarks + ", "
				+ "txtFileVersion=" + txtFileVersion + ", "
				+ "txtProductVersion=" + txtProductVersion + 
			"]";
	}
	
	public void setDefaults(Map<String, Object> info) {
		fileVersion 		= defaultIfBlank(fileVersion, 		"1.0.0.0");
		txtFileVersion 		= defaultIfBlank(txtFileVersion, 	"" + info.get("version"));
		productVersion 		= defaultIfBlank(productVersion, 	"1.0.0.0");
		txtProductVersion 	= defaultIfBlank(txtProductVersion, "" + info.get("version"));
		companyName			= defaultIfBlank(companyName, 		"" + info.get("organizationName"));
		copyright 			= defaultIfBlank(copyright, 		"" + info.get("organizationName"));
		fileDescription 	= defaultIfBlank(fileDescription, 	"" + info.get("description"));
		productName 		= defaultIfBlank(productName, 		"" + info.get("name"));
		internalName 		= defaultIfBlank(internalName, 		"" + info.get("name"));
		originalFilename 	= defaultIfBlank(originalFilename, 	info.get("name") + ".exe");
	}
	
}
