package io.github.fvarrui.javapackager.gradle;

import static io.github.fvarrui.javapackager.utils.ObjectUtils.defaultIfNull;

import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.packagers.PackagerFactory;

/**
 * Default packaging task for Gradle 
 */
public class DefaultPackageTask extends AbstractPackageTask {
	
	@Override
	protected Packager createPackager() throws Exception {
		
		PackagePluginExtension extension = getProject().getExtensions().findByType(PackagePluginExtension.class);
		
		return 
			(Packager) PackagerFactory
				.createPackager(extension.getPlatform())
					.additionalModules(extension.getAdditionalModules())
					.additionalModulePaths(extension.getAdditionalModulePaths())
					.additionalResources(extension.getAdditionalResources())
					.administratorRequired(extension.getAdministratorRequired())
					.assetsDir(extension.getAssetsDir())
					.bundleJre(extension.getBundleJre())
					.copyDependencies(extension.getCopyDependencies())
					.createTarball(extension.getCreateTarball())
					.createZipball(extension.getCreateZipball())
					.customizedJre(extension.getCustomizedJre())
					.description(extension.getDescription())
					.displayName(extension.getDisplayName())
					.envPath(extension.getEnvPath())
					.extra(extension.getExtra())
					.fileAssociations(extension.getFileAssociations())
					.forceInstaller(extension.isForceInstaller())
					.generateInstaller(extension.getGenerateInstaller())
					.jdkPath(extension.getJdkPath())
					.jreDirectoryName(extension.getJreDirectoryName())
					.jreMinVersion(extension.getJreMinVersion())
					.jrePath(extension.getJrePath())
					.licenseFile(extension.getLicenseFile())
					.linuxConfig(extension.getLinuxConfig())
					.macConfig(extension.getMacConfig())
					.mainClass(extension.getMainClass())
					.manifest(extension.getManifest())
					.modules(extension.getModules())
					.name(extension.getName())
					.organizationEmail(extension.getOrganizationEmail())
					.organizationName(extension.getOrganizationName())
					.organizationUrl(extension.getOrganizationUrl())
					.outputDirectory(extension.getOutputDirectory())
					.packagingJdk(extension.getPackagingJdk())
					.runnableJar(extension.getRunnableJar())
					.useResourcesAsWorkingDir(extension.isUseResourcesAsWorkingDir())
					.url(extension.getUrl())
					.version(defaultIfNull(extension.getVersion(), getProject().getVersion().toString()))
					.vmArgs(extension.getVmArgs())
					.winConfig(extension.getWinConfig());

	}
	
}
