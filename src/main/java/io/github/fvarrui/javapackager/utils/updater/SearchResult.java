package io.github.fvarrui.javapackager.utils.updater;

public class SearchResult {
    public boolean isUpdateAvailable;
    public Exception exception;
    public String latestVersion;
    public String downloadUrl;
    public String downloadFileExtension;
    public String sha256;

    public SearchResult(boolean isUpdateAvailable, Exception exception, String latestVersion, String downloadUrl, String downloadFileExtension, String sha256) {
        this.isUpdateAvailable = isUpdateAvailable;
        this.exception = exception;
        this.latestVersion = latestVersion;
        this.downloadUrl = downloadUrl;
        this.downloadFileExtension = downloadFileExtension;
        this.sha256 = sha256;
    }
}
