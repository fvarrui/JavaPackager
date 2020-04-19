package io.github.fvarrui.javapackager.model;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.util.Map;

public class WinConfig {

	private String companyName;
	private String copyright;
	private String fileDescription;
	private String fileVersion;
	private String internalName;
	private String language;
	private String originalFilename;
	private String productName;
	private String productVersion;
	private String trademarks;
	private String txtFileVersion;
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
