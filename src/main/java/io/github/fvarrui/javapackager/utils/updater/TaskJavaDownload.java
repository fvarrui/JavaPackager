/*
 * Copyright (c) 2021-2022 Osiris-Team.
 * All rights reserved.
 *
 * This software is copyrighted work, licensed under the terms
 * of the MIT-License. Consult the "LICENSE" file for details.
 */

package io.github.fvarrui.javapackager.utils.updater;

import io.github.fvarrui.javapackager.utils.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskJavaDownload {
    private File newDest;
    private boolean isTar;
    private String url;
    private File dest;
    private AdoptV3API.OperatingSystemType osType;

    /**
     * @param url     the download-url.
     * @param dest    the downloads final destination. Note that the file name must end with '.file', because
     *                the actual file type gets set when there is download information available.
     */
    public void execute(String url, File dest, AdoptV3API.OperatingSystemType osType) throws Exception {
        this.url = url;
        this.dest = dest;
        this.osType = osType;

        String fileName = dest.getName();
        Logger.info("Fetching file " + fileName + " from: " + url);

        Request request = new Request.Builder().url(url)
                .header("User-Agent", "AutoPlug Client/" + new Random().nextInt() + " - https://autoplug.one")
                .build();
        Response response = new OkHttpClient().newCall(request).execute();
        ResponseBody body = null;
        try {
            if (response.code() != 200)
                throw new Exception("Download of '" + fileName + "' failed! Code: " + response.code() + " Message: " + response.message() + " Url: " + url);

            body = response.body();
            if (body == null)
                throw new Exception("Download of '" + fileName + "' failed because of null response body!");
            else if (body.contentType() == null)
                throw new Exception("Download of '" + fileName + "' failed because of null content type!");
            else if (!body.contentType().type().equals("application"))
                throw new Exception("Download of '" + fileName + "' failed because of invalid content type: " + body.contentType().type());
            else if (!body.contentType().subtype().equals("java-archive")
                    && !body.contentType().subtype().equals("jar")
                    && !body.contentType().subtype().equals("octet-stream")
                    && !body.contentType().subtype().equals("x-gtar") // ADDITIONS FOR JAVA DOWNLOADS
                    && !body.contentType().subtype().equals("zip"))
                throw new Exception("Download of '" + fileName + "' failed because of invalid sub-content type: " + body.contentType().subtype());

            // Set the file name
            if (body.contentType().subtype().equals("x-gtar")) {
                isTar = true;
                fileName = fileName.replace(".file", ".tar.gz");
            } else {
                // In this case we check the response header for file information
                // Example: (content-disposition, attachment; filename=JDK15U-jre_x86-32_windows_hotspot_15.0.2_7.zip)
                String contentDispo = response.headers().get("content-disposition");
                if (contentDispo == null)
                    throw new Exception("Failed to determine download file type!");

                if (contentDispo.contains(".tar.gz")) {
                    isTar = true;
                    fileName = fileName.replace(".file", ".tar.gz");
                } else {
                    Pattern p = Pattern.compile("[.][^.]+$"); // Returns the file extension with dot. example.txt -> .txt
                    Matcher m = p.matcher(contentDispo);
                    if (m.find()) {
                        String fileExtension = m.group();
                        fileName = fileName.replace(".file", fileExtension);
                    } else
                        throw new Exception("Failed to determine download file type! Download-Url: " + contentDispo);
                }
            }


            // We need to at least create the cache dest to then rename it
            if (dest.exists()) dest.delete();
            dest.getParentFile().mkdirs();
            dest.createNewFile();

            // The actual file with the correct file extension
            newDest = new File(dest.getParentFile().getAbsolutePath() + "/" + fileName);
            if (newDest.exists()) newDest.delete();
            newDest.getParentFile().mkdirs();
            newDest.createNewFile();

            long completeFileSize = body.contentLength();

            BufferedInputStream in = new BufferedInputStream(body.byteStream());
            FileOutputStream fos = new FileOutputStream(dest);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] data = new byte[1024];
            long downloadedFileSize = 0;
            int x = 0;
            Logger.info("Downloading " + fileName + " with " + completeFileSize / (1024 * 1024) + "mb. This may take a bit...");
            while ((x = in.read(data, 0, 1024)) >= 0) {
                downloadedFileSize += x;
                bout.write(data, 0, x);
            }

            Logger.info("Downloaded " + fileName + " (" + downloadedFileSize / (1024 * 1024) + "mb/" + completeFileSize / (1024 * 1024) + "mb)");
            bout.close();
            in.close();
            body.close();
            response.close();

            Files.copy(dest.toPath(), newDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            if (body != null) body.close();
            response.close();
            throw e;
        }
    }

    /**
     * Retrieve this once the task finished to get a correct result.
     */
    public boolean isTar() {
        return isTar;
    }

    public File getNewCacheDest() {
        return newDest;
    }

    /**
     * Only use this method after finishing the download.
     * It will get the hash for the newly downloaded file and
     * compare it with the given hash.
     *
     * @param sha256
     * @return true if the hashes match
     */
    public boolean compareWithSHA256(String sha256) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    FileUtils.readFileToByteArray(dest));
            final String hashResult = bytesToHex(encodedhash);
            Logger.debug("Comparing hashes (SHA-256):");
            Logger.debug("Expected hash: " + sha256);
            Logger.debug("Actual hash: " + hashResult);
            return hashResult.equals(sha256);
        } catch (Exception e) {
            Logger.error("Failed to compare hashes.", e);
            return false;
        }

    }

    @NotNull
    private String bytesToHex(@NotNull byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
