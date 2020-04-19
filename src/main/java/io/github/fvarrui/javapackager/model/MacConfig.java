package io.github.fvarrui.javapackager.model;

import java.io.File;

public class MacConfig {

	private File backgroundImage;
	private Integer windowWidth;
	private Integer windowHeight;
	private Integer windowX;
	private Integer windowY;
	private Integer iconSize;
	private Integer textSize;
	private Integer iconX;
	private Integer iconY;
	private Integer appsLinkIconX;
	private Integer appsLinkIconY;
	private File volumeIcon;
	private String volumeName;

	public File getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(File backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public Integer getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(Integer windowWidth) {
		this.windowWidth = windowWidth;
	}

	public Integer getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(Integer windowHeight) {
		this.windowHeight = windowHeight;
	}

	public Integer getWindowX() {
		return windowX;
	}

	public void setWindowX(Integer windowX) {
		this.windowX = windowX;
	}

	public Integer getWindowY() {
		return windowY;
	}

	public void setWindowY(Integer windowY) {
		this.windowY = windowY;
	}

	public Integer getIconSize() {
		return iconSize;
	}

	public void setIconSize(Integer iconSize) {
		this.iconSize = iconSize;
	}

	public Integer getTextSize() {
		return textSize;
	}

	public void setTextSize(Integer textSize) {
		this.textSize = textSize;
	}

	public Integer getIconX() {
		return iconX;
	}

	public void setIconX(Integer iconX) {
		this.iconX = iconX;
	}

	public Integer getIconY() {
		return iconY;
	}

	public void setIconY(Integer iconY) {
		this.iconY = iconY;
	}

	public Integer getAppsLinkIconX() {
		return appsLinkIconX;
	}

	public void setAppsLinkIconX(Integer appsLinkIconX) {
		this.appsLinkIconX = appsLinkIconX;
	}

	public Integer getAppsLinkIconY() {
		return appsLinkIconY;
	}

	public void setAppsLinkIconY(Integer appsLinkIconY) {
		this.appsLinkIconY = appsLinkIconY;
	}

	public File getVolumeIcon() {
		return volumeIcon;
	}

	public void setVolumeIcon(File volumeIcon) {
		this.volumeIcon = volumeIcon;
	}

	public String getVolumeName() {
		return volumeName;
	}

	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}

}
