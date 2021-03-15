package io.github.fvarrui.javapackager.model;

import java.util.ArrayList;
import java.util.List;

public class Registry {

	private List<RegistryEntry> entries = new ArrayList<>();

	public List<RegistryEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<RegistryEntry> entries) {
		this.entries = entries;
	}

	@Override
	public String toString() {
		return "Registry [entries=" + entries + "]";
	}

}
