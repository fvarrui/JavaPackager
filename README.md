# JavaPackager
JavaPackager is a Maven plugin which provides an easy way to package Java applications in native Windows, Mac OS X, or GNU/Linux executables, and generates installers for them.

## How to use the plugin

### Config your project 

Add to your `pom.xml` the following `pluginRepository` tag:

```xml
<pluginRepository>
    <id>javapackager-repo</id>
    <url>https://github.com/fvarrui/JavaPackager/raw/master/releases</url>
    <releases>
        <enabled>true</enabled>
    </releases>
</pluginRepository>
```

And the following `plugin` tag.

```xml
<plugin>
    <groupId>fvarrui.maven</groupId>
    <artifactId>javapackager</artifactId>
    <version>0.8.9</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <mainClass>path.to.your.mainClass</mainClass>
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

### Package your app

Execute next command in project's root folder:

```bash
mvn package
```

And by default it will generate next artifacts in `target ` folder:

| Artifact                                  | Description                                                  |
| ----------------------------------------- | ------------------------------------------------------------ |
| `app`                                     | Directory with the native application and other needed assets. |
| `projectname-projectversion-runnable.jar` | Runnable JAR file.                                           |
| `projectname_projectversion.deb`          | DEB package file if it's executed on GNU/Linux (requires **dpkg-deb**).              |
| `projectname_projectversion.rpm`          | RPM package file if it's executed on GNU/Linux (requires **alien** & **rpmbuild**). |
| `projectname_projectversion.exe`          | Installer file if it's executed on Windows (requires [**Inno Setup**](http://www.jrsoftware.org/isinfo.php)). |
| `projectname_projectversion.dmg`          | Disk image file if it's executed on Mac OS X (requires **hdiutil**).                |

>  :warning: DEB, RPM, EXE installer and DMG files will be ommited if `generateInstaller` plugin property is `false` or if target platform is different from execution platform.

### Plugin configutation properties

| Property                | Mandatory | Default value                  | Description                                                  |
| ----------------------- | --------- | ------------------------------ | ------------------------------------------------------------ |
| `additionalModules`     | No        | []                             | Additional modules to the ones identified by `jdeps` or the specified with `modules` property. |
| `additionalResources`   | No        | []                             | Additional files and folders to include in the bundled app.  |
| `administratorRequired` | No        | `false`                        | App will run as administrator (with elevated privileges).    |
| `bundleJre`             | No        | `false`                        | Embeds a customized JRE with the app.                        |
| `copyDependencies`      | No        | `true`                         | Bundles all dependencies (JAR files) with the app.           |
| `customizedJre`         | No        | `true`                         | Generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included. |
| `displayName`           | No        | `${project.name}`              | App name to show.                                            |
| `envPath`               | No        | `null`                         | Defines PATH environment variable in GNU/Linux and Mac OS X startup scripts. |
| `generateInstaller`     | No        | `true`                         | Generates an installer for the app.                          |
| `iconFile`              | No        | `null`                         | Path to the app icon file (PNG, ICO or ICNS).                |
| `jrePath`               | No        | `""`                           | Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least. |
| `licenseFile`           | No        | `${project.licenses[0].url}`   | Path to project license file.                                |
| `mainClass`             | Yes       | `null`                         | Full path to your app main class.                            |
| `modules`               | No        | []                             | Defines modules to customize the bundled JRE. Don't use `jdeps` to get module dependencies. |
| `organizationName`      | No        | `${project.organization.name}` | Organization name.                                           |
| `organizationUrl`       | No        | `${project.organization.url}`  | Organization website URL.                                    |
| `organizationEmail`     | No        | `null`                         | Organization email.                                          |
| `platform`              | No        | `auto`                         | Defines the target platform, which could be different to the execution platform. Possible values:  `auto`, `mac`, `linux`, `windows`. Use `auto`  for using execution platform as target. |
| `runnableJar`           | No        | `null`                         | Defines your own JAR file to be bundled. If it's ommited, the plugin packages your code in a runnable JAR and bundle it with the app. |
| `url`                   | No        | `null`                         | App website URL.                                             |
| `vmArgs`                | No        | []                             | Adds VM arguments.                                           |

> See [**Older documentation**](#older-documentation) for previous versions properties.

### Plugin assets

Some assets, such as application icons and Velocity templates, could be placed in `assets` folder organized by platform.

```
<project>/
└── assets/
	├── linux/
	├── macosx/
	└── windows/
