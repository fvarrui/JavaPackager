package io.github.fvarrui.javapackager.model;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import io.github.fvarrui.javapackager.packagers.Packager;

public class WindowsConfig {

	private HeaderType headerType;
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
	private boolean disableDirPage = true;
	private boolean disableProgramGroupPage = true;
	private boolean disableFinishedPage = true;
	private boolean createDesktopIconTask = true;

	public HeaderType getHeaderType() {
		return headerType;
	}

	public void setHeaderType(HeaderType headerType) {
		this.headerType = headerType;
	}

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

	public boolean isDisableDirPage() {
		return disableDirPage;
	}

	public void setDisableDirPage(boolean disableDirPage) {
		this.disableDirPage = disableDirPage;
	}

	public boolean isDisableProgramGroupPage() {
		return disableProgramGroupPage;
	}

	public void setDisableProgramGroupPage(boolean disableProgramGroupPage) {
		this.disableProgramGroupPage = disableProgramGroupPage;
	}

	public boolean isDisableFinishedPage() {
		return disableFinishedPage;
	}

	public void setDisableFinishedPage(boolean disableFinishedPage) {
		this.disableFinishedPage = disableFinishedPage;
	}

	public boolean isCreateDesktopIconTask() {
		return createDesktopIconTask;
	}

	public void setCreateDesktopIconTask(boolean createDesktopIconTask) {
		this.createDesktopIconTask = createDesktopIconTask;
	}

	@Override
	public String toString() {
		return "[headerType=" + headerType + ", companyName=" + companyName + ", copyright=" + copyright
				+ ", fileDescription=" + fileDescription + ", fileVersion=" + fileVersion + ", internalName="
				+ internalName + ", language=" + language + ", originalFilename=" + originalFilename + ", productName="
				+ productName + ", productVersion=" + productVersion + ", trademarks=" + trademarks
				+ ", txtFileVersion=" + txtFileVersion + ", txtProductVersion=" + txtProductVersion
				+ ", disableDirPage=" + disableDirPage + ", disableProgramGroupPage=" + disableProgramGroupPage
				+ ", disableFinishedPage=" + disableFinishedPage + ", createDesktopIconTask=" + createDesktopIconTask
				+ "]";
	}

	/**
	 * Tests Windows specific config and set defaults if not specified
	 * @param packager
	 */
	public void setDefaults(Packager packager) {
		this.setHeaderType(this.getHeaderType() == null ? HeaderType.gui : this.getHeaderType());
		this.setFileVersion(defaultIfBlank(this.getFileVersion(), "1.0.0.0"));
		this.setTxtFileVersion(defaultIfBlank(this.getTxtFileVersion(), "" + packager.getVersion()));
		this.setProductVersion(defaultIfBlank(this.getProductVersion(), "1.0.0.0"));
		this.setTxtProductVersion(defaultIfBlank(this.getTxtProductVersion(), "" + packager.getVersion()));
		this.setCompanyName(defaultIfBlank(this.getCompanyName(), packager.getOrganizationName()));
		this.setCopyright(defaultIfBlank(this.getCopyright(), packager.getOrganizationName()));
		this.setFileDescription(defaultIfBlank(this.getFileDescription(), packager.getDescription()));
		this.setProductName(defaultIfBlank(this.getProductName(), packager.getName()));
		this.setInternalName(defaultIfBlank(this.getInternalName(), packager.getName()));
		this.setOriginalFilename(defaultIfBlank(this.getOriginalFilename(), packager.getName() + ".exe"));
	}

}
