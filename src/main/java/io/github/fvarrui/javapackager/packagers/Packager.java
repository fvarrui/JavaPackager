package io.github.fvarrui.javapackager.packagers;

import static org.apache.commons.collections4.CollectionUtils.addIgnoreNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import io.github.fvarrui.javapackager.model.Platform;
import io.github.fvarrui.javapackager.utils.CommandUtils;
import io.github.fvarrui.javapackager.utils.FileUtils;
import io.github.fvarrui.javapackager.utils.IconUtils;
import io.github.fvarrui.javapackager.utils.JDKUtils;
import io.github.fvarrui.javapackager.utils.JavaUtils;
import io.github.fvarrui.javapackager.utils.Logger;
import io.github.fvarrui.javapackager.utils.VelocityUtils;

public abstract class Packager extends PackagerSettings {
	
	private static final String DEFAULT_ORGANIZATION_NAME = "ACME";
	
	// artifact generators collection
	protected List<ArtifactGenerator> installerGenerators = new ArrayList<>();
	
	// internal generic properties (setted in "createAppStructure/createApp")
	protected File appFolder;
	protected File assetsFolder;
	protected File executable;
	protected File jarFile;
	protected File libsFolder;
	
	// internal specific properties (setted in "doCreateAppStructure")
	protected File executableDestinationFolder;
	protected File jarFileDestinationFolder;
	protected File jreDestinationFolder;
	protected File resourcesDestinationFolder;
	
	// processed classpaths list
	protected List<String> classpaths = new ArrayList<>();

	// ===============================================	
	
	public File getAppFolder() {
		return appFolder;
	}

	public File getAssetsFolder() {
		return assetsFolder;
	}

	public File getExecutable() {
		return executable;
	}

	public File getJarFile() {
		return jarFile;
	}
	
	public File getJarFileDestinationFolder() {
		return jarFileDestinationFolder;
	}
	
	public File getLibsFolder() {
		return libsFolder;
	}
	
	public List<String> getClasspaths() {
		return classpaths;
	}

	// ===============================================
	
	public Packager() {
		super();
		Logger.info("Using packager " + this.getClass().getName());
	}
	
	private void init() throws Exception {
		
		Logger.infoIndent("Initializing packager ...");
		
		if (mainClass == null || mainClass.isEmpty()) {
			throw new Exception("'mainClass' cannot be null");
		}
		
		// sets assetsDir for velocity to locate custom velocity templates
		VelocityUtils.setAssetsDir(assetsDir);

		// using name as displayName, if it's not specified
		displayName = defaultIfBlank(displayName, name);
		
		// using displayName as description, if it's not specified
		description = defaultIfBlank(description, displayName);
		
		// using "ACME" as organizationName, if it's not specified
		organizationName = defaultIfBlank(organizationName, DEFAULT_ORGANIZATION_NAME);

		// using empty string as organizationUrl, if it's not specified
		organizationUrl = defaultIfBlank(organizationUrl, "");

		// determines target platform if not specified 
		if (platform == null || platform == Platform.auto) {
			platform = Platform.getCurrentPlatform();
		}
		
		// sets jdkPath by default if not specified
		if (jdkPath == null) {
			jdkPath = new File(System.getProperty("java.home"));
		}
		if (!jdkPath.exists()) {
			throw new Exception("JDK path doesn't exist: " + jdkPath);
		}
		
		// check if name is valid as filename
		try {
			Paths.get(name);
			if (name.contains("/")) throw new InvalidPathException(name, "Illegal char </>");
			if (name.contains("\\")) throw new InvalidPathException(name, "Illegal char <\\>");
		} catch (InvalidPathException e) {
			throw new Exception("Invalid name specified: " + name, e);
		}
		
		// init setup languages
		if (platform.equals(Platform.windows) && (winConfig.getSetupLanguages() == null || winConfig.getSetupLanguages().isEmpty())) {
			winConfig.getSetupLanguages().put("english", "compiler:Default.isl");
			winConfig.getSetupLanguages().put("spanish", "compiler:Languages\\Spanish.isl");
		}
		
		doInit();
		
		// removes not necessary platform specific configs 
		switch (platform) {
		case linux: macConfig = null; winConfig = null; break;
		case mac: winConfig = null; linuxConfig = null; break;
		case windows: linuxConfig = null; macConfig = null; break;
		default:
		}
		
		Logger.info("" + this); // prints packager settings		
				
		Logger.infoUnindent("Packager initialized!");

	}

