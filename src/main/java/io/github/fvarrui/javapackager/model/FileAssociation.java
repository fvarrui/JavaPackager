package io.github.fvarrui.javapackager.model;

public class FileAssociation {
	private String mimeType;
	private String extension;
	private String description;

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "FileAssociation [mimeType=" + mimeType + ", extension=" + extension + ", description=" + description
				+ "]";
	}

}
