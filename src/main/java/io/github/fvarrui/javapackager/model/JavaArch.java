package io.github.fvarrui.javapackager.model;

public enum JavaArch {
    aarch64("arm64", "AARCH64"),
    x64("amd64", "X86_64");

    private String deb;
    private String rpm;

    JavaArch(String deb, String rpm) {
        this.deb = deb;
        this.rpm = rpm;
    }

    public String getDeb() {
        return deb;
    }

    public String getRpm() {
        return rpm;
    }
}
