package io.github.fvarrui.javapackager.utils.updater;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsVersionTest {

    @Test
    void isLatestBigger() {
        assertTrue(new UtilsVersion().isLatestBigger("v1.0.0", "v1.0.1"));
        assertFalse(new UtilsVersion().isLatestBigger("v1.0.0", "v1.0.0"));
    }

    @Test
    void isLatestBiggerOrEqual() {
        assertTrue(new UtilsVersion().isLatestBiggerOrEqual("v1.0.0", "v1.0.1"));
        assertTrue(new UtilsVersion().isLatestBiggerOrEqual("v1.0.0", "v1.0.0"));
    }
}