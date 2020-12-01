package io.github.fvarrui.javapackager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manifest implements Serializable {
	private static final long serialVersionUID = -7271763575775465174L;
	
	private Map<String, String> additionalEntries = new HashMap<>();
	private List<ManifestSection> sections = new ArrayList<>();

	public Map<String, String> getAdditionalEntries() {
		return additionalEntries;
	}

	public void setAdditionalEntries(Map<String, String> additionalEntries) {
		this.additionalEntries = additionalEntries;
	}

	public List<ManifestSection> getSections() {
		return sections;
	}

	public void setSections(List<ManifestSection> sections) {
		this.sections = sections;
	}

	@Override
	public String toString() {
		return "Manifest [additionalEntries=" + additionalEntries + ", sections=" + sections + "]";
	}

}
