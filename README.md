# JavaPackager

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.fvarrui/javapackager/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.fvarrui/javapackager)

JavaPackager is a hybrid plugin for **Maven** and **Gradle** which provides an easy way to package Java applications in native Windows, Mac OS X or GNU/Linux executables, and generate installers for them.

> SNAPSHOT version is not released to Maven Central, so you have to [install it manually](#how-to-build-and-install-the-plugin).

> :eyes: See [JavaPackager changes and fixes](https://github.com/fvarrui/JavaPackager/releases).

## History

It was born while teaching to my students how to build and distribute their Java apps, and after seeing that a chain of several plugins was needed to achieve this task, I decided to develop a plugin :ring: to govern them all.

## How to use this plugin

### Config your project and package your app with Maven

Add the following `plugin` tag to your `pom.xml`:

```xml
<plugin>
	<groupId>io.github.fvarrui</groupId>
	<artifactId>javapackager</artifactId>
	<version>1.3.0</version>
	<executions>
		<execution>
			<phase>package</phase>
			<goals>
				<goal>package</goal>
			</goals>
			<configuration>
				<!-- mandatory -->
				<mainClass>path.to.your.mainClass</mainClass>
				<!-- optional -->
				<bundleJre>true|false</bundleJre>
				<generateInstaller>true|false</generateInstaller>       				<administratorRequired>true|false</administratorRequired>
				<platform>auto|linux|mac|windows</platform>
				<additionalResources>
					<additionalResource>file path</additionalResource>
					<additionalResource>folder path</additionalResource>
					<additionalResource>...</additionalResource>
				</additionalResources>
				<linuxConfig>...</linuxConfig>
				<macConfig>...</macConfig>
				<winConfig>...</winConfig>
				[...]
			</configuration>
		</execution>
	</executions>
</plugin>
```

> See [Maven plugin configuration samples](docs/maven/plugin-configuration-samples.md) to know more.

And execute the next command in project's root folder:

```bash
mvn package
```

### Config your project and package your app with Gradle

Apply JavaPackager plugin in `build.gradle` using legacy mode (because at the moment it's only available in Maven Central repository):

```groovy
buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath 'io.github.fvarrui:javapackager:1.3.0'
	}
}

apply plugin: 'io.github.fvarrui.javapackager.plugin'
```

Create your packaging task:

```groovy
task packageMyApp(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	// mandatory
	mainClass = 'path.to.your.mainClass'
	// optional
	bundleJre = true|false
	generateInstaller = true|false
	administratorRequired = true|false
	platform = auto|linux|mac|windows
	additionalResources = [ file('file path'), file('folder path'), ... ]
	linuxConfig {
		...
	}
	macConfig {
		...
	}
	winConfig {
		...
	}
	...
}
```

>  See [Gradle plugin configuration samples](docs/gradle/plugin-configuration-samples.md) to know more.

And execute the next command in project's root folder:

```bash
gradle packageMyApp
```

### Generated artifacts

By default it will generate next artifacts in `${outputDirectory} ` folder:

| Artifact                                | Description                                                  |
| --------------------------------------- | ------------------------------------------------------------ |
| `${name}`                               | Directory with the native application and other needed assets. |
| `${name}-${version}-runnable.jar`       | Runnable JAR file.                                           |
| `${name}_${version}.deb`                | DEB package file if it's executed on GNU/Linux (requires **dpkg-deb**). |
| `${name}_${version}.rpm`                | RPM package file if it's executed on GNU/Linux (requires **rpmbuild**). |
| `${name}_${version}.exe`                | Setup file if it's executed on Windows (requires [**Inno Setup**](http://www.jrsoftware.org/isinfo.php)). |
| `${name}_${version}.msi`                | MSI installer file if it's executed on Windows (requires **[WiX Toolset](https://wixtoolset.org/)**). |
| `${name}_${version}.msm`                | MSI merge module file if it's executed on Windows (requires **[WiX Toolset](https://wixtoolset.org/)**). |
| `${name}_${version}.dmg`                | Disk image file if it's executed on Mac OS X (requires **hdiutil**). |
| `${name}_${version}.pkg`                | PKG installer file if it's executed on Mac OS X (requires **pkgbuild**) |
| `${name}-${version}-${platform}.zip`    | Zipball containing generated directory `${name}`.            |
| `${name}-${version}-${platform}.tar.gz` | Compressed tarball containing generated directory `${name}`. |

>  :warning: Installers generation will be ommited if target platform is different from current platform (see `platform` property).

> :warning: **DEB and RPM package generation in Gradle is not yet available. Coming soon!**

### Plugin configutation properties

| Property                   | Mandatory          | Default value                                                | Description                                                  |
| -------------------------- | ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `additionalModules`        | :x:                | `[]`                                                         | Additional modules to the ones identified by `jdeps` or the specified with `modules` property. |
| `additionalResources`      | :x:                | `[]`                                                         | Additional files and folders to include in the bundled app.  |
| `administratorRequired`    | :x:                | `false`                                                      | App will run as administrator (with elevated privileges).    |
| `assetsDir`                | :x:                | `${basedir}/assets` or `${projectdir}/assets`                | Assets location (icons and custom Velocity templates).       |
| `bundleJre`                | :x:                | `false`                                                      | Embeds a customized JRE with the app.                        |
| `copyDependencies`         | :x:                | `true`                                                       | Bundles all dependencies (JAR files) with the app.           |
| `createTarball`            | :x:                | `false`                                                      | Bundles app folder in tarball.                               |
| `createZipball`            | :x:                | `false`                                                      | Bundles app folder in zipball.                               |
| `customizedJre`            | :x:                | `true`                                                       | Generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included. |
| `description`              | :x:                | `${project.description}` or `${displayName}`                 | Project description.                                         |
| `displayName`              | :x:                | `${project.name}` or `${name}`                               | App name to show.                                            |
| `envPath`                  | :x:                | `null`                                                       | Defines PATH environment variable in GNU/Linux and Mac OS X startup scripts. |
| `extra`                    | :x:                | `null`                                                       | Map with extra properties to be used in customized Velocity templates, accesible through `$info.extra` variable. |
| `generateInstaller`        | :x:                | `true`                                                       | Generates an installer for the app.                          |
| `iconFile`                 | :x:                | `null`                                                       | Path to the app icon file (PNG, XPM, ICO or ICNS). **:warning: Deprecated (see platform specific properties).** |
| `jdkPath`                  | :x:                | `${java.home}`                                               | JDK used to generate a customized JRE. It allows to bundle customized JREs for different platforms. |
| `jreDirectoryName`         | :x:                | `"jre"`                                                      | Bundled JRE directory name.                                  |
| `jrePath`                  | :x:                | `""`                                                         | Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least. |
| `licenseFile`              | :x:                | `${project.licenses[0].url}`  or `${basedir}/LICENSE` or `${projectdir}/LICENSE` | Path to project license file.                                |
| `mainClass`                | :heavy_check_mark: | `${exec.mainClass}`                                          | Full path to your app main class.                            |
| `modules`                  | :x:                | `[]`                                                         | Defines modules to customize the bundled JRE. Don't use `jdeps` to get module dependencies. |
| `name`                     | :x:                | `${project.name}` or `${project.artifactId}`                 | App name.                                                    |
| `organizationName`         | :x:                | `${project.organization.name}` or `"ACME"`                   | Organization name.                                           |
| `organizationUrl`          | :x:                | `${project.organization.url}`                                | Organization website URL.                                    |
| `organizationEmail`        | :x:                | `null`                                                       | Organization email.                                          |
| `outputDirectory`          | :x:                | `${project.build.directory}` or `${project.builddir}`        | Output directory (where the artifacts will be generated).    |
| `platform`                 | :x:                | `auto`                                                       | Defines the target platform, which could be different to the execution platform. Possible values:  `auto`, `mac`, `linux`, `windows`. Use `auto`  for using execution platform as target. |
| `runnableJar`              | :x:                | `null`                                                       | Defines your own JAR file to be bundled. If it's ommited, the plugin packages your code in a runnable JAR and bundle it with the app. |
| `url`                      | :x:                | `null`                                                       | App website URL.                                             |
| `useResourcesAsWorkingDir` | :x:                | `true`                                                       | Uses app resources folder as default working directory.      |
| ` version`                 | :x:                | `${project.version}`                                         | Project version.                                             |
| `vmArgs`                   | :x:                | `[]`                                                         | Adds VM arguments.                                           |

> Some default values depends on the used building tool.

**Platform specific properties**

| Property      | Mandatory | Default | Description                                                  |
| ------------- | --------- | ------- | ------------------------------------------------------------ |
| `linuxConfig` | :x:       | `null`  | [GNU/Linux specific properties](docs/linux-specific-properties.md). |
| `macConfig`   | :x:       | `null`  | [Mac OS X specific properties](docs/macosx-specific-properties.md). |
| `winConfig`   | :x:       | `null`  | [Windows specific properties](docs/windows-specific-properties.md). |

> See [**Older documentation**](#older-documentation) for previous versions properties.

> :warning: Be careful when using the `platform` property if your project uses platform dependent libraries, so the libraries of the current platform will be copied, not those required for the target platform. You can solve this problem using `classifiers`. Also, customized JRE and intallers generation will be ommited.

### Plugin assets

Some assets, such as application icons and Velocity templates, could be placed in `${assetsDir}` folder organized by platform.

```
${assetsDir}/
├── linux/
├── mac/
└── windows/
```

#### Icons

If icons are located in `${assetsDir}` folders, it would not be necessary to specify the `iconFile` property:

```
${assetsDir}/
├── linux/
│   ├── ${name}.png     # on GNU/Linux it has to be a PNG file for DEB package
│   └── ${name}.xpm     # and XPM file for RPM package
├── mac/
│   └── ${name}.icns    # on Mac OS X it has to be a ICNS file
└── windows/
    └── ${name}.ico     # on Windows it has to be a ICO file
```

> :warning: If `iconFile` plugin property is not specified and it can't find the correct icon in `${assetsDir}` folder, it will use an [icon by default](https://raw.githubusercontent.com/fvarrui/JavaPackager/master/src/main/resources/linux/default-icon.png) for all platforms.
>

#### Velocity templates

[Velocity](https://velocity.apache.org/engine/2.0/user-guide.html) templates (`.vtl` files) are used to generate some artifacts which have to be bundled with the app.

It is possible to use your own customized templates. You just have to put one of the following templates in the `${assetsDir}` folder organized by platform, and the plugin will use these templates instead of default ones:

```
${assetsDir}/
├── linux/
|   ├── assembly.xml.vtl               # maven-assembly-plugin template to generate ZIP/TGZ bundles for GNU/Linux
|   ├── control.vtl                    # DEB control template
|   ├── desktop.vtl                    # Desktop template
│   └── startup.sh.vtl                 # Startup script template
├── mac/
|   ├── assembly.xml.vtl               # maven-assembly-plugin template to generate ZIP/TGZ bundles for Mac OS X
|   ├── customize-dmg.applescript.vtl  # DMG customization Applescript template
|   ├── Info.plist.vtl                 # Info.plist template
│   └── startup.vtl                    # Startup script template
└── windows/
    ├── assembly.xml.vtl               # maven-assembly-plugin template to generate ZIP/TGZ bundles for Windows
    ├── exe.manifest.vtl               # exe.manifest template
    ├── iss.vtl                        # Inno Setup Script template
    └── wxs.vtl                        # WiX Toolset WXS template
```

> Use [default templates](https://github.com/fvarrui/JavaPackager/tree/master/src/main/resources) as examples.

An object called `info` is passed to all templates with all plugin properties.

## How to build and install the plugin

> Useful to try SNAPSHOT versions.

Execute next commands in BASH (GNU/Linux or macOS) or CMD (Windows):

1. Download source code and change to the project directory:

```bash
git clone https://github.com/fvarrui/JavaPackager.git
cd JavaPackager
```

2. Compile, package and install the plugin in your local repository (ommit `./` on Windows):

```bash
./gradlew publishToMavenLocal
```

## How to release the plugin to Maven Central

Run next command (ommit `./` on Windows):

```bash
./gradlew -Prelease uploadArchives closeAndReleaseRepository
```

> Related [guide](https://nemerosa.ghost.io/2015/07/01/publishing-to-the-maven-central-using-gradle/).

## How to release the plugin to Gradle plugin portal

Only the first time, run next command:

```bash
./gradlew login
```

And then, run (ommit `./` on Windows):

```bash
./gradlew publishPlugins
```

> Related [guide](https://plugins.gradle.org/docs/submit).

## Future features

Check the [TO-DO list](https://github.com/fvarrui/JavaPackager/projects/1#column-7704117) to know the features we plan to add to JavaPackager.

## Older documentation

- [v1.2.0](https://github.com/fvarrui/JavaPackager/blob/v1.2.0/README.md)
- [v1.1.0](https://github.com/fvarrui/JavaPackager/blob/v1.1.0/README.md)
- [v1.0.3](https://github.com/fvarrui/JavaPackager/blob/v1.0.3/README.md)
- [v1.0.2](https://github.com/fvarrui/JavaPackager/blob/v1.0.2/README.md)
- [v1.0.1](https://github.com/fvarrui/JavaPackager/blob/v1.0.1/README.md)
- [v1.0.0](https://github.com/fvarrui/JavaPackager/blob/v1.0.0/README.md)
- [v0.9.7](https://github.com/fvarrui/JavaPackager/blob/v0.9.7/README.md)
- [v0.9.6](https://github.com/fvarrui/JavaPackager/blob/v0.9.6/README.md)
- [v0.9.5](https://github.com/fvarrui/JavaPackager/blob/v0.9.5/README.md)
- [v0.9.4](https://github.com/fvarrui/JavaPackager/blob/v0.9.4/README.md)
- [v0.9.3](https://github.com/fvarrui/JavaPackager/blob/v0.9.3/README.md)
- [v0.9.1](https://github.com/fvarrui/JavaPackager/blob/v0.9.1/README.md)
- [v0.9.0](https://github.com/fvarrui/JavaPackager/blob/v0.9.0/README.md)
