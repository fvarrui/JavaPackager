package io.github.fvarrui.javapackager.packagers;

import io.github.fvarrui.javapackager.model.MacStartup;
import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Packager for MacOS
 */
public class MacPackager extends Packager {

	private File appFile;
	private File contentsFolder;
	private File resourcesFolder;
	private File javaFolder;
	private File macOSFolder;
	private File jreBundleFolder;
	
	public MacPackager() {		
		super();
		platform(Platform.mac);
	}

	public File getAppFile() {
		return appFile;
	}

	@Override
	public void doInit() throws Exception {

		this.macConfig.setDefaults(this);

		// FIX useResourcesAsWorkingDir=false doesn't work fine on Mac OS (option disabled)
		if (!this.isUseResourcesAsWorkingDir()) {
			this.useResourcesAsWorkingDir = true;
			Logger.warn("'useResourcesAsWorkingDir' property disabled on Mac OS (useResourcesAsWorkingDir is always true)");
		}

	}

	@Override
	protected void doCreateAppStructure() throws Exception {

		// initializes the references to the app structure folders
		this.appFile = new File(appFolder, name + ".app");
		this.contentsFolder = new File(appFile, "Contents");
		this.resourcesFolder = new File(contentsFolder, "Resources");
		this.javaFolder = new File(resourcesFolder, this.macConfig.isRelocateJar() ? "Java" : "");
		this.macOSFolder = new File(contentsFolder, "MacOS");

		// makes dirs

		FileUtils.mkdir(this.appFile);
		Logger.info("App file folder created: " + appFile.getAbsolutePath());

		FileUtils.mkdir(this.contentsFolder);
		Logger.info("Contents folder created: " + contentsFolder.getAbsolutePath());

		FileUtils.mkdir(this.resourcesFolder);
		Logger.info("Resources folder created: " + resourcesFolder.getAbsolutePath());

		FileUtils.mkdir(this.javaFolder);
		Logger.info("Java folder created: " + javaFolder.getAbsolutePath());

		FileUtils.mkdir(this.macOSFolder);
		Logger.info("MacOS folder created: " + macOSFolder.getAbsolutePath());

		// sets common folders
		this.executableDestinationFolder = macOSFolder;
		this.jarFileDestinationFolder = javaFolder;
		this.jreBundleFolder = new File(contentsFolder, "PlugIns/" + jreDirectoryName + ".jre");
		this.jreDestinationFolder = new File(jreBundleFolder, "Contents/Home");
		this.resourcesDestinationFolder = resourcesFolder;

	}

	/**
	 * Creates a native MacOS app bundle
	 */
	@Override
	public File doCreateApp() throws Exception {
		if(bundleJre) {
			processRuntimeInfoPlistFile();
		}

		// copies jarfile to Java folder
		FileUtils.copyFileToFolder(jarFile, javaFolder);

		processStartupScript();

		processClasspath();

		processInfoPlistFile();

		processProvisionProfileFile();

		codesign();

		notarize();

		return appFile;
	}

	private void processStartupScript() throws Exception {
		
		if (this.administratorRequired) {

			// We need a helper script ("startup") in this case,
			// which invokes the launcher script/ executable with administrator rights.
			// TODO: admin script depends on launcher file name 'universalJavaApplicationStub'

			// sets startup file
			this.executable = new File(macOSFolder, "startup");

			// creates startup file to boot java app
			VelocityUtils.render("mac/startup.vtl", executable, this);
			
		} else {

			File launcher = macConfig.getCustomLauncher();
			if (launcher != null && launcher.canRead() && launcher.isFile()){
				FileUtils.copyFileToFolder(launcher, macOSFolder);
				this.executable = new File(macOSFolder, launcher.getName());
			} else {
				this.executable = preparePrecompiledStartupStub();
			}
		}
		
		executable.setExecutable(true, false);
		Logger.info("Startup script file created in " + executable.getAbsolutePath());
	}

	private void processClasspath() {
		// TODO: Why are we doing this here? I do not see any usage of 'classpath' or 'classpaths' here.
		classpath = (this.macConfig.isRelocateJar() ? "Java/" : "") + this.jarFile.getName() + (classpath != null ? ":" + classpath : "");
		classpaths = Arrays.asList(classpath.split("[:;]"));
		if (!isUseResourcesAsWorkingDir()) {
			classpaths = classpaths
					.stream()
					.map(cp -> new File(cp).isAbsolute() ? cp : "$ResourcesFolder/" + cp)
					.collect(Collectors.toList());
		}
		classpath = StringUtils.join(classpaths, ":");
	}

