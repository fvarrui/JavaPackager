package io.github.fvarrui.javapackager.packagers;

import io.github.fvarrui.javapackager.PackageTask;
import org.apache.commons.lang3.SystemUtils;

public interface PackagerFactory {
    default Packager createPackager(PackageTask task) {
        Packager packager = null;
        switch (task.getPlatform()) {
            case mac:
                packager = new MacPackager(task);
                break;
            case linux:
                packager = new LinuxPackager(task);
                break;
            case windows:
                packager = new WindowsPackager(task);
                break;
            default:
                throw new RuntimeException("Unsupported operating system: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH);
        }
        return packager;
    }
}
