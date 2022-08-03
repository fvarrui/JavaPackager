package io.github.fvarrui.javapackager.ideas;

import io.github.fvarrui.javapackager.gradle.AbstractPackageTask;
import io.github.fvarrui.javapackager.packagers.Packager;

public class MyGradleTask extends AbstractPackageTask implements MySettings{
    @Override
    protected Packager createPackager() throws Exception {
        return null;
    }
}
