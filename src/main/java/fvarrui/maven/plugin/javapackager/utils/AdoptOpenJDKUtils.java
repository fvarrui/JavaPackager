package fvarrui.maven.plugin.javapackager.utils;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang.SystemUtils;

public class AdoptOpenJDKUtils {
	
	public static String getDownloadUrl(URL jreUrl) {
		String urlFile = new File(jreUrl.getFile()).getName();
		String jdk = urlFile.substring(0, urlFile.indexOf("-")).toUpperCase();
		String version = urlFile.substring(urlFile.indexOf("-") + 1);
		
		String os = SystemUtils.IS_OS_WINDOWS ? "windows" : SystemUtils.IS_OS_LINUX ? "linux" : "mac";
		String extension = SystemUtils.IS_OS_WINDOWS ? "zip" : "tar.gz";

		String file = "Open" + jdk + "-jre_x64_" + os + "_hotspot_" + version + "." + extension;		
		return jreUrl.toExternalForm().replace("tag", "download") + "/" + file;
	}

}
