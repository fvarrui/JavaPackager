package io.github.fvarrui.javapackager.utils.updater;

import io.github.fvarrui.javapackager.model.Platform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskJavaUpdaterTest {
    @Test
    void testWindows() throws Exception {
        TaskJavaUpdater taskJavaUpdater = new TaskJavaUpdater(Platform.windows);
        taskJavaUpdater.execute("8", "adoptium");
        assertNotNull(taskJavaUpdater.jdkPath);
        assertTrue(taskJavaUpdater.jdkPath.listFiles().length != 0);
        System.out.println(taskJavaUpdater.jdkPath);
    }

    @Test
    void testLinux() throws Exception {
        TaskJavaUpdater taskJavaUpdater = new TaskJavaUpdater(Platform.linux);
        taskJavaUpdater.execute("8", "adoptium");
        assertNotNull(taskJavaUpdater.jdkPath);
        assertTrue(taskJavaUpdater.jdkPath.listFiles().length != 0);
        System.out.println(taskJavaUpdater.jdkPath);
    }

    @Test
    void testMac() throws Exception {
        TaskJavaUpdater taskJavaUpdater = new TaskJavaUpdater(Platform.mac);
        taskJavaUpdater.execute("8", "adoptium");
        assertNotNull(taskJavaUpdater.jdkPath);
        assertTrue(taskJavaUpdater.jdkPath.listFiles().length != 0);
        System.out.println(taskJavaUpdater.jdkPath);
    }
}