/*
 * Copyright (c) 2021-2022 Osiris-Team.
 * All rights reserved.
 *
 * This software is copyrighted work, licensed under the terms
 * of the MIT-License. Consult the "LICENSE" file for details.
 */

package io.github.fvarrui.javapackager.utils.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Details here: https://api.adoptium.net/q/swagger-ui
 */
public class AdoptV3API {
    private final String BASE = "https://api.adoptium.net/v3";
    private final String START_DOWNLOAD_URL = BASE + "/binary/version/";
    private final String START_RELEASES_URL = BASE + "/info/release_versions?architecture=";
    private final String START_ASSETS_URL = BASE + "/assets/version/";

    public JsonObject getAvailableReleases() throws IOException {
        return Json.fromUrlAsObject(BASE + "/info/available_releases");
    }

    public String getLatestLTSRelease() throws IOException {
        JsonArray arr = getAvailableReleases().getAsJsonArray("available_lts_releases");
        return arr.get(arr.size() - 1).getAsString();
    }

    public String getLatestRelease() throws IOException {
        JsonArray arr = getAvailableReleases().getAsJsonArray("available_releases");
        return arr.get(arr.size() - 1).getAsString();
    }

    /**
     * Creates and returns a new url from the provided parameters. <br>
     * For a list of all available parameters types see: https://api.adoptium.net/q/swagger-ui/#/Assets/searchReleasesByVersion
     *
     * @param releaseVersionName Example: 11.0.4.1+11.1
     * @param isLargeHeapSize    If true allows your jvm to use more that 57gb of ram.
     * @param isHotspotImpl      If true uses hotspot, otherwise the openj9 implementation.
     * @param isOnlyLTS          If true only shows LTS (Long Term Support) releases.
     * @param maxItems           Example: 20
     * @return
     */
    public String getVersionInformationUrl(String releaseVersionName, OperatingSystemArchitectureType osArchitectureType, boolean isLargeHeapSize, ImageType imageType,
                                           boolean isHotspotImpl, boolean isOnlyLTS, OperatingSystemType osType, int maxItems,
                                           VendorProjectType vendorProject, ReleaseType releaseType) {
        String jvmImplementation = isHotspotImpl ? "hotspot" : "openj9";
        String heapSize = isLargeHeapSize ? "large" : "normal";
        return START_ASSETS_URL
                + "%5B" + releaseVersionName.replace("+", "%2B") + "%2C%29"
                + "?architecture=" + osArchitectureType.name
                + "&heap_size=" + heapSize
                + "&image_type=" + imageType.name
                + "&jvm_impl=" + jvmImplementation
                + "&lts=" + isOnlyLTS
                + "&os=" + osType.name
                + "&page=0"
                + "&page_size=" + maxItems
                + "&project=" + vendorProject.name
                + "&release_type=" + releaseType.name
                + "&sort_method=DEFAULT&sort_order=DESC"
                + "&vendor=eclipse";
    }

    public JsonArray getVersionInformation(String releaseVersionName, OperatingSystemArchitectureType osArchitectureType, boolean isLargeHeapSize, ImageType imageType,
                                           boolean isHotspotImpl, boolean isOnlyLTS, OperatingSystemType osType, int maxItems,
                                           VendorProjectType vendorProject, ReleaseType releaseType) throws IOException {
        return Json.fromUrlAsJsonArray(getVersionInformationUrl(
                releaseVersionName, osArchitectureType, isLargeHeapSize, imageType, isHotspotImpl,
                isOnlyLTS, osType, maxItems, vendorProject, releaseType
        ));
    }

