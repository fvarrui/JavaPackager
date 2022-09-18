package io.github.fvarrui.javapackager.utils;

import java.io.File;
import java.nio.file.NotDirectoryException;

public class GraalVM {
    public File dir;

    public GraalVM(File dir) throws NotDirectoryException {
        if(!dir.isDirectory()) throw new NotDirectoryException(dir.toString());
        this.dir = dir;
    }

}
