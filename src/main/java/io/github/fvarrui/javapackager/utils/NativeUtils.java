package io.github.fvarrui.javapackager.utils;

import java.io.File;

public class NativeUtils {
    public static File getUserTempFolder(){
        return new File(System.getProperty("java.io.tmpdir"));
    }
}
