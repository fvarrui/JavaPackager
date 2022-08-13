package io.github.fvarrui.javapackager.generator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassFile extends File {
    public String packageName;
    public File baseDir;

    public PluginClassFile(@NotNull String pathname, String packageName, File baseDir) {
        super(pathname);
        this.packageName = packageName;
        this.baseDir = baseDir;
    }

    public Class<?> loadClass() throws MalformedURLException, ClassNotFoundException {
        URL url = baseDir.toURI().toURL();
        URL[] urls = new URL[]{url};
        ClassLoader cl = new URLClassLoader(urls);
        return cl.loadClass(packageName);
    }
}
