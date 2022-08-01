package io.github.fvarrui.javapackager.utils;

import java.io.File;

public class NativeUtils {
    /**
     * @return JavaPackager temp directory. Example on Windows: <br>
     * C:\Users\Name\AppData\Local\Temp\JavaPackager
     */
    public static File getUserTempFolder(){
        return new File(System.getProperty("java.io.tmpdir") +"/JavaPackager");
    }
}
