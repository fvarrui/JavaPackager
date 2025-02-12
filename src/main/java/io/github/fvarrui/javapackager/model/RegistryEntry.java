package io.github.fvarrui.javapackager.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Windows Registry entry
 */
public class RegistryEntry implements Serializable {
	private static final long serialVersionUID = 447936480111873679L;

	/**
	 * Windows registry key: HKCU, HKLM, ...
	 */
	private String key;

	private  List<RegistryValue> registryValues = new ArrayList<>();

	public RegistryEntry() {
		super();
	}

	public RegistryEntry(String key, List<RegistryValue> registryValues) {
		super();
		this.key = key;
		this.registryValues = registryValues;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<RegistryValue> getRegistryValues() {
		return registryValues;
	}

	public void setRegistryValue(List<RegistryValue> registryValues) {
		this.registryValues = registryValues;
	}



	public String getRoot() {
		return key.split(":")[0];
	}

	public String getSubkey() {
		String subkey = key.split(":")[1];
		return subkey.startsWith("/") ? subkey.substring(1) : subkey;
	}



	@Override
	public String toString() {
		return "RegistryEntry [key=" + key + ", registryValues=" + registryValues + "]";
	}

}
