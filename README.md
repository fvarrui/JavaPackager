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
    <version>0.8.8</version>
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
| `projectname_projectversion.deb`          | DEB package file if it's executed on GNU/Linux.              |
| `projectname_projectversion.rpm`          | RPM package file if it's executed on GNU/Linux (requires **alien** & **rpmbuild**). |
| `projectname_projectversion.exe`          | Installer file if it's executed on Windows (requires [**InnoSetup**](http://www.jrsoftware.org/isinfo.php)). |
| `projectname_projectversion.dmg`          | Disk image file if it's executed on Mac OS X.                |

>  :warning: DEB, RPM, EXE installer and DMG files will be ommited if `generateInstaller` property is `false` or if target platform is different than running platform.

### Plugin configutation properties

| Property                | Mandatory | Default value                  | Description                                                  |
| ----------------------- | --------- | ------------------------------ | ------------------------------------------------------------ |
| `additionalModules`     | No        | []                             | Adds additional modules other than the ones identified by `jdeps` before calling `jlink`. |
| `additionalResources`   | No        | []                             | Additional files and folders to include in the bundled app.  |
| `administratorRequired` | No        | `false`                        | If `true`, app will run with administrator privileges.       |
| `bundleJre`             | No        | `false`                        | Embeds a customized JRE with the app.                        |
| `copyDependencies`      | No        | `true`                         | If `true`, all dependencies (JAR files) will be bundled with the app. |
| `customizedJre`         | No        | `true`                         | If `true`, a customized JRE will be generated, including only needed modules. Otherwise, all modules will be included. |
| `displayName`           | No        | `${project.name}`              | App name to show.                                            |
| `envPath`               | No        | `null`                         | Defines environment variable PATH in GNU/Linux and Mac OS X startup scripts. |
| `generateInstaller`     | No        | `true`                         | Generates an installer for the app.                          |
| `iconFile`              | No        | `null`                         | Path to the app icon file (PNG, ICO or ICNS).                |
| `jrePath`               | No        | `""`                           | Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least. |
| `licenseFile`           | No        | `${project.licenses[0].url}`   | Path to project license file.                                |
| `mainClass`             | Yes       | `null`                         | Full path to your app main class.                            |
| `modules`               | No        | []                             | Uses specified modules to customize the bundled JRE. Don't use `jdeps` to get module dependencies. |
| `organizationName`      | No        | `${project.organization.name}` | Organization name.                                           |
| `organizationUrl`       | No        | `${project.organization.url}`  | Organization website URL.                                    |
| `organizationEmail`     | No        | `null`                         | Organization email.                                          |
| `platform`              | No        | `auto`                         | Specifies the target platform, which could be different to current one. Possible values:  `auto`, `mac`, `linux`, `windows`. Use `auto`  for using current platform as target. |
| `runnableJar`           | No        | `null`                         | Specifies your own JAR file to be bundled. If it's ommited, the plugin packages your code in a runnable JAR and bundle with the app. |
| `url`                   | No        | `null`                         | App website URL.                                             |
| `vmArgs`                | No        | []                             | Adds VM arguments.                                           |

> See [**Older documentation**](#Older documentation) for previous versions properties.

### Plugin assets

Some assets, such as application icons, could be located in `assets` folder organized by platform, and so it would not be necessary to specify the `iconFile` property:

```
<project>
└── assets
	├── linux
	│   └── projectname.png		# on GNU/Linux it has to be a png image
	├── macosx
	│   └── projectname.icns	# on Mac OS X it has to be a icns file
	└── windows
	    └── projectname.ico		# on Windows it has to be an ico file
```

> **projectname** corresponds to `name` property in `pom.xml`.

> :warning: If `iconFile` property is not specified and it can't find the correct icon in `assets` folder, it will use next icon by default for all platforms:
>
> ![Default icon](https://raw.githubusercontent.com/fvarrui/JavaPackager/master/src/main/resources/linux/default-icon.png)

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

## Older documentation

- [v0.8.7](https://github.com/fvarrui/JavaPackager/blob/882f7e2eed31d67940d8f34e2a4ebb44ba0e8001/README.md)
- [v0.8.6](https://github.com/fvarrui/JavaPackager/blob/63f7787ba769672701f49fcf014cb0f7cea86117/README.md)