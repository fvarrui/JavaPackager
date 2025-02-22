# JavaPackager

[![Maven Central](http://img.shields.io/maven-central/v/io.github.fvarrui/javapackager)](https://central.sonatype.com/search?smo=true&q=a%3Ajavapackager+g%3Aio.github.fvarrui)
[![GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-%250778B9.svg)](https://www.gnu.org/licenses/gpl-3.0.html)

JavaPackager is a hybrid plugin for **Maven** and **Gradle** which provides an easy way to package Java applications in native Windows, MacOS or GNU/Linux executables, and generate installers for them.

> [!IMPORTANT] 
> See [JavaPackager changes and fixes](https://github.com/fvarrui/JavaPackager/releases).

## Project maintainers needed

This project has been actively maintained for many years, but due to lack of time, I can no longer dedicate the attention it deserves. To keep it alive and evolving, I’m looking for contributors to help with its maintenance.  

:point_right: **[Join the discussion](https://github.com/fvarrui/JavaPackager/discussions/460)**  

Any help is welcome! Thank you for your support.

## History

It was born while teaching to my students how to build and distribute their Java apps, and after seeing that a chain of several plugins was needed to achieve this task, I decided to develop a plugin :ring: to rule them all.

## Apps packaged with JavaPackager

- [AstroImageJ](http://astroimagej.com/)
- [Astro Pixel Processor](https://www.astropixelprocessor.com/)
- [GistFX](https://github.com/RedmondSims/GistFX)
- [Spektar Design Lab](https://spektar.io/)

## How to use this plugin

### Package your app with Maven

Add the following `plugin` tag to your `pom.xml`:

```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{latest.version}</version>
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
                <generateInstaller>true|false</generateInstaller>
                <administratorRequired>true|false</administratorRequired>
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

> [!tip]
> See [Maven plugin configuration samples](docs/maven/plugin-configuration-samples.md) to know more.

And execute the next command in project's root folder:

```bash
mvn package
```

### Package your app with Gradle

Apply JavaPackager plugin in `build.gradle` using legacy mode (because at the moment it's only available in Maven Central repository):

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'io.github.fvarrui:javapackager:{latest.version}'
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
    platform = "auto"|"linux"|"mac"|"windows"
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

> [!TIP] 
> See [Gradle plugin configuration samples](docs/gradle/plugin-configuration-samples.md) to know more.

And execute the next command in project's root folder:

```bash
gradle packageMyApp
```

### Generated artifacts

By default it will generate next artifacts in `${outputDirectory} ` folder:

| Artifact                                | Description                                                      | Platform  | Requires                                                                                         |
| --------------------------------------- | ---------------------------------------------------------------- | --------- | ------------------------------------------------------------------------------------------------ |
| `${name}`                               | Directory with native application and other assets.              | All       |                                                                                                  |
| `${name}-${version}-runnable.jar`       | Runnable JAR file.                                               | All       |                                                                                                  |
| `${name}_${version}.AppImage`           | AppImage package file.                                           | GNU/Linux | [FUSE 2](https://github.com/AppImage/AppImageKit/wiki/FUSE) to run the app.                      |
| `${name}_${version}.deb`                | DEB package file.                                                | All       |                                                                                                  |
| `${name}_${version}.rpm`                | RPM package file.                                                | All       |                                                                                                  |
| `${name}_${version}.exe`                | Setup file.                                                      | Windows   | [Inno Setup](http://www.jrsoftware.org/isinfo.php) (`iscc` command must be in PATH variable)     |
| `${name}_${version}.msi`                | MSI installer file.                                              | Windows   | [WiX Toolset](https://wixtoolset.org/) (`candle` and `light` commands must be in PATH variable)  |
| `${name}_${version}.msm`                | MSI merge module file.                                           | Windows   | [WiX Toolset](https://wixtoolset.org/) ( `candle` and `light` commands must be in PATH variable) |
| `${name}_${version}.dmg`                | Disk image file (uses **hdiutil**).                              | MacOS     |                                                                                                  |
| `${name}_${version}.pkg`                | PKG installer file (uses **pkgbuild**).                          | MacOS     |                                                                                                  |
| `${name}-${version}-${platform}.zip`    | Zipball containing generated directory `${name}`.                | All       |                                                                                                  |
| `${name}-${version}-${platform}.tar.gz` | Compressed tarball containing generated directory `${name}`.     | All       |                                                                                                  |
| `assets`                                | Directory with all intermediate files generated by JavaPackager. | All       |                                                                                                  |

> [!TIP]
> **Inno Setup** and **WiX Toolset** installation [guide](docs/windows-tools-guide.md).

### Plugin configuration properties

| Property                   | Mandatory          | Default value                                                                                                                                      | Description                                                                                                                                                                               |
| -------------------------- | ------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `additionalModulePaths`    | :x:                | `[]`                                                                                                                                               | Additional module paths for `jdeps`.                                                                                                                                                      |
| `additionalModules`        | :x:                | `[]`                                                                                                                                               | Additional modules to the ones identified by `jdeps` or the specified with `modules` property.                                                                                            |
| `additionalResources`      | :x:                | `[]`                                                                                                                                               | Additional files and folders to include in the bundled app.                                                                                                                               |
| `arch`                     | :x:                | `${os.arch}`                                                                                                                                       | The dependency of some ArtifactGenerator objects in the process of making packages, such as GenerateDeb                                                                                   |
| `administratorRequired`    | :x:                | `false`                                                                                                                                            | App will run as administrator (with elevated privileges).                                                                                                                                 |
| `assetsDir`                | :x:                | `${basedir}/assets` or `${projectdir}/assets`                                                                                                      | Assets location (icons and custom Velocity templates).                                                                                                                                    |
| `bundleJre`                | :x:                | `false`                                                                                                                                            | Embeds a customized JRE with the app.                                                                                                                                                     |
| `classpath`                | :x:                |                                                                                                                                                    | List of additional paths to JVM classpath, separated with `;` (recommended) or `:`.                                                                                                       |
| `copyDependencies`         | :x:                | `true`                                                                                                                                             | Bundles all dependencies (JAR files) with the app.                                                                                                                                        |
| `createTarball`            | :x:                | `false`                                                                                                                                            | Bundles app folder in tarball.                                                                                                                                                            |
| `createZipball`            | :x:                | `false`                                                                                                                                            | Bundles app folder in zipball.                                                                                                                                                            |
| `customizedJre`            | :x:                | `true`                                                                                                                                             | Generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included.                                                                      |
| `description`              | :x:                | `${project.description}` or `${displayName}`                                                                                                       | Project description.                                                                                                                                                                      |
| `displayName`              | :x:                | `${project.name}` or `${name}`                                                                                                                     | App name to show.                                                                                                                                                                         |
| `envPath`                  | :x:                |                                                                                                                                                    | Defines PATH environment variable in GNU/Linux and MacOS startup scripts.                                                                                                                 |
| `extra`                    | :x:                |                                                                                                                                                    | Map with extra properties to be used in customized Velocity templates, accesible through `$info.extra` variable.                                                                          |
| `fileAssociations`         | :x:                | [`FileAssociation[]`](https://github.com/fvarrui/JavaPackager/blob/master/src/main/java/io/github/fvarrui/javapackager/model/FileAssociation.java) | Associate file extensions or MIME types to the app.                                                                                                                                       |
| `forceInstaller`           | :x:                | `false`                                                                                                                                            | If `true`, skips operating system check when generating installers.                                                                                                                       |
| `generateInstaller`        | :x:                | `true`                                                                                                                                             | Generates an installer for the app.                                                                                                                                                       |
| `jdkPath`                  | :x:                | `${java.home}`                                                                                                                                     | JDK used to generate a customized JRE. It allows to bundle customized JREs for different platforms.                                                                                       |
| `jreDirectoryName`         | :x:                | `"jre"`                                                                                                                                            | Bundled JRE directory name.                                                                                                                                                               |
| `jreMinVersion`            | :x:                |                                                                                                                                                    | JRE minimum version. If an appropriate version cannot be found display error message. Disabled if a JRE is bundled.                                                                       |
| `jrePath`                  | :x:                | `""`                                                                                                                                               | Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least.                                                 |
| `licenseFile`              | :x:                | `${project.licenses[0].url}`  or `${basedir}/LICENSE` or `${projectdir}/LICENSE`                                                                   | Path to project license file.                                                                                                                                                             |
| `mainClass`                | :heavy_check_mark: | `${exec.mainClass}`                                                                                                                                | Full path to your app main class.                                                                                                                                                         |
| `manifest`                 | :x:                |                                                                                                                                                    | [Allows adding additional entries to MANIFEST.MF file.](docs/manifest.md)                                                                                                                 |
| `modules`                  | :x:                | `[]`                                                                                                                                               | Modules to customize the bundled JRE. Don't use `jdeps` to get module dependencies.                                                                                                       |
| `name`                     | :x:                | `${project.name}` or `${project.artifactId}`                                                                                                       | App name.                                                                                                                                                                                 |
| `organizationName`         | :x:                | `${project.organization.name}` or `"ACME"`                                                                                                         | Organization name.                                                                                                                                                                        |
| `organizationUrl`          | :x:                | `${project.organization.url}`                                                                                                                      | Organization website URL.                                                                                                                                                                 |
| `organizationEmail`        | :x:                |                                                                                                                                                    | Organization email.                                                                                                                                                                       |
| `outputDirectory`          | :x:                | `${project.build.directory}` or `${project.builddir}`                                                                                              | Output directory (where the artifacts will be generated).                                                                                                                                 |
| `packagingJdk`             | :x:                | `${java.home}`                                                                                                                                     | JDK used in the execution of `jlink` and other JDK tools.                                                                                                                                 |
| `platform`                 | :x:                | `auto`                                                                                                                                             | Defines the target platform, which could be different to the execution platform. Possible values:  `auto`, `mac`, `linux`, `windows`. Use `auto`  for using execution platform as target. |
| `runnableJar`              | :x:                |                                                                                                                                                    | Defines your own JAR file to be bundled. If it's ommited, the plugin packages your code in a runnable JAR and bundle it with the app.                                                     |
| `scripts`                  | :x:                |                                                                                                                                                    | Specify bootstrap script. **Pre and post-install scripts comming soon!**                                                                                                                  |
| `url`                      | :x:                |                                                                                                                                                    | App website URL.                                                                                                                                                                          |
| `useResourcesAsWorkingDir` | :x:                | `true`                                                                                                                                             | Uses app resources folder as default working directory (always `true` on MacOS).                                                                                                          |
| `version`                  | :x:                | `${project.version}`                                                                                                                               | App version.                                                                                                                                                                              |
| `vmArgs`                   | :x:                | `[]`                                                                                                                                               | VM arguments.                                                                                                                                                                             |

> [!IMPORTANT]
> Some default values depends on the used building tool.

**Platform specific properties**

| Property      | Mandatory | Description                                                         |
| ------------- | --------- | ------------------------------------------------------------------- |
| `linuxConfig` | :x:       | [GNU/Linux specific properties](docs/linux-specific-properties.md). |
| `macConfig`   | :x:       | [MacOS specific properties](docs/macosx-specific-properties.md).    |
| `winConfig`   | :x:       | [Windows specific properties](docs/windows-specific-properties.md). |

> [!WARNING]
> Be careful when using the `platform` property if your project uses platform dependent libraries, so the libraries of the current platform will be copied, not those required for the target platform. You can solve this problem using `classifiers`.

### Plugin assets

Any [asset used by JavaPackager](https://github.com/fvarrui/JavaPackager/tree/master/src/main/resources), such as application icons or templates, can be replaced just by placing a file with the same name in `${assetsDir}` folder organized by platform.

```bash
${assetsDir}/
├── linux/
├── mac/
└── windows/
```

#### Icons

If icons are located in `${assetsDir}` folder, it would not be necessary to use icon properties:

```bash
${assetsDir}/
├── linux/
│   └── ${name}.png     # on GNU/Linux it has to be a PNG file
├── mac/
│   └── ${name}.icns    # on MacOS it has to be a ICNS file
└── windows/
    └── ${name}.ico     # on Windows it has to be a ICO file
```

> [!WARNING] 
> If icon is not specified , it will use an [icon by default](https://raw.githubusercontent.com/fvarrui/JavaPackager/master/src/main/resources/linux/default-icon.png) for all platforms.

#### Templates

[Velocity](https://velocity.apache.org/engine/2.0/user-guide.html) templates (`.vtl` files) are used to generate some artifacts which have to be bundled with the app or needed to generate other artifacts.

It is possible to use your own customized templates. You just have to put one of the following templates in the `${assetsDir}` folder organized by platform, and the plugin will use these templates instead of default ones:

```bash
${assetsDir}/
├── linux/
|   ├── assembly.xml.vtl               # maven-assembly-plugin template to generate ZIP/TGZ bundles for GNU/Linux
|   ├── control.vtl                    # DEB control template
|   ├── desktop.vtl                    # Desktop template
|   ├── desktop-appimage.vtl           # AppImage format Desktop template
|   ├── mime.xml.vtl                   # MIME.XML template
│   └── startup.sh.vtl                 # Startup script template
├── mac/
|   ├── assembly.xml.vtl               # maven-assembly-plugin template to generate ZIP/TGZ bundles for MacOS
|   ├── customize-dmg.applescript.vtl  # DMG customization Applescript template
|   ├── Info.plist.vtl                 # Info.plist template
│   └── startup.vtl                    # Startup script template
└── windows/
    ├── assembly.xml.vtl               # maven-assembly-plugin template to generate ZIP/TGZ bundles for Windows
    ├── exe.manifest.vtl               # exe.manifest template
    ├── ini.vtl                        # WinRun4J INI template
    ├── iss.vtl                        # Inno Setup Script template
    ├── msm.wxs.vtl                    # WiX Toolset WXS template to generate Merge Module
    ├── startup.vbs.vtl                # Startup script template (VB Script)
    ├── why-ini.vtl                    # WHY INI template
    └── wxs.vtl                        # WiX Toolset WXS template to generate MSI
```

An object called `info` of type [`PackagerSettings`](https://github.com/fvarrui/JavaPackager/blob/master/src/main/java/io/github/fvarrui/javapackager/packagers/PackagerSettings.java) is passed to all templates with all plugin properties.

You can use [default templates](https://github.com/fvarrui/JavaPackager/tree/master/src/main/resources) as examples to create your own templates, and use the `extra` map property to add your own properties in the plugin settings to use in your custom templates (e.g. `${info.extra["myProperty"]}`).

### Additional JVM options at runtime

When you build your app, all configuration details are hardcoded into the executable and cannot be changed without recreating or hacking it with a resource editor. JavaPackager introduces a feature that allows to pass additional JVM options at runtime from an `.l4j.ini` file (like [Launch4j](http://launch4j.sourceforge.net/docs.html) does, but available for all platforms in the same way). So, you can specify these options in the packager's configuration (packaging time), in INI file (runtime) or in both. 

The INI file's name must correspond to `${name}.l4j.ini` and it has to be located next to the executable on Windows and GNU/Linux, and in `Resources` folder on MacOS.

The options should be separated with spaces or new lines:

```ini
# Additional JVM options
-Dswing.aatext=true
-Dsomevar="%SOMEVAR%"
-Xms16m
```

> [!IMPORTANT] 
> An VM argument per line.

And then bundle this file with your app:

```xml
<additionalResources>
    <additionalResource>${name}.l4j.ini</additionalResource>
</additionalResources>
```

> [!NOTE]
> Last property copies `${name}.l4j.ini` file next to the EXE/binary on Windows/Linux, and in `Resources` folder on MacOS.

## How to use SNAPSHOT versions

[Here](https://oss.sonatype.org/content/repositories/snapshots/io/github/fvarrui/javapackager/) you can find the uploaded JavaPackager SNAPSHOT versions.

### Maven

Add the plugin repository to your `pom.xml`:

```xml
<pluginRepositories>
    <pluginRepository>
        <id>nexus</id>
        <name>nexus-snapshot-repository</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </pluginRepository>
</pluginRepositories>
```

And then you can use the latest SNAPSHOT version:

```xml
<plugin>    
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{javapackager.version}-SNAPSHOT</version>
    [...]
</plugin>
```

Or a specific SNAPSHOT version (specifying its timestamp and index):

```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{javapackager.version}-{timestamp}-{index}</version>
    [...]
</plugin>
```

> SNAPSHOT version example: `1.7.2-20230505.095442-5`.

### Gradle

Add the plugin repository to your `build.gradle` and use the latest SNAPSHOT version:

```groovy
buildscript {
    repositories {
        maven {
            url "https://oss.sonatype.org/content/repositories/snapshots"
        }
    }
    dependencies {
        classpath 'io.github.fvarrui:javapackager:{javapackager.version}-SNAPSHOT'
    }
}
```

Or set a specific SNAPSHOT version specifying its timestamp and index:

```groovy
buildscript {
    [...]
    dependencies {
        classpath 'io.github.fvarrui:javapackager:{javapackager.version}-{timestamp}-{index}'
    }
}
```

> SNAPSHOT version example: `1.7.2-20230505.095442-5`.

## How to build and install the plugin in your local repo

Execute next commands in BASH (GNU/Linux or macOS) or CMD (Windows):

1. Download source code and change to the project directory:

```bash
git clone https://github.com/fvarrui/JavaPackager.git [--branch devel]
cd JavaPackager
```

2. Compile, package and install the plugin in your local repository (ommit `./` on Windows):

```bash
./gradlew publishToMavenLocal
```

> [!IMPORTANT]
> It is recommended to build the plugin with Java 19.

## How to release the plugin to Maven Central

Run next command (ommit `./` on Windows):

```bash
./gradlew publish closeAndReleaseRepository
```