	/**
	 * Creates and writes the Info.plist file if no custom file is specified.
	 * @throws Exception if anything goes wrong
	 */
	private void processInfoPlistFile() throws Exception {
		File infoPlistFile = new File(contentsFolder, "Info.plist");
		if(macConfig.getCustomInfoPlist() != null && macConfig.getCustomInfoPlist().isFile() && macConfig.getCustomInfoPlist().canRead()){
			FileUtils.copyFileToFile(macConfig.getCustomInfoPlist(), infoPlistFile);
		} else {
			VelocityUtils.render("mac/Info.plist.vtl", infoPlistFile, this);
			XMLUtils.prettify(infoPlistFile);
		}
		Logger.info("Info.plist file created in " + infoPlistFile.getAbsolutePath());
	}

	/**
	 * Creates and writes the Info.plist inside the JRE if no custom file is specified.
	 * @throws Exception if anything goes wrong
	 */
	private void processRuntimeInfoPlistFile() throws Exception {
		File infoPlistFile = new File(jreBundleFolder, "Contents/Info.plist");
		if(macConfig.getCustomRuntimeInfoPlist() != null && macConfig.getCustomRuntimeInfoPlist().isFile() && macConfig.getCustomRuntimeInfoPlist().canRead()){
			FileUtils.copyFileToFile(macConfig.getCustomRuntimeInfoPlist(), infoPlistFile);
		} else {
			VelocityUtils.render("mac/RuntimeInfo.plist.vtl", infoPlistFile, this);
			XMLUtils.prettify(infoPlistFile);
		}
		Logger.info("RuntimeInfo.plist file created in " + infoPlistFile.getAbsolutePath());
	}

	private void codesign() throws Exception {
		if (!Platform.mac.isCurrentPlatform()) {
			Logger.warn("Generated app could not be signed due to current platform is " + Platform.getCurrentPlatform());
		} else if (!getMacConfig().isCodesignApp()) {
			Logger.warn("App codesigning disabled");
		} else {
			codesign(this.macConfig.getDeveloperId(), this.macConfig.getEntitlements(), this.appFile);
		}
	}

	private void notarize() throws Exception {
		if (!Platform.mac.isCurrentPlatform()) {
			Logger.warn("Generated app could not be notarized due to current platform is " + Platform.getCurrentPlatform());
		} else if (!getMacConfig().isCodesignApp()) {
			Logger.warn("App codesigning disabled. Cannot notarize unsigned app");
		} else if (!getMacConfig().isNotarizeApp()) {
			Logger.warn("App notarization disabled");
		} else {
			notarize(this.macConfig.getKeyChainProfile(), this.appFile);
		}
	}

	private void processProvisionProfileFile() throws Exception {
		if (macConfig.getProvisionProfile() != null && macConfig.getProvisionProfile().isFile() && macConfig.getProvisionProfile().canRead()) {
			// file name must be 'embedded.provisionprofile'
			File provisionProfile = new File(contentsFolder, "embedded.provisionprofile");
			FileUtils.copyFileToFile(macConfig.getProvisionProfile(), provisionProfile);
			Logger.info("Provision profile file created from " + "\n" +
					macConfig.getProvisionProfile() + " to \n" +
					provisionProfile.getAbsolutePath());
		}
	}

	private File preparePrecompiledStartupStub() throws Exception {
		// sets startup file
		File appStubFile = new File(macOSFolder, "universalJavaApplicationStub");
		String universalJavaApplicationStubResource = null;
		switch (macConfig.getMacStartup()) {
			case UNIVERSAL:	universalJavaApplicationStubResource = "universalJavaApplicationStub"; break;
			case X86_64:	universalJavaApplicationStubResource = "universalJavaApplicationStub.x86_64"; break;
			case ARM64: 	universalJavaApplicationStubResource = "universalJavaApplicationStub.arm64"; break;
			case SCRIPT: 	universalJavaApplicationStubResource = "universalJavaApplicationStub.sh"; break;
		}
		// unixStyleNewLinux=true if startup is a script (this will replace '\r\n' with '\n')
		FileUtils.copyResourceToFile("/mac/" + universalJavaApplicationStubResource, appStubFile, macConfig.getMacStartup() == MacStartup.SCRIPT);
		return appStubFile;
	}

