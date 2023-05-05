package io.github.fvarrui.javapackager.gradle;

import java.util.ArrayList;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

import io.github.fvarrui.javapackager.model.LinuxConfig;

public abstract class LinuxTaskConfig {
    @Input
    @Optional
    public abstract ListProperty<String> getCategories();
    @Input
    @Optional
    public abstract Property<Boolean> isGenerateDeb();
    @Input
    @Optional
    public abstract Property<Boolean> isGenerateRpm();
    @Input
    @Optional
    public abstract Property<Boolean> isGenerateAppImage();;
    @Input
    @Optional
    public abstract RegularFileProperty getPngFile();
    @Input
    @Optional
    public abstract Property<Boolean> isWrapJar();

    public LinuxConfig buildConfig() {
        LinuxConfig ret = new LinuxConfig();
        ret.setCategories(getCategories().getOrElse(new ArrayList<>()));
        ret.setGenerateDeb(isGenerateDeb().getOrElse(true));
        ret.setGenerateRpm(isGenerateRpm().getOrElse(true));
        ret.setGenerateAppImage(isGenerateAppImage().getOrElse(true));
        ret.setPngFile(getPngFile().getAsFile().getOrNull());
        ret.setWrapJar(isWrapJar().getOrElse(true));
        return ret;
    }
}
