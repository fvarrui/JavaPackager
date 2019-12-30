# JavaPackager
JavaPackager Maven Plugin provides an easy way to package Java applications in native Windows, OS X, or Linux executables.

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

## How to use the plugin

Add the following `pluginRepository` to your `pom.xml`:

```xml
<pluginRepository>
    <id>javapackager-repo</id>
    <url>https://github.com/fvarrui/JavaPackager/raw/master/releases</url>
    <releases>
        <enabled>true</enabled>
    </releases>
</pluginRepository>
```

And the following `plugin` tag to your `pom.xml`.

```xml
<plugin>
    <groupId>fvarrui.maven</groupId>
    <artifactId>javapackager</artifactId>
    <version>0.8.5</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <mainClass>path.to.your.mainClass</mainClass>
                <bundleJre>true|false</bundleJre>
                <administratorRequired>true|false</administratorRequired>
                <additionalResources>
                    <param>file path</param>
                    <param>folder path</param>
                    <param>...</param>
                </additionalResources>
                <generateInstaller>true|false</generateInstaller>        
                [...]
            </configuration>
        </execution>
    </executions>
</plugin>
```

Where:

| Property                          | Mandatory | Default value                  | Description                                                                                                                                                      |
| --------------------------------- | --------- | ------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `mainClass`                       | Yes       | `null`                         | Full path to your app main class.                                                                                                                                |
| `bundleJre`                       | No        | `false`                        | Embed a customized JRE with the app.                                                                                                                             |
| `forceJreOptimization`            | No        | `false`                        | Although JDK version < 13, it will try to reduce the bundled JRE.                                                                                                |
| `jrePath`                         | No        | `""`                           | Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least.                        |
| `moduleDependenceAnalysisOption`  | No        | `"--list-deps"`                | When generating a customized JRE, this option allows to specify a different Module dependence analysis option other than the default (--list-deps) for jdeps     |
| `additionalModules`               | No        | `""`                           | When generating a customized JRE, allows adding aditional modules other than the ones identified by jdeps before calling jlink.                                  |
| `administratorRequired`           | No        | `false`                        | If true, app will run with administrator privileges.                                                                                                             |
| `additionalResources`             | No        | []                             | Additional files and folders to include in the bundled app.                                                                                                      |
| `generateInstaller`               | No        | `true`                         | Generate an installer for the app.                                                                                                                               |
| `displayName`                     | No        | `${project.name}`              | App name to show.                                                                                                                                                |
| `iconFile`                        | No        | `null`                         | Path to the app icon file (PNG, ICO or ICNS).                                                                                                                    |
| `licenseFile`                     | No        | `${project.licenses[0].url}`   | Path to project license file.                                                                                                                                    |
| `url`                             | No        | `null`                         | App website URL.                                                                                                                                                 |
| `organizationName`                | No        | `${project.organization.name}` | Organization name.                                                                                                                                               |
| `organizationUrl`                 | No        | `${project.organization.url}`  | Organization website URL.                                                                                                                                        |
| `organizationEmail`               | No        | `null`                         | Organization email.                                                                                                                                              |

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

Execute next command in project's root folder:

```bash
mvn package
```

By default, it will generate next artifacts in `target ` folder:

- `app`: directory with the native application.
- `projectname-projectversion-runnable.jar`: runnable JAR file.
- `projectname_projectversion.deb`: DEB package file if it's executed on GNU/Linux. 
- `projectname_projectversion.rpm`: RPM package file if it's executed on GNU/Linux (requires **alien** & **rpmbuild**).
- `projectname_projectversion.exe`: installer file if it's executed on Windows (requires [**InnoSetup**](http://www.jrsoftware.org/isinfo.php)).
- `projectname_projectversion.dmg`: disk image file if it's executed on Mac OS X.

>  :warning: DEB, RPM, EXE installer and DMG files will be ommited if `generateInstaller` property is `false`.