	public void resolveResources() throws Exception {
		
		Logger.infoIndent("Resolving resources ...");
		
		// locates license file
		licenseFile = resolveLicense(licenseFile);
		
		// locates icon file
		iconFile = resolveIcon(iconFile, name, assetsFolder);
		
		// adds to additional resources
		if (additionalResources != null) {
			if (licenseFile != null) additionalResources.add(licenseFile);
			additionalResources.add(iconFile);
			Logger.info("Effective additional resources " + additionalResources);			
		}		
		
		Logger.infoUnindent("Resources resolved!");
		
	}	
	
	/**
	 * Copy a list of resources to a folder
	 * 
	 * @param resources   List of files and folders to be copied
	 * @param destination Destination folder. All specified resources will be copied
	 *                    here
	 */
	protected void copyAdditionalResources(List<File> resources, File destination) {

		Logger.infoIndent("Copying additional resources");
		
		resources.stream().forEach(r -> {
			if (!r.exists()) {
				Logger.warn("Additional resource " + r + " doesn't exist");
				return;
			}
			try {
				if (r.isDirectory()) {
					FileUtils.copyFolderToFolder(r, destination);
				} else if (r.isFile()) {
					FileUtils.copyFileToFolder(r, destination);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		Logger.infoUnindent("All additional resources copied!");
		
	}

	/**
	 * Bundle a Java Runtime Enrironment with the app.
	 * @param destinationFolder Destination folder
	 * @param jarFile Runnable jar file
	 * @param libsFolder Libs folder
	 * @param specificJreFolder Specific JRE folder to be used
	 * @param customizedJre Creates a reduced JRE
	 * @param defaultModules Default modules
	 * @param additionalModules Additional modules
	 * @param platform Target platform
	 * @throws Exception Process failed
	 */
	protected void bundleJre(File destinationFolder, File jarFile, File libsFolder, File specificJreFolder, boolean customizedJre, List<String> defaultModules, List<String> additionalModules, Platform platform) throws Exception {
		if (!bundleJre) {
			Logger.warn("Bundling JRE disabled by property 'bundleJre'!\n");
			return;
		}
		
		File currentJdk = new File(System.getProperty("java.home"));
		
		Logger.infoIndent("Bundling JRE ... with " + currentJdk);
		
		if (specificJreFolder != null) {
			
			Logger.info("Embedding JRE from " + specificJreFolder);
			
			// fixes the path to the JRE on MacOS if "release" file not found
			if (platform.equals(Platform.mac) && !FileUtils.folderContainsFile(specificJreFolder, "release")) {
				specificJreFolder = new File(specificJreFolder, "Contents/Home");
			}
			
			// checks if valid jre specified
			if (!JDKUtils.isValidJRE(platform, specificJreFolder)) {
				throw new Exception("Invalid JRE specified for '" + platform + "' platform: " + specificJreFolder);
			}
			
			// removes old jre folder from bundle
			if (destinationFolder.exists()) FileUtils.removeFolder(destinationFolder);

			// copies JRE folder to bundle
			FileUtils.copyFolderContentToFolder(specificJreFolder, destinationFolder);

			// sets execution permissions on executables in jre
			File binFolder = new File(destinationFolder, "bin");
			Arrays.asList(binFolder.listFiles()).forEach(f -> f.setExecutable(true, false));

		} else if (JavaUtils.getJavaMajorVersion() <= 8) {
			
			throw new Exception("Could not create a customized JRE due to JDK version is " + SystemUtils.JAVA_VERSION + ". Must use jrePath property to specify JRE location to be embedded");
			
		} else if (!platform.isCurrentPlatform() && jdkPath.equals(currentJdk)) {
			
			Logger.warn("Cannot create a customized JRE ... target platform (" + platform + ") is different than execution platform (" + Platform.getCurrentPlatform() + "). Use jdkPath property.");
			
			bundleJre = false;

		} else {
			
			Logger.info("Creating customized JRE ...");
			
			// tests if specified JDK is for the same platform than target platform
			if (!JDKUtils.isValidJDK(platform, jdkPath)) {
				throw new Exception("Invalid JDK for platform '" + platform + "': " + jdkPath);
			}
			
			String modules = getRequiredModules(libsFolder, customizedJre, jarFile, defaultModules, additionalModules);

			Logger.info("Creating JRE with next modules included: " + modules);

			File modulesDir = new File(jdkPath, "jmods");
			if (!modulesDir.exists()) {
				throw new Exception("jmods folder doesn't exist: " + modulesDir);
			}
			
			Logger.info("Using " + modulesDir + " modules directory");
	
			File jlink = new File(currentJdk, "/bin/jlink");
	
			if (destinationFolder.exists()) FileUtils.removeFolder(destinationFolder);
			
			// generates customized jre using modules
			CommandUtils.execute(jlink.getAbsolutePath(), "--module-path", modulesDir, "--add-modules", modules, "--output", destinationFolder, "--no-header-files", "--no-man-pages", "--strip-debug", "--compress=2");
	
			// sets execution permissions on executables in jre
			File binFolder = new File(destinationFolder, "bin");
			Arrays.asList(binFolder.listFiles()).forEach(f -> f.setExecutable(true, false));

		}
		
		// removes jre/legal folder
		File legalFolder = new File(destinationFolder, "legal");
		if (legalFolder.exists()) {
			FileUtils.removeFolder(legalFolder);
		}
	
		if (bundleJre) {
			Logger.infoUnindent("JRE bundled in " + destinationFolder.getAbsolutePath() + "!");
		} else {
			Logger.infoUnindent("JRE bundling skipped!");
		}
		
	}
	
	/**
	 * Uses jdeps command tool to determine which modules all used jar files depend on
	 * 
	 * @param libsFolder folder containing all needed libraries
	 * @param customizedJre if true generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included.
	 * @param jarFile Runnable jar file reference
	 * @param defaultModules Additional files and folders to include in the bundled app.
	 * @param additionalModules Defines modules to customize the bundled JRE. Don't use jdeps to get module dependencies.
	 * @return string containing a comma separated list with all needed modules
	 * @throws Exception Process failed
	 */
	protected String getRequiredModules(File libsFolder, boolean customizedJre, File jarFile, List<String> defaultModules, List<String> additionalModules) throws Exception {
		
		Logger.infoIndent("Getting required modules ... ");
		
		File jdeps = new File(System.getProperty("java.home"), "/bin/jdeps");

		File jarLibs = null;
		if (libsFolder != null && libsFolder.exists()) 
			jarLibs = new File(libsFolder, "*.jar");
		else
			Logger.warn("No dependencies found!");
		
		List<String> modulesList;
		
		if (customizedJre && defaultModules != null && !defaultModules.isEmpty()) {
			
			modulesList = 
				defaultModules
					.stream()
					.map(module -> module.trim())
					.collect(Collectors.toList());
		
		} else if (customizedJre && JavaUtils.getJavaMajorVersion() >= 13) { 
			
			String modules = 
				CommandUtils.execute(
					jdeps.getAbsolutePath(), 
					"-q",
					"--multi-release", JavaUtils.getJavaMajorVersion(),
					"--ignore-missing-deps", 
					"--print-module-deps", 
					jarLibs,
					jarFile
				);
			
			modulesList = 
				Arrays.asList(modules.split(","))
					.stream()
					.map(module -> module.trim())
					.filter(module -> !module.isEmpty())
					.collect(Collectors.toList());
			
		} else if (customizedJre && JavaUtils.getJavaMajorVersion() >= 9) { 
		
			String modules = 
				CommandUtils.execute(
					jdeps.getAbsolutePath(), 
					"-q",
					"--multi-release", JavaUtils.getJavaMajorVersion(),
					"--list-deps", 
					jarLibs,
					jarFile
				);

			modulesList = 
				Arrays.asList(modules.split("\n"))
					.stream()
					.map(module -> module.trim())
					.map(module -> (module.contains("/") ? module.split("/")[0] : module))
					.filter(module -> !module.isEmpty())
					.filter(module -> !module.startsWith("JDK removed internal"))
					.distinct()
					.collect(Collectors.toList());

		} else {
			
			modulesList = Arrays.asList("ALL-MODULE-PATH");
			
		}
				
		modulesList.addAll(additionalModules);
		
		if (modulesList.isEmpty()) {
			Logger.warn("It was not possible to determine the necessary modules. All modules will be included");
			modulesList.add("ALL-MODULE-PATH");
		}
		
		Logger.infoUnindent("Required modules found: " + modulesList);
		
		return StringUtils.join(modulesList, ",");
	}

	/**
	 * Locates license file
	 * @param licenseFile Specified license file
	 * @return Resolved license file
	 */
	protected File resolveLicense(File licenseFile) {
		
		// if default license file doesn't exist 
		if (licenseFile != null && !licenseFile.exists()) {
			Logger.warn("Specified license file doesn't exist: " + licenseFile.getAbsolutePath());
			licenseFile = null;
		}
		
		// invokes custom license resolver if exists
		if (licenseFile == null) {
			try {
				licenseFile = Context.getContext().resolveLicense(this);
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		
		// if license is still null, looks for LICENSE file
		if (licenseFile == null || !licenseFile.exists()) {
			licenseFile = new File(Context.getContext().getRootDir(), "LICENSE");
			if (!licenseFile.exists()) licenseFile = null;
		}
		
		if (licenseFile != null) {
			Logger.info("License file found: " + licenseFile.getAbsolutePath());
		} else {
			Logger.warn("No license file specified");
		}
		
		return licenseFile;
	}

	/**
	 * Locates assets or default icon file if the specified one doesn't exist or isn't specified
	 * @param iconFile Specified icon file
	 * @param name Name
	 * @param assetsFolder Assets folder
	 * @return Resolved icon file
	 * @throws Exception Process failed
	 */
	protected File resolveIcon(File iconFile, String name, File assetsFolder) throws Exception {

		// searchs for specific icons 
		switch (platform) {
		case linux: 	iconFile = FileUtils.exists(linuxConfig.getPngFile())	? linuxConfig.getPngFile() 	: null; break; 
		case mac: 		iconFile = FileUtils.exists(macConfig.getIcnsFile())	? macConfig.getIcnsFile() 	: null; break; 
		case windows: 	iconFile = FileUtils.exists(winConfig.getIcoFile())		? winConfig.getIcoFile() 	: null; break; 
		default:
		}
		
		String iconExtension = IconUtils.getIconFileExtensionByPlatform(platform);		
		
		// if not specific icon specified for target platform, searchs for an icon in "${assetsDir}" folder  
		if (iconFile == null) {
			iconFile = new File(assetsDir, platform + "/" + name + iconExtension);
		}
		
		// if there's no icon yet, uses default one
		if (!iconFile.exists()) {
			iconFile = new File(assetsFolder, iconFile.getName());
			FileUtils.copyResourceToFile("/" + platform + "/default-icon" + iconExtension, iconFile);
		}
		
		Logger.info("Icon file resolved: " + iconFile.getAbsolutePath());
		
		return iconFile;
	}
	
	/**
	 * Bundling app folder in tarball and/or zipball
	 * @return Generated bundles 
	 * @throws Exception Process failed
	 */
	public List<File> createBundles() throws Exception {
		
		List<File> bundles = new ArrayList<>();

		Logger.infoIndent("Creating bundles ...");
		
		if (createZipball) {
			File zipball = Context.getContext().createZipball(this);
			Logger.info("Zipball created: " + zipball);
			bundles.add(zipball);
		}

		if (createTarball) {
			File tarball = Context.getContext().createTarball(this);
			Logger.info("Tarball created: " + tarball);
			bundles.add(tarball);
		}
		
		Logger.infoUnindent("Bundles created!");
		
		return bundles;
	}
	
	private void createAppStructure() throws Exception {
		
		Logger.infoIndent("Creating app structure ...");
		
		// creates output directory if it doesn't exist
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		// creates app destination folder
		appFolder = new File(outputDirectory, name);
		if (appFolder.exists()) {
			FileUtils.removeFolder(appFolder);
			Logger.info("Old app folder removed " + appFolder.getAbsolutePath());
		} 
		appFolder = FileUtils.mkdir(outputDirectory, name);
		Logger.info("App folder created: " + appFolder.getAbsolutePath());

		// creates folder for intermmediate assets 
		assetsFolder = FileUtils.mkdir(outputDirectory, "assets");
		Logger.info("Assets folder created: " + assetsFolder.getAbsolutePath());

		// create the rest of the structure
		doCreateAppStructure();

		Logger.infoUnindent("App structure created!");
		
	}

	public File createApp() throws Exception {
		
		Logger.infoIndent("Creating app ...");

		init();

		// creates app folders structure
		createAppStructure();
		
		// resolve resources
		resolveResources();

		// copies additional resources
		copyAdditionalResources(additionalResources, resourcesDestinationFolder);
        
		// copies all dependencies to Java folder
		Logger.infoIndent("Copying all dependencies ...");		
		libsFolder = copyDependencies ? Context.getContext().copyDependencies(this) : null;
		Logger.infoUnindent("Dependencies copied to " + libsFolder + "!");		

		// creates a runnable jar file
        if (runnableJar != null && runnableJar.exists()) {
        	Logger.info("Using runnable JAR: " + runnableJar);
            jarFile = runnableJar;
        } else {
    		Logger.infoIndent("Creating runnable JAR...");
            jarFile = Context.getContext().createRunnableJar(this);
    		Logger.infoUnindent("Runnable jar created in " + jarFile + "!");
        }

		// checks if JRE should be embedded
		bundleJre(jreDestinationFolder, jarFile, libsFolder, jrePath, customizedJre, modules, additionalModules, platform);
        
        File appFile = doCreateApp();

		Logger.infoUnindent("App created in " + appFolder.getAbsolutePath() + "!");
		        
		return appFile;
	}

	public List<File> generateInstallers() throws Exception {
		List<File> installers = new ArrayList<>();
		
		if (!generateInstaller) {
			Logger.warn("Installer generation is disabled by 'generateInstaller' property!");
			return installers;
		}
		if (!platform.isCurrentPlatform()) {
			Logger.warn("Installers cannot be generated due to the target platform (" + platform + ") is different from the execution platform (" + Platform.getCurrentPlatform() + ")!");
			return installers;
		}
		
		Logger.infoIndent("Generating installers ...");

		init();
		
		// creates folder for intermmediate assets if it doesn't exist  
		assetsFolder = FileUtils.mkdir(outputDirectory, "assets");
		
		// invokes installer producers
		
		for (ArtifactGenerator generator : installerGenerators) {
			try {
				Logger.infoIndent("Generating " + generator.getArtifactName() + "...");
				File artifact = generator.apply(this);
				if (artifact != null) {
					addIgnoreNull(installers, artifact);
					Logger.infoUnindent(generator.getArtifactName() +  " generated in " + artifact + "!");
				} else {
					Logger.warnUnindent(generator.getArtifactName() +  " NOT generated!!!");					
				}
				
			} catch (Exception e) {
				Logger.errorUnindent(generator.getArtifactName() + " generation failed due to: " + e.getMessage(), e);
			}
		}
		
		Logger.infoUnindent("Installers generated! " + installers);
		
		return installers;		
	}

	protected abstract void doCreateAppStructure() throws Exception; 

	public abstract File doCreateApp() throws Exception;
	
	public abstract void doInit() throws Exception;
	
}
