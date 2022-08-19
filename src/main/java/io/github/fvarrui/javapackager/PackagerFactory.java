package io.github.fvarrui.javapackager;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.packagers.LinuxPackager;
import io.github.fvarrui.javapackager.packagers.MacPackager;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.WindowsPackager;
import org.apache.commons.lang3.SystemUtils;

public interface PackagerFactory {
    default Packager createPackager(PackageTask task){
        Packager packager = null;
        switch (Platform.getCurrentPlatform()) {
            case mac:
                packager = new MacPackager(task); break;
            case linux:
                packager = new LinuxPackager(task); break;
            case windows:
                packager = new WindowsPackager(task); break;
            default:
                throw new RuntimeException("Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);
        }
        return packager;
    }
}