```

#### Icons

If icons are located in `assets` folders, it would not be necessary to specify the `iconFile` property:

```
<project>/
└── assets/
	├── linux/
	│   └── projectname.png		# on GNU/Linux it has to be a png image
	├── macosx/
	│   └── projectname.icns	# on Mac OS X it has to be a icns file
	└── windows/
	    └── projectname.ico		# on Windows it has to be a ico file
```

> **projectname** corresponds to `name` plugin property.

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
	├── macosx/
	|   ├── Info.plist.vtl     # Info.plist template
	│   └── startup.vtl        # Startup script template
	└── windows/
	    ├── exe.manifest.vtl   # exe.manifest template
	    └── iss.vtl            # Inno Setup Script template
```

> Use [default templates](https://github.com/fvarrui/JavaPackager/tree/master/src/main/resources) as examples.

A map called `info` is passed to all templates when they are rendered with next properties:

| Property                        | Type    | Description                                      |
| ------------------------------- | ------- | ------------------------------------------------ |
| `${info.name}`                  | String  | Same as `name` plugin property.                  |
| `${info.displayName}`                | String  | Same as `displayName` plugin property.           |
| `${info.version}`               | String  | Same as `version` plugin property.               |
| `${info.description}`           | String  | Same as `description` plugin property.           |
| `${info.url}`                   | String  | Same as `url` plugin property.                   |
| `${info.organizationName}`      | String  | Same as `organizationName` plugin property.      |
| `${info.organizationUrl}`       | String  | Same as `organizationUrl` plugin property.       |
| `${info.organizationEmail}`     | String  | Same as `organizationEmail` plugin property.     |
| `${info.administratorRequired}` | Boolean | Same as `administratorRequired` plugin property. |
| `${info.bundleJre}`             | Boolean | Same as `bundleJre` plugin property.             |
| `${info.jarFile}`               | String  | Full path to runnable JAR file.                  |
| `${info.license}`               | String  | Full path to license file.                       |
| `${info.envPath}`               | String  | Same as `envPath` plugin property.               |
| `${info.vmArgs}`                | String  | Same as `vmArgs` plugin property.                |

## How to build and install the plugin

Execute next commands in BASH (GNU/Linux or macOS) or CMD (Windows):

1. Download source code and change to the project directory:

```bash
git clone https://github.com/fvarrui/JavaPackager.git
cd JavaPackager
```

2. Compile, package and install the plugin in your local repository and in the project's `releases` folder:

```bash
mvn install
```

## Future features

Check the [TO-DO list](https://github.com/fvarrui/JavaPackager/projects/1#column-7704117) to know the features we plan to add to JavaPackager.

## Older documentation

- [v0.8.8](https://github.com/fvarrui/JavaPackager/blob/v0.8.8/README.md)
- [v0.8.7](https://github.com/fvarrui/JavaPackager/blob/v0.8.7/README.md)
- [v0.8.6](https://github.com/fvarrui/JavaPackager/blob/v0.8.6/README.md)
- [v0.8.5](https://github.com/fvarrui/JavaPackager/blob/v0.8.5/README.md)
- [v0.8.4](https://github.com/fvarrui/JavaPackager/blob/v0.8.4/README.md)
- [v0.8.3](https://github.com/fvarrui/JavaPackager/blob/v0.8.3/README.md)
