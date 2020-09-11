package io.github.fvarrui.javapackager.gradle;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.io.File;
import java.util.ArrayList;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import io.github.fvarrui.javapackager.model.LinuxConfig;
import io.github.fvarrui.javapackager.model.MacConfig;
import io.github.fvarrui.javapackager.model.WindowsConfig;
import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFactory;

public class PackageTask extends DefaultTask {
	
	private PackagePluginExtension pluginSettings;
	
	public PackageTask() {
		super();
		setGroup(PackagePlugin.GROUP_NAME);
		setDescription("Packages the application as a native Windows, Mac OS X or GNU/Linux executable and creates an installer");
		dependsOn("build");
	}
	
	@TaskAction
	public void doPackage() {
		
		pluginSettings = (PackagePluginExtension) getProject().getExtensions().findByName(PackagePlugin.SETTINGS_EXT_NAME);
		
		try {

			Packager packager = 
				(Packager) PackagerFactory
					.createPackager(pluginSettings.getPlatform())
						.additionalModules(defaultIfNull(pluginSettings.getAdditionalModules(), new ArrayList<>()))
						.additionalResources(defaultIfNull(pluginSettings.getAdditionalResources(), new ArrayList<>()))
						.administratorRequired(pluginSettings.getAdministratorRequired())
						.appVersion(defaultIfBlank(pluginSettings.getVersion(), "" + getProject().getVersion()))
						.assetsDir(defaultIfNull(pluginSettings.getAssetsDir(), new File(getProject().getBuildDir(), "assets")))
						.bundleJre(defaultIfNull(pluginSettings.getBundleJre(), true))
						.copyDependencies(defaultIfNull(pluginSettings.getCopyDependencies(), true))
						.createTarball(defaultIfNull(pluginSettings.getCreateTarball(), false))
						.createZipball(defaultIfNull(pluginSettings.getCreateZipball(), false))
						.customizedJre(defaultIfNull(pluginSettings.getCustomizedJre(), true))
						.description(defaultIfBlank(pluginSettings.getDescription(), getProject().getDescription()))
						.displayName(defaultIfBlank(pluginSettings.getDisplayName(), getProject().getDisplayName()))
						.envPath(pluginSettings.getEnvPath())
						.extra(pluginSettings.getExtra())
						.generateInstaller(defaultIfNull(pluginSettings.getGenerateInstaller(), true))
						.iconFile(pluginSettings.getIconFile())
						.jdkPath(pluginSettings.getJdkPath())
						.jreDirectoryName(defaultIfBlank(pluginSettings.getJreDirectoryName(), "jre"))
						.jrePath(pluginSettings.getJrePath())
						.licenseFile(pluginSettings.getLicenseFile())
						.linuxConfig(defaultIfNull(pluginSettings.getLinuxConfig(), new LinuxConfig()))
						.macConfig(defaultIfNull(pluginSettings.getMacConfig(), new MacConfig()))
						.mainClass(pluginSettings.getMainClass())
						.modules(defaultIfNull(pluginSettings.getModules(), new ArrayList<>()))
						.name(defaultIfBlank(pluginSettings.getName(), getProject().getName()))
						.organizationEmail(defaultIfNull(pluginSettings.getOrganizationEmail(), ""))
						.organizationName(pluginSettings.getOrganizationName())
						.organizationUrl(pluginSettings.getOrganizationUrl())
						.outputDirectory(defaultIfNull(pluginSettings.getOutputDirectory(), getProject().getBuildDir()))
						.runnableJar(pluginSettings.getRunnableJar())
						.useResourcesAsWorkingDir(defaultIfNull(pluginSettings.isUseResourcesAsWorkingDir(), true))
						.url(pluginSettings.getUrl())
						.vmArgs(defaultIfNull(pluginSettings.getVmArgs(), new ArrayList<>()))
						.winConfig(defaultIfNull(pluginSettings.getWinConfig(), new WindowsConfig()));
			
			// generates app, installers and bundles
			packager.createApp();
			packager.generateInstallers();
			packager.createBundles();
			
		} catch (Exception e) {

			throw new RuntimeException(e.getMessage(), e);
			
		}


	}
	
}
