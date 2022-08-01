package io.github.fvarrui.javapackager.gradle;

import groovy.lang.Closure;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.Manifest;
import io.github.fvarrui.javapackager.model.Scripts;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Context;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFactory;
import io.github.fvarrui.javapackager.packagers.PackagerSettings;

import static io.github.fvarrui.javapackager.utils.ObjectUtils.defaultIfBlank;
import static io.github.fvarrui.javapackager.utils.ObjectUtils.defaultIfNull;

public class GradlePackagerSettings extends PackagerSettings {
    PackageTask packageTask;

    public GradlePackagerSettings(PackageTask packageTask) {
        this.packageTask = packageTask;
    }

    public MacConfig macConfig(Closure<MacConfig> closure) {
        macConfig = new MacConfig();
        packageTask.getProject().configure(macConfig, closure);
        return macConfig;
    }

    public WindowsConfig winConfig(Closure<WindowsConfig> closure) {
        winConfig = new WindowsConfig();
        packageTask.getProject().configure(winConfig, closure);
        return winConfig;
    }

    public Manifest manifest(Closure<Manifest> closure) {
        manifest = new Manifest();
        packageTask.getProject().configure(manifest, closure);
        return manifest;
    }

    public Scripts scripts(Closure<Scripts> closure) {
        scripts = new Scripts();
        packageTask.getProject().configure(scripts, closure);
        return scripts;
    }

    @SuppressWarnings("unchecked")
    public Packager createPackager() throws Exception {

        PackagePluginExtension extension = packageTask.getProject().getExtensions().findByType(PackagePluginExtension.class);

        return
                (Packager) PackagerFactory
                        .createPackager(defaultIfNull(platform, extension.getPlatform()))
                        .additionalModules(defaultIfNull(additionalModules, extension.getAdditionalModules()))
                        .additionalModulePaths(defaultIfNull(additionalModulePaths, extension.getAdditionalModulePaths()))
                        .additionalResources(defaultIfNull(additionalResources, extension.getAdditionalResources()))
                        .administratorRequired(defaultIfNull(administratorRequired, extension.getAdministratorRequired()))
                        .assetsDir(defaultIfNull(assetsDir, extension.getAssetsDir()))
                        .bundleJre(defaultIfNull(bundleJre, extension.getBundleJre()))
                        .classpath(defaultIfNull(classpath, extension.getClasspath()))
                        .copyDependencies(defaultIfNull(copyDependencies, extension.getCopyDependencies()))
                        .createTarball(defaultIfNull(createTarball, extension.getCreateTarball()))
                        .createZipball(defaultIfNull(createZipball, extension.getCreateZipball()))
                        .customizedJre(defaultIfNull(customizedJre, extension.getCustomizedJre()))
                        .description(defaultIfNull(description, extension.getDescription()))
                        .displayName(defaultIfNull(displayName, extension.getDisplayName()))
                        .envPath(defaultIfNull(envPath, extension.getEnvPath()))
                        .extra(defaultIfNull(extra, extension.getExtra()))
                        .fileAssociations(defaultIfNull(fileAssociations, extension.getFileAssociations()))
                        .forceInstaller(defaultIfNull(forceInstaller, extension.isForceInstaller()))
                        .generateInstaller(defaultIfNull(generateInstaller, extension.getGenerateInstaller()))
                        .jdkPath(defaultIfNull(jdkPath, extension.getJdkPath()))
                        .jreDirectoryName(defaultIfBlank(jreDirectoryName, extension.getJreDirectoryName()))
                        .jreMinVersion(defaultIfBlank(jreMinVersion, extension.getJreMinVersion()))
                        .jrePath(defaultIfNull(jrePath, extension.getJrePath()))
                        .licenseFile(defaultIfNull(licenseFile, extension.getLicenseFile()))
                        .linuxConfig(defaultIfNull(linuxConfig, extension.getLinuxConfig()))
                        .macConfig(defaultIfNull(macConfig, extension.getMacConfig()))
                        .mainClass(defaultIfNull(mainClass, extension.getMainClass()))
                        .manifest(defaultIfNull(manifest, extension.getManifest()))
                        .modules(defaultIfNull(modules, extension.getModules()))
                        .name(defaultIfNull(name, extension.getName()))
                        .organizationEmail(defaultIfNull(organizationEmail, extension.getOrganizationEmail()))
                        .organizationName(defaultIfNull(organizationName, extension.getOrganizationName()))
                        .organizationUrl(defaultIfNull(organizationUrl, extension.getOrganizationUrl()))
                        .outputDirectory(defaultIfNull(outputDirectory, extension.getOutputDirectory()))
                        .packagingJdk(defaultIfNull(packagingJdk, extension.getPackagingJdk(), Context.getGradleContext().getDefaultToolchain()))
                        .runnableJar(defaultIfNull(runnableJar, extension.getRunnableJar()))
                        .scripts(defaultIfNull(scripts, extension.getScripts()))
                        .useResourcesAsWorkingDir(defaultIfNull(useResourcesAsWorkingDir, extension.isUseResourcesAsWorkingDir()))
                        .url(defaultIfNull(url, extension.getUrl()))
                        .version(defaultIfNull(version, extension.getVersion(), packageTask.getProject().getVersion().toString()))
                        .vmArgs(defaultIfNull(vmArgs, extension.getVmArgs()))
                        .winConfig(defaultIfNull(winConfig, extension.getWinConfig()))
                        .iconFile(defaultIfNull(iconFile, extension.getIconFile()));
    }
}
