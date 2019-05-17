# JavaPackager
JavaPackager Maven Plugin provides an easy way to package Java applications in native Windows, OS X, or Linux executables.

### How to build and install the plugin

Execute next commands in BASH (GNU/Linux) or CMD (Windows):

1. Download source code and change to the project directory:

```bash
git clone https://github.com/fvarrui/JavaPackager.git
cd JavaPackager
```

2. Compile, package and install the plugin in your local repository:

```bash
mvn install
```

### How to use the plugin

Add the following `plugin` tag to your `pom.xml`.

```xml
<plugin>
    <groupId>fvarrui.maven</groupId>
    <artifactId>javapackager</artifactId>
    <version>0.7.0</version>
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
            </configuration>
        </execution>
    </executions>
</plugin>
```

Where:

| Property                | Mandatory | Default value | Description                                                 |
| ----------------------- | --------- | ------------- | ----------------------------------------------------------- |
| `mainClass`             | Yes       | `null`        | Full path to your app main class.                           |
| `bundleJre`             | No        | `false`       | Embed a customized JRE with the app.                        |
| `forceJreOptimization`  | No        | `false`       | If JDK version < 13, it will try to reduce the bundled JRE. |
| `administratorRequired` | No        | `false`       | If true, app will run with administrator privileges.        |
| `additionalResources`   | No        | []            | Additional files and folders to include in the bundled app. |

Some assets, like app icons, must be located in:

```
<project>
└── assets
	├── linux
	│   └── projectname.png		# on GNU/Linux it has to be a png image
	├── macosx
	│   └── projectname.icns	# on Mac OS X it has to be a icns file
	└── windows
	    └── projectname.ico		# on Windows it has to be a ico file
```

>  Where **projectname** corresponds to `name` property in `pom.xml`.

> :warning: If icon is not specified, it will use a default icon for every platform.

Execute next command in project's root folder:

```bash
mvn package
```

By default, it will generate next artifacts in `target ` folder:

- A native application in `app` directory with a bundled JRE.
- A `projectname_projectversion.deb` package file on GNU/Linux. 
- A `projectname_projectversion.rpm` package file on GNU/Linux (requires alien && rpmbuild).
- A `projectname_projectversion.exe` installer file on Windows.
- A `projectname_projectversion.dmg` installer file on Mac OS X.

## Contributors

Thanks to the following contributors, who have provided patches and other assistance:

* **[Heinrich Drendorf (nachsommer)](https://github.com/nachsommer)**