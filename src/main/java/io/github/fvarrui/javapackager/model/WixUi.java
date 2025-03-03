package io.github.fvarrui.javapackager.model;

public enum WixUi {

    WixUI_Minimal;


    public static WixUi getWixUi(String wixUiString) {
        switch (wixUiString) {
            case "WixUI_Minimal":
                return WixUI_Minimal;
            default:
                throw new IllegalArgumentException("Unknown WIXUI " + wixUiString);
        }
    }

}


