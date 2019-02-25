# JavaPackager
JavaPackager Maven Plugin provides an easy way to package Java applications in native Windows, OS X, or Linux executables.

### How to build and install the plugin

Execute next commands in BASH (GNU/Linux) or CMD (Windows):

1. Download source code and change to the project directory:

```bash
git clone https://github.com/fvarrui/JavaPackager.git
cd JavaPackager
```

2. Compile and package the project:

```bash
mvn install
```

It installs  JavaPackager Maven plugin in your local repository.

### How to use the plugin

Add the following `plugin` to your `pom.xml`.

```xml
<plugin>
    <groupId>fvarrui.maven</groupId>
    <artifactId>javapackager</artifactId>
    <version>0.0.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <mainClass>path.to.your.mainClass</mainClass>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Where:

| Property  | Description                       |
| --------- | --------------------------------- |
| mainClass | Full path to your app main class. |

By default it will generate next artifacts:

- A native application in `target/app` directory with a bundled JRE.
- A `project-name_x.y.z.deb` package file on GNU/Linux. 
- A `project-name_x.y.z.rpm` package file on GNU/Linux.
- A `project-name_x.y.z.exe` installer file on Windows.
- A `project-name_x.y.z.dmg` installer file on Mac OS X.

> **x.y.z** is your project version number (e.g. 1.2.3).