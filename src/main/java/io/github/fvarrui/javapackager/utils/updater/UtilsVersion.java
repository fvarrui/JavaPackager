package io.github.fvarrui.javapackager.utils.updater;

import java.util.Objects;

public class UtilsVersion {

    /**
     * Compares the current version with the latest
     * version and returns true if the latest version is
     * bigger than the current version.
     */
    public boolean isLatestBigger(String currentVersion, String latestVersion) {
        try {
            String[] arrCurrent = cleanAndSplitByDots(currentVersion);
            String[] arrLatest = cleanAndSplitByDots(latestVersion);

            if (arrLatest.length == arrCurrent.length) {
                int latest, current;
                for (int i = 0; i < arrLatest.length; i++) {
                    latest = Integer.parseInt(arrLatest[i]);
                    current = Integer.parseInt(arrCurrent[i]);
                    if (latest == current) continue;
                    else return latest > current;
                }
                return false; // All are the same
            } else return arrLatest.length > arrCurrent.length;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Compares the current version with the latest
     * version and returns true if the latest version is
     * bigger or equal to the current version.
     */
    public boolean isLatestBiggerOrEqual(String currentVersion, String latestVersion) {
        try {
            String[] arrCurrent = cleanAndSplitByDots(currentVersion);
            String[] arrLatest = cleanAndSplitByDots(latestVersion);

            if (arrLatest.length == arrCurrent.length) {
                int latest, current;
                for (int i = 0; i < arrLatest.length; i++) {
                    latest = Integer.parseInt(arrLatest[i]);
                    current = Integer.parseInt(arrCurrent[i]);
                    if (latest == current) continue;
                    else return latest >= current;
                }
                return true; // All are the same
            } else return arrLatest.length >= arrCurrent.length;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String[] cleanAndSplitByDots(String version) throws Exception {
        Objects.requireNonNull(version);
        version = version.trim() // Remove left and right spaces
                .replaceAll("[^0-9.]", ""); // Remove everything except numbers and dots
        if (version.isEmpty()) throw new Exception("Empty version string!");
        return version.split("\\."); // Split string by .
    }

}
