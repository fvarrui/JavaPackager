package io.github.fvarrui.javapackager.model;

import java.io.Serializable;

public class RegistryValue implements Serializable {

    private static final long serialVersionUID = 146958222849019L;

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
    public String toString () {
        return "RegisterValue [valueName=" + valueName + ",valueType=" + valueType + ",valueData="+valueData+"]";
    }
}
