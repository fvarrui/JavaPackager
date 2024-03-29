package io.github.fvarrui.javapackager.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manifest section
 */
public class ManifestSection implements Serializable {
	private static final long serialVersionUID = 118641813298011799L;

	private String name;
	private Map<String, String> entries = new HashMap<>();

	public ManifestSection() {
		super();
	}

	public ManifestSection(String name, Map<String, String> entries) {
		super();
		this.name = name;
		this.entries = entries;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getEntries() {
		return entries;
	}

	public void setEntries(Map<String, String> entries) {
		this.entries = entries;
	}

	@Override
	public String toString() {
		return "ManifestSection [name=" + name + ", entries=" + entries + "]";
	}

}
