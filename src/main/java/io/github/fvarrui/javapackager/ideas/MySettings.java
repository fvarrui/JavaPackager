package io.github.fvarrui.javapackager.ideas;

import org.apache.maven.plugins.annotations.Parameter;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public interface MySettings {
    boolean IS_GRADLE = false;

    @Parameter(defaultValue = "${project.name}", property = "name", required = false)
    @Input
    @Optional
    default String getName(){ return (IS_GRADLE ? "default-gradle-name" : "${project.name}");}
}