    /**
     * Creates and returns a new url from the provided parameters. <br>
     * For a list of all available parameters types see: https://api.adoptium.net/q/swagger-ui/#/Release%20Info/getReleaseVersions
     *
     * @param isLargeHeapSize If true allows your jvm to use more that 57gb of ram.
     * @param isHotspotImpl   If true uses hotspot, otherwise the openj9 implementation.
     * @param isOnlyLTS       If true only shows LTS (Long Term Support) releases.
     * @param maxItems        Example: 20
     * @return
     */
    public String getReleasesUrl(OperatingSystemArchitectureType osArchitectureType, boolean isLargeHeapSize, ImageType imageType,
                                 boolean isHotspotImpl, boolean isOnlyLTS, OperatingSystemType osType, int maxItems,
                                 VendorProjectType vendorProject, ReleaseType releaseType) {
        String jvmImplementation = isHotspotImpl ? "hotspot" : "openj9";
        String heapSize = isLargeHeapSize ? "large" : "normal";
        return START_RELEASES_URL
                + osArchitectureType.name
                + "&heap_size=" + heapSize
                + "&image_type=" + imageType.name
                + "&jvm_impl=" + jvmImplementation
                + "&lts=" + isOnlyLTS
                + "&os=" + osType.name
                + "&page=0"
                + "&page_size=" + maxItems
                + "&project=" + vendorProject.name
                + "&release_type=" + releaseType.name
                + "&sort_method=DEFAULT&sort_order=DESC"
                + "&vendor=eclipse";
    }

    public JsonObject getReleases(OperatingSystemArchitectureType osArchitectureType, boolean isLargeHeapSize, ImageType imageType,
                                  boolean isHotspotImpl, boolean isOnlyLTS, OperatingSystemType osType, int maxItems,
                                  VendorProjectType vendorProject, ReleaseType releaseType) throws IOException {
        return Json.fromUrlAsObject(getReleasesUrl(osArchitectureType, isLargeHeapSize, imageType,
                isHotspotImpl, isOnlyLTS, osType, maxItems, vendorProject, releaseType));
    }

    /**
     * Creates and returns a new url from the provided parameters. <br>
     * For a list of all available parameters types see: https://api.adoptium.net/q/swagger-ui/#/Binary/getBinaryByVersion
     *
     * @param releaseName     Note that this is not the regular version name. Example: jdk-15.0.2+7
     * @param isHotspotImpl   If true uses hotspot, otherwise the openj9 implementation.
     * @param isLargeHeapSize If true allows your jvm to use more that 57gb of ram.
     */
    public String getDownloadUrl(String releaseName, OperatingSystemType osType, OperatingSystemArchitectureType osArchitectureType,
                                 ImageType imageType, boolean isHotspotImpl, boolean isLargeHeapSize,
                                 VendorProjectType vendorProject) {
        String jvmImplementation = isHotspotImpl ? "hotspot" : "openj9";
        String heapSize = isLargeHeapSize ? "large" : "normal";
        return START_DOWNLOAD_URL
                + releaseName + "/"
                + osType.name + "/"
                + osArchitectureType.name + "/"
                + imageType.name + "/"
                + jvmImplementation + "/"
                + heapSize + "/"
                + "eclipse?project=" + vendorProject.name;
    }


    // ENUMS:


    public enum VendorProjectType {
        JDK("jdk"),
        VALHALLA("valhalla"),
        METROPOLIS("metropolis"),
        JFR("jfr"),
        SHENANDOAH("shenandoah");

        private final String name;

        VendorProjectType(String name) {
            this.name = name;
        }
    }

    public enum ImageType {
        JDK("jdk"),
        JRE("jre"),
        TEST_IMAGE("testimage"),
        DEBUG_IMAGE("debugimage"),
        STATIC_LIBS("staticlibs");

        private final String name;

        ImageType(String name) {
            this.name = name;
        }
    }

    public enum OperatingSystemArchitectureType {
        X64("x64"),
        X86("x86"),
        X32("x32"),
        PPC64("ppc64"),
        PPC64LE("ppc64le"),
        S390X("s390x"),
        AARCH64("aarch64"),
        ARM("arm"),
        SPARCV9("sparcv9"),
        RISCV64("riscv64"),
        // x64 with alternative names:
        AMD64("x64"),
        X86_64("x64"),
        // x32 with alternative names:
        I386("x32"),
        // AARCHx64 with alternative names:
        ARM64("aarch64");

        public final String name;

        OperatingSystemArchitectureType(String name) {
            this.name = name;
        }
    }

    public enum OperatingSystemType {
        LINUX("linux"),
        WINDOWS("windows"),
        MAC("mac"),
        SOLARIS("solaris"),
        AIX("aix"),
        ALPINE_LINUX("alpine-linux");

        private final String name;

        OperatingSystemType(String name) {
            this.name = name;
        }
    }

    public enum ReleaseType {
        GENERAL_AVAILABILITY("ga"),
        EARLY_ACCESS("ea");

        private final String name;

        ReleaseType(String name) {
            this.name = name;
        }
    }
}
