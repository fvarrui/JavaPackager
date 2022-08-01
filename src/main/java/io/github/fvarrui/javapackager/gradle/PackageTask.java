package io.github.fvarrui.javapackager.gradle;

import static io.github.fvarrui.javapackager.utils.ObjectUtils.defaultIfBlank;
import static io.github.fvarrui.javapackager.utils.ObjectUtils.defaultIfNull;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.github.fvarrui.javapackager.packagers.PackagerSettings;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

import groovy.lang.Closure;
import io.github.fvarrui.javapackager.model.FileAssociation;
import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.Manifest;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.model.Scripts;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFactory;

/**
 * Packaging task fro Gradle 
 */
public class PackageTask extends AbstractPackageTask {
	public GradlePackagerSettings settings = new GradlePackagerSettings(this);

	@SuppressWarnings("unchecked")
	@Override
	protected Packager createPackager() throws Exception {
		return settings.createPackager();
	}
	
}
