package io.github.fvarrui.javapackager.utils.updater;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Github {

    /**
     * Searches the latest GitHub release and returns a {@link SearchResult} object with all the relevant information.
     *
     * @param repoName           GitHub repository name.
     * @param currentVersion     current version of the installed software.
     * @param assetNamePredicate predicate that contains the asset name and ist used to determine the asset to download.
     */
    public static SearchResult searchUpdate(String repoName, String currentVersion, Predicate<String> assetNamePredicate) {
        Exception exception = null;
        boolean updateAvailable = false;
        String downloadUrl = null;
        String latestVersion = null;
        String downloadFile = null;
        String sha256 = null;
        try {
            JsonObject latestRelease = Json.fromUrlAsObject("https://api.github.com/repos/" + repoName + "/releases/latest");
            latestVersion = latestRelease.get("tag_name").getAsString();
            if (latestVersion != null)
                latestVersion = latestVersion.replaceAll("[^0-9.]", ""); // Before passing over remove everything except numbers and dots
            if (new UtilsVersion().isLatestBigger(currentVersion, latestVersion)) {
                updateAvailable = true;
                // Contains JsonObjects sorted by their asset-names lengths, from smallest to longest.
                // The following does that sorting.
                List<JsonObject> sortedArtifactObjects = new ArrayList<>();
                for (JsonElement e :
                        latestRelease.getAsJsonArray("assets")) {
                    JsonObject obj = e.getAsJsonObject();
                    String name = obj.get("name").getAsString();
                    if (sortedArtifactObjects.size() == 0) sortedArtifactObjects.add(obj);
                    else {
                        int finalIndex = 0;
                        boolean isSmaller = false;
                        for (int i = 0; i < sortedArtifactObjects.size(); i++) {
                            String n = sortedArtifactObjects.get(i).get("name").getAsString();
                            if (name.length() < n.length()) {
                                isSmaller = true;
                                finalIndex = i;
                                break;
                            }
                        }
                        if (!isSmaller) sortedArtifactObjects.add(obj);
                        else sortedArtifactObjects.add(finalIndex, obj);
                    }
                }

                // Find asset-name containing our provided asset-name
                for (JsonObject obj : sortedArtifactObjects) {
                    String name = obj.get("name").getAsString();
                    if (assetNamePredicate.test(name)) {
                        downloadFile = name;
                        downloadUrl = obj.get("browser_download_url").getAsString();
                        break;
                    }
                }

                if (downloadUrl == null) {
                    List<String> names = new ArrayList<>();
                    for (JsonObject obj :
                            sortedArtifactObjects) {
                        String n = obj.get("name").getAsString();
                        names.add(n);
                    }
                    throw new Exception("Failed to find an asset-name matching the assetNamePredicate inside of " + Arrays.toString(names.toArray()));
                }

                // Determine sha256
                String expectedShaAssetName = downloadFile + ".sha256";
                for (JsonObject obj : sortedArtifactObjects) {
                    String name = obj.get("name").getAsString();
                    if (name.equals(expectedShaAssetName)) {
                        sha256 = IOUtils.toString(new URL(obj.get("browser_download_url").getAsString()), StandardCharsets.UTF_8);
                        break;
                    }
                }

            }
        } catch (Exception e) {
            exception = e;
        }

        return new SearchResult(updateAvailable, exception, latestVersion, downloadUrl, downloadFile, sha256);
    }
}
