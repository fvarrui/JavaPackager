# JavaPackager
JavaPackager is a Maven plugin which provides an easy way to package Java applications in native Windows, Mac OS X, or GNU/Linux executables, and generates installers for them.

> Plugin published at [Maven Central](https://search.maven.org/artifact/io.github.fvarrui/javapackager) since v0.9.0. See published versions at [releases](https://github.com/fvarrui/JavaPackager/releases) section.

> SNAPSHOT versions are not pusblished at Maven Central, so you have to [install them manually](#how-to-build-and-install-the-plugin). 

## How to use the plugin

### Config your project

Add the following `plugin` tag to your `pom.xml`:

```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>0.9.5|0.9.6-SNAPSHOT</version>    
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
                    <param>file path</param>
                    <param>folder path</param>
                    <param>...</param>
                </additionalResources>
                <additionalModules>
                    <param>module1</param>
                    <param>module2</param>
                    <param>...</param>
                </additionalModules>
                [...]
            </configuration>
        </execution>
    </executions>
</plugin>
```

> See [plugin configuration samples](docs/plugin-configuration-samples.md) to know more.

### Package your app

Execute next command in project's root folder:

```bash
mvn package
```

And by default it will generate next artifacts in `target ` folder:

| Artifact                           | Description                                                  |
| ---------------------------------- | ------------------------------------------------------------ |
| `${name}`                          | Directory with the native application and other needed assets. |
| `${name}-${version}-runnable.jar`  | Runnable JAR file.                                           |
| `${name}_${version}.deb`           | DEB package file if it's executed on GNU/Linux (requires **dpkg-deb**). |
| `${name}_${version}.rpm`           | RPM package file if it's executed on GNU/Linux (requires **rpm-build**). |
| `${name}_${version}.exe`           | Installer file if it's executed on Windows (requires [**Inno Setup**](http://www.jrsoftware.org/isinfo.php)). |
| `${name}_${version}.dmg`           | Disk image file if it's executed on Mac OS X (requires **hdiutil**). |
| `${name}-${version}-bundle.zip`    | Zipball containing generated directory `${name}` if `createZipball` property is `true`. |
| `${name}-${version}-bundle.tar`    | Tarball containing generated directory `${name}` if `createTarball` property is `true`. |
| `${name}-${version}-bundle.tar.gz` | Compressed tarball containing generated directory `${name}` if `createTarball` property is `true`. |

>  :warning: DEB, RPM, EXE installer and DMG files will be ommited if `generateInstaller` property is `false` or if target platform is different from current platform.

### Plugin configutation properties

| Property                | Mandatory          | Default value                                                | Description                                                  |
| ----------------------- | ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `additionalModules`     | :x:                | `[]`                                                         | Additional modules to the ones identified by `jdeps` or the specified with `modules` property. |
| `additionalResources`   | :x:                | `[]`                                                         | Additional files and folders to include in the bundled app.  |
| `administratorRequired` | :x:                | `false`                                                      | App will run as administrator (with elevated privileges).    |
| `bundleJre`             | :x:                | `false`                                                      | Embeds a customized JRE with the app.                        |
| `copyDependencies`      | :x:                | `true`                                                       | Bundles all dependencies (JAR files) with the app.           |
| `createTarball`         | :x:                | `false`                                                      | Bundles app folder in tarball.                               |
| `createZipball`         | :x:                | `false`                                                      | Bundles app folder in zipball.                               |
| `customizedJre`         | :x:                | `true`                                                       | Generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included. |
| `description`           | :x:                | `${project.description}` or `${displayName}`                 | Project description.                                         |
| `displayName`           | :x:                | `${project.name}` or `${name}`                               | App name to show.                                            |
| `envPath`               | :x:                | `null`                                                       | Defines PATH environment variable in GNU/Linux and Mac OS X startup scripts. |
| `generateInstaller`     | :x:                | `true`                                                       | Generates an installer for the app.                          |
| `iconFile`              | :x:                | `null`                                                       | Path to the app icon file (PNG, XPM, ICO or ICNS).           |
| `jreDirectoryName`      | :x:                | `"jre"`                                                      | Bundled JRE directory name.                                  |
| `jrePath`               | :x:                | `""`                                                         | Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least. |
| `licenseFile`           | :x:                | `${project.licenses[0].url}` or `${project.basedir}/LICENSE` | Path to project license file.                                |
| `mainClass`             | :heavy_check_mark: | `${exec.mainClass}`                                          | Full path to your app main class.                            |
| `modules`               | :x:                | `[]`                                                         | Defines modules to customize the bundled JRE. Don't use `jdeps` to get module dependencies. |
| `name`                  | :x:                | `${project.name}` or `${project.artifactId}`                 | App name.                                                    |
| `organizationName`      | :x:                | `${project.organization.name}` or `"ACME"`                   | Organization name.                                           |
| `organizationUrl`       | :x:                | `${project.organization.url}`                                | Organization website URL.                                    |
| `organizationEmail`     | :x:                | `null`                                                       | Organization email.                                          |
| `platform`              | :x:                | `auto`                                                       | Defines the target platform, which could be different to the execution platform. Possible values:  `auto`, `mac`, `linux`, `windows`. Use `auto`  for using execution platform as target. |
| `runnableJar`           | :x:                | `null`                                                       | Defines your own JAR file to be bundled. If it's ommited, the plugin packages your code in a runnable JAR and bundle it with the app. |
| `url`                   | :x:                | `null`                                                       | App website URL.                                             |
| ` version`              | :x:                | `${project.version}`                                         | Project version.                                             |
| `versionInfo`           | :x:                | `null`                                                       | [Version information](#version-information-property-example) for native Windows `.exe` file. |
| `vmArgs`                | :x:                | `[]`                                                         | Adds VM arguments.                                           |

> See [**Older documentation**](#older-documentation) for previous versions properties.

> :warning: Be careful when using the `platform` property if your project uses platform dependent libraries, so the libraries of the current platform will be copied, not those of the target platform. You can solve this problem using `classifiers`.

#### Version information property example

Using default values:

```xml
<versionInfo>
	<fileVersion>1.0.0.0</fileVersion>
	<txtFileVersion>${version}</txtFileVersion>
	<productVersion>1.0.0.0</productVersion>
	<txtProductVersion>${version}</txtProductVersion>
	<fileDescription>${description}</fileDescription>
	<copyright>${organizationName}</copyright>
	<productName>${name}</productName>
	<internalName>${name}</internalName>
	<originalFilename>${name}.exe</originalFilename>
</versionInfo>
```

### Plugin assets

Some assets, such as application icons and Velocity templates, could be placed in `assets` folder organized by platform.

```
<project>/
└── assets/
	├── linux/
	├── mac/
	└── windows/
```

#### Icons

If icons are located in `assets` folders, it would not be necessary to specify the `iconFile` property:

```
<project>/
└── assets/
	├── linux/
	│   ├── ${name}.png     # on GNU/Linux it has to be a PNG file for DEB package
	│   └── ${name}.xpm     # and XPM file for RPM package
	├── mac/
	│   └── ${name}.icns    # on Mac OS X it has to be a ICNS file
	└── windows/
	    └── ${name}.ico     # on Windows it has to be a ICO file
```

> :warning: If `iconFile` plugin property is not specified and it can't find the correct icon in `assets` folder, it will use an [icon by default](https://raw.githubusercontent.com/fvarrui/JavaPackager/master/src/main/resources/linux/default-icon.png) for all platforms.
>

#### Velocity templates

[Velocity](https://velocity.apache.org/engine/2.0/user-guide.html) templates (.vtl files) are used to generate some artifacts which have to be bundled with the app.

It is possible to use your own customized templates. You just have to put one of the following templates in the `assets` folder organized by platform, and the plugin will use these templates instead of default ones:

```
<project>/
└── assets/
	├── linux/
	|   ├── control.vtl        # DEB control template
	|   ├── desktop.vtl        # Desktop template
	│   └── startup.sh.vtl     # Startup script template
	├── mac/
	|   ├── Info.plist.vtl     # Info.plist template
	│   └── startup.vtl        # Startup script template
	├── windows/
	|   ├── exe.manifest.vtl   # exe.manifest template
	│   └── iss.vtl            # Inno Setup Script template
	└── assembly.xml.vtl       # assembly.xml template for maven-assembly-plugin
```

> Use [default templates](https://github.com/fvarrui/JavaPackager/tree/master/src/main/resources) as examples.

A map called `info` is passed to all templates when they are rendered with next keys:

| Key                             | Type    | Description                                      |
| ------------------------------- | ------- | ------------------------------------------------ |
| `${info.name}`                  | String  | Same as `name` plugin property.                  |
| `${info.displayName}`           | String  | Same as `displayName` plugin property.           |
| `${info.version}`               | String  | Same as `version` plugin property.               |
| `${info.description}`           | String  | Same as `description` plugin property.           |
| `${info.url}`                   | String  | Same as `url` plugin property.                   |
| `${info.organizationName}`      | String  | Same as `organizationName` plugin property.      |
| `${info.organizationUrl}`       | String  | Same as `organizationUrl` plugin property.       |
| `${info.organizationEmail}`     | String  | Same as `organizationEmail` plugin property.     |
| `${info.administratorRequired}` | Boolean | Same as `administratorRequired` plugin property. |
| `${info.bundleJre}`             | Boolean | Same as `bundleJre` plugin property.             |
| `${info.jarFile}`               | String  | Full path to runnable JAR file.                  |
| `${info.jreDirectoryName}`      | String  | Same as `jreDirectoryName` plugin property.      |
| `${info.license}`               | String  | Full path to license file.                       |
| `${info.envPath}`               | String  | Same as `envPath` plugin property.               |
| `${info.vmArgs}`                | String  | Same as `vmArgs` plugin property.                |
| `${info.createTarball}`         | Boolean | Same as `createTarball` plugin property.         |
| `${info.createZipball}`         | Boolean | Same as `createZipball` plugin property.         |

## How to build and install the plugin

> Useful to try SNAPSHOT versions.

Execute next commands in BASH (GNU/Linux or macOS) or CMD (Windows):

1. Download source code and change to the project directory:

```bash
git clone https://github.com/fvarrui/JavaPackager.git
cd JavaPackager
```

2. Compile, package and install the plugin in your local repository:

```bash
mvn install
```

## How to publish the plugin on Maven Central

```bash
mvn clean release:clean
mvn release:prepare
mvn release:perform
```

> Related [guide](https://dzone.com/articles/publish-your-artifacts-to-maven-central).

## Future features

Check the [TO-DO list](https://github.com/fvarrui/JavaPackager/projects/1#column-7704117) to know the features we plan to add to JavaPackager.

## Older documentation

- [v0.9.4](https://github.com/fvarrui/JavaPackager/blob/v0.9.4/README.md)
- [v0.9.3](https://github.com/fvarrui/JavaPackager/blob/v0.9.3/README.md)
- [v0.9.1](https://github.com/fvarrui/JavaPackager/blob/v0.9.1/README.md)
- [v0.9.0](https://github.com/fvarrui/JavaPackager/blob/v0.9.0/README.md)
- [v0.8.9](https://github.com/fvarrui/JavaPackager/blob/v0.8.9/README.md)
- [v0.8.8](https://github.com/fvarrui/JavaPackager/blob/v0.8.8/README.md)
- [v0.8.7](https://github.com/fvarrui/JavaPackager/blob/v0.8.7/README.md)
- [v0.8.6](https://github.com/fvarrui/JavaPackager/blob/v0.8.6/README.md)
- [v0.8.5](https://github.com/fvarrui/JavaPackager/blob/v0.8.5/README.md)
- [v0.8.4](https://github.com/fvarrui/JavaPackager/blob/v0.8.4/README.md)
- [v0.8.3](https://github.com/fvarrui/JavaPackager/blob/v0.8.3/README.md)