	private void codesign(String developerId, File entitlements, File appFile) throws Exception {

		entitlements = prepareEntitlementFile(entitlements);

		signAppBundle(appFile, developerId, entitlements);

	}

	private File prepareEntitlementFile(File entitlements) throws Exception {
		// if entitlements.plist file not specified, use a default one
		if (entitlements == null) {
			Logger.warn("Entitlements file not specified. Using defaults!");
			entitlements = new File(assetsFolder, "entitlements.plist");
			VelocityUtils.render("mac/entitlements.plist.vtl", entitlements, this);
		} else if (!entitlements.exists()) {
			throw new Exception("Entitlements file doesn't exist: " + entitlements);
		}
		return entitlements;
	}

	private void signAppBundle(File appFolder, String developerCertificateName, File entitlements) throws IOException, CommandLineException {
//		Sign all embedded executables and dynamic libraries
//		Structure and order adapted from the JRE's jpackage
		try (Stream<Path> stream = Files.walk(appFolder.toPath())) {
			stream.filter(p -> Files.isRegularFile(p)
					&& (Files.isExecutable(p) || p.toString().endsWith(".dylib"))
					&& !(p.toString().contains("dylib.dSYM/Contents"))
					&& !(p.equals(this.executable.toPath()))
			).forEach(p -> {
				if (Files.isSymbolicLink(p)) {
					Logger.debug("Skipping signing symlink: " + p);
				} else {
					try {
						codesign(Files.isExecutable(p) ? entitlements : null, developerCertificateName, p.toFile());
					} catch (IOException | CommandLineException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}

		if(bundleJre) {
			// sign the JRE itself after signing all its contents
			codesign(developerCertificateName, jreBundleFolder);
		}

		// make sure the executable is signed last
		codesign(entitlements, developerCertificateName, this.executable);

		// finally, sign the top level directory
		codesign(entitlements, developerCertificateName, appFolder);
	}

	private void codesign(String developerCertificateName, File file) throws IOException, CommandLineException {
		codesign(null, developerCertificateName, file);
	}
	
	private void codesign(File entitlements, String developerCertificateName, File file) throws IOException, CommandLineException {
		List<Object> arguments = new ArrayList<>();
		arguments.add("-f");
		if(entitlements != null) {
			addHardenedCodesign(arguments);
			arguments.add("--entitlements");
			arguments.add(entitlements);
		}
		arguments.add("--timestamp");
		arguments.add("-s");
		arguments.add(developerCertificateName);
		arguments.add(file);
		CommandUtils.execute("codesign", arguments);
	}

	private void addHardenedCodesign(Collection<Object> args){
		if (macConfig.isHardenedCodesign()) {
			if (VersionUtils.compareVersions("10.13.6", SystemUtils.OS_VERSION) >= 0) {
				args.add("-o");
				args.add("runtime"); // enable hardened runtime if Mac OS version >= 10.13.6
			} else {
				Logger.warn("Mac OS version detected: " + SystemUtils.OS_VERSION + " ... hardened runtime disabled!");
			}
		}
	}

	private void notarize(String keyChainProfile, File appFile) throws IOException, CommandLineException {
		Path zippedApp = null;
		try {
			zippedApp = zipApp(appFile);
			List<Object> notarizeArgs = new ArrayList<>();
			notarizeArgs.add("notarytool");
			notarizeArgs.add("submit");
			notarizeArgs.add(zippedApp.toString());
			notarizeArgs.add("--wait");
			notarizeArgs.add("--keychain-profile");
			notarizeArgs.add(keyChainProfile);
			CommandUtils.execute("xcrun", notarizeArgs);
		} finally {
			if(zippedApp != null) {
				Files.deleteIfExists(zippedApp);
			}
		}

		List<Object> stapleArgs = new ArrayList<>();
		stapleArgs.add("stapler");
		stapleArgs.add("staple");
		stapleArgs.add(appFile);
		CommandUtils.execute("xcrun", stapleArgs);
	}

	private Path zipApp(File appFile) throws IOException {
		Path zipPath = assetsFolder.toPath().resolve(appFile.getName() + "-notarization.zip");
		try(ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
			Path sourcePath = appFile.toPath();
			Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					zos.putNextEntry(new ZipEntry(sourcePath.getParent().relativize(file).toString()));
					Files.copy(file, zos);
					zos.closeEntry();
					return FileVisitResult.CONTINUE;
				}
			});
		}
		return zipPath;
	}
}
