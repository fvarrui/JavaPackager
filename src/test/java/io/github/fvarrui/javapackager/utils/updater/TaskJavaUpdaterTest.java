package io.github.fvarrui.javapackager.utils.updater;

import io.github.fvarrui.javapackager.model.Platform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskJavaUpdaterTest {
    @Test
    void execute() throws Exception {
        TaskJavaUpdater taskJavaUpdater = new TaskJavaUpdater(Platform.getCurrentPlatform());
        taskJavaUpdater.execute("8", "adoptium");
        assertNotNull(taskJavaUpdater.jdkPath);
        assertTrue(taskJavaUpdater.jdkPath.listFiles().length != 0);
        System.out.println(taskJavaUpdater.jdkPath);
    }
}