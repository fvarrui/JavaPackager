/*
 * Copyright (c) 2021-2022 Osiris-Team.
 * All rights reserved.
 *
 * This software is copyrighted work, licensed under the terms
 * of the MIT-License. Consult the "LICENSE" file for details.
 */

package io.github.fvarrui.javapackager.utils.updater;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.Const;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.NativeUtils;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Searches for updates and installs them is AUTOMATIC profile is selected.
 */
public class TaskJavaUpdater {

    public File downloadsDir = new File(NativeUtils.getUserTempFolder() + "/downloads");
    public File jdkPath;
    public Platform platform;
    public AdoptV3API.OperatingSystemType osType;

    public TaskJavaUpdater(Platform platform) {
        this.platform = platform;
        switch (platform) {
            case linux:
                jdkPath = new File(NativeUtils.getUserTempFolder() + "/jdk/linux");
                osType = AdoptV3API.OperatingSystemType.LINUX;
                break;
            case mac:
                jdkPath = new File(NativeUtils.getUserTempFolder() + "/jdk/mac");
                osType = AdoptV3API.OperatingSystemType.MAC;
                break;
            case windows:
                jdkPath = new File(NativeUtils.getUserTempFolder() + "/jdk/win");
                osType = AdoptV3API.OperatingSystemType.WINDOWS;
                break;
            default:
                throw new RuntimeException();
        }
        jdkPath.mkdirs();
    }

    public void execute(String javaVersion, String javaVendor) throws Exception {
        Objects.requireNonNull(javaVersion);
        Objects.requireNonNull(javaVendor);
        if (javaVendor.equals(Const.graalvm)) {
            Logger.info("Checking java installation...");

            String currentVersion = getBuildID(javaVersion, javaVendor);
            String osName = (platform.equals(Platform.linux) ? "linux" :
                    platform.equals(Platform.mac) ? "darwin" :
                            platform.equals(Platform.windows) ? "windows" :
                                    null);
            Objects.requireNonNull(osName);
            Pattern pattern = Pattern.compile("(java)(\\d+)"); // matches java11 or java17 for example
            SearchResult result = Github.searchUpdate("graalvm/graalvm-ce-builds", currentVersion,
                    assetName -> assetName.contains(osName)
                            && assetName.contains("amd64")
                            && new UtilsVersion().isLatestBiggerOrEqual(javaVersion, pattern.matcher(assetName).group())
                            && !assetName.endsWith(".sha256")
                            && !assetName.endsWith(".jar"));
            if (result.exception != null) throw result.exception;
            if (!result.isUpdateAvailable) {
                Logger.info("Your Java installation is on the latest version!");
                return;
            }

            if (result.downloadUrl == null) {
                Logger.error("Couldn't find a matching asset to download.");
                return;
            }

            download(result.downloadUrl, result.sha256,
                    currentVersion, result.latestVersion,
                    javaVersion, javaVendor);

        } else if (javaVendor.equals(Const.adoptium)) {
            Logger.info("Checking java installation...");

            AdoptV3API.OperatingSystemArchitectureType osArchitectureType = AdoptV3API.OperatingSystemArchitectureType.X64;
            int currentBuildId = Integer.parseInt(getBuildID(javaVersion, javaVendor));
            AdoptV3API.ImageType imageType = AdoptV3API.ImageType.JDK;

            JsonObject jsonReleases = new AdoptV3API().getReleases(
                    osArchitectureType,
                    false,
                    imageType,
                    true,
                    true, // Changing this to false makes the api return even fewer versions, which is pretty weird.
                    osType,
                    20,
                    AdoptV3API.VendorProjectType.JDK,
                    AdoptV3API.ReleaseType.GENERAL_AVAILABILITY
            );

            JsonObject jsonLatestRelease = null;
            for (JsonElement e :
                    jsonReleases.getAsJsonArray("versions")) {
                JsonObject o = e.getAsJsonObject();
                if (o.get("major").getAsString().equals(javaVersion)) {
                    jsonLatestRelease = o;
                    break;
                }
            }

            if (jsonLatestRelease == null) {
                Logger.error("Couldn't find a matching major version to '" + javaVersion + "'.");
                return;
            }

            int latestBuildId = jsonLatestRelease.get("build").getAsInt();
            if (latestBuildId <= currentBuildId) {
                Logger.info("Your Java installation is on the latest version!");
                return;
            }

            // semver = the version string like: 11.0.0+28 for example // Not a typo ^-^
            String versionString = jsonLatestRelease.get("semver").toString().replace("\"", ""); // Returns with apostrophes ""

            JsonArray jsonVersionDetails = new AdoptV3API().getVersionInformation(
                    versionString,
                    osArchitectureType,
                    false,
                    imageType,
                    true,
                    true,
                    osType,
                    20,
                    AdoptV3API.VendorProjectType.JDK,
                    AdoptV3API.ReleaseType.GENERAL_AVAILABILITY);

            String checksum = jsonVersionDetails.get(0).getAsJsonObject().getAsJsonArray("binaries")
                    .get(0).getAsJsonObject().get("package").getAsJsonObject().get("checksum").getAsString();

            // The release name that can be used to retrieve the download link
            String releaseName = jsonVersionDetails.get(0).getAsJsonObject().get("release_name").getAsString();
            String downloadURL = new AdoptV3API().getDownloadUrl(
                    releaseName,
                    osType,
                    osArchitectureType,
                    imageType,
                    true,
                    false,
                    AdoptV3API.VendorProjectType.JDK
            );

            download(downloadURL, checksum,
                    "" + currentBuildId, "" + latestBuildId,
                    javaVersion, javaVendor);

        } else {
            throw new IllegalArgumentException("The provided Java vendor '" + javaVendor + "' is currently not supported!" +
                    " Supported: " + Const.adoptium + " and " + Const.graalvm + ".");
        }
    }

    private void download(String downloadURL, String expectedSha256,
                          String currentVersion, String latestVersion,
                          String javaVersion, String javaVendor) throws Exception {
        Logger.info("Update found " + currentVersion + " -> " + latestVersion);
        File final_dir_dest = jdkPath;
        File cache_dest = new File(downloadsDir + "/" + javaVendor + "-" + javaVersion + "-" + latestVersion + ".file");
        TaskJavaDownload download = new TaskJavaDownload();
        download.execute(downloadURL, cache_dest, osType);

        Logger.info("Java update downloaded. Checking hash...");
        if (!download.compareWithSHA256(expectedSha256))
            throw new IOException("Hash of downloaded Java update is not valid!");
        Logger.info("Hash is valid, removing old installation...");
        FileUtils.deleteDirectory(final_dir_dest);
        final_dir_dest.mkdirs();

        Archiver archiver;
        if (download.isTar())
            archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        else // A zip
            archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);

        // The zip/archive contains another folder inside like /jdk8+189
        // thus we need to move that folders content to its parent dir
        archiver.extract(download.getNewCacheDest(), final_dir_dest);
        setBuildID("" + latestVersion, javaVersion, javaVendor);
        File actualJdkPath = null;
        for (File file : jdkPath.listFiles()) {
            if (file.isDirectory()) {
                actualJdkPath = file;
                break;
            }
        }
        for (File file : actualJdkPath.listFiles()) {
            Files.move(file, new File(jdkPath + "/" + file.getName()));
        }
        FileUtils.deleteDirectory(actualJdkPath);
        FileUtils.deleteDirectory(downloadsDir);
        Logger.info("Java update was installed successfully (" + currentVersion + " -> " + latestVersion + ") at " + jdkPath);
    }

    private String getFileNameWithoutID(String javaVersion, String javaVendor) {
        return "java_packager_jdk_" + javaVersion + "_" + javaVendor + "_build_id";
    }

    private String getBuildID(String javaVersion, String javaVendor) throws IOException {
        for (File file : jdkPath.listFiles()) {
            if (file.getName().startsWith(getFileNameWithoutID(javaVersion, javaVendor))) {
                return file.getName().split(" ")[1];
            }
        }
        setBuildID("0", javaVersion, javaVendor);
        return "0";
    }

    private void setBuildID(String id, String javaVersion, String javaVendor) throws IOException {
        for (File file : jdkPath.listFiles()) {
            if (file.getName().startsWith(getFileNameWithoutID(javaVersion, javaVendor))) {
                file.delete();
            }
        }
        File file = new File(jdkPath + "/" + getFileNameWithoutID(javaVersion, javaVendor) + " " + id);
        file.createNewFile();
    }

}
