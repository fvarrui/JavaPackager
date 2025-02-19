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

	/**
	 * Windows Registry value name
	 */
	private String valueName;

	/**
	 * Windows Registry value type
	 */
	private ValueType valueType = ValueType.REG_SZ;

	/**
	 * Windows Registry value data
	 */
	private String valueData = "";

	private List<RegistryValue> values=new ArrayList<>();

	public RegistryEntry() {
		super();
	}

	public RegistryEntry(String key, String valueName, ValueType valueType, String valueData) {
		super();
		this.key = key;
		this.valueName = valueName;
		this.valueType = valueType;
		this.valueData = valueData;
	}

	public RegistryEntry(String key, List<RegistryValue> values) {
		super();
		this.key = key;
		if(values!=null){
			this.values.addAll(values);
		}
	}


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public String getValueData() {
		return valueData;
	}

	public void setValueData(String valueData) {
		this.valueData = valueData;
	}

	public String getRoot() {
		return key.split(":")[0];
	}

	public String getSubkey() {
		String subkey = key.split(":")[1];
		return subkey.startsWith("/") ? subkey.substring(1) : subkey;
	}


	public List<RegistryValue> getValues() {
		return values;
	}

	public void setValues(List<RegistryValue> values) {
		this.values = values;
	}

	/**
	 * Returns value type as Inno Setup expects
	 * https://jrsoftware.org/ishelp/index.php?topic=registrysection
	 * @return Value type converted to IS format
	 */
	public String getValueTypeAsInnoSetupString() {
		switch(valueType) {
			case REG_BINARY: return "binary";
			case REG_DWORD: return "dword";
			case REG_EXPAND_SZ: return "expandsz";
			case REG_MULTI_SZ: return "multisz";
			case REG_QWORD: return "qword";
			case REG_SZ: return "string";
			default: return "none";
		}
	}

	/**
	 * Returns value type as WIX Toolset expects
	 * https://wixtoolset.org/documentation/manual/v3/xsd/wix/registryvalue.html
	 */
	public String getValueTypeAsWIXToolsetString() {
		switch(valueType) {
			case REG_BINARY: return "binary";
			case REG_DWORD: return "integer";
			case REG_EXPAND_SZ: return "expandable";
			case REG_MULTI_SZ: return "multiString";
			case REG_QWORD: return "integer";
			case REG_SZ: return "string";
			default: return "none";
		}
	}

	@Override
	public String toString() {
		return "RegistryEntry [key=" + key + ", valueName=" + valueName + ", valueType=" + valueType + ", valueData="
				+ valueData + "]";
	}

}
