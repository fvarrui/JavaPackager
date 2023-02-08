# Plugin configuration samples for Maven
## Minimal config
```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{latest-plugin-version-here}</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <mainClass>fvarrui.sample.Main</mainClass>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Also, JavaPackager plugin is able to get some properties from `pom.xml`, so you don't need to specify them twice:
```xml
<project>
    <properties>
        <exec.mainClass>fvarrui.sample.Main</exec.mainClass>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>io.github.fvarrui</groupId>
                <artifactId>javapackager</artifactId>
                <version>{latest-plugin-version-here}</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>package</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```
> :warning: This minimal configuration will not bundle a  JRE, so final user will need one in order to run the app.
## Bundle with a customized JRE
```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{latest-plugin-version-here}</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <mainClass>fvarrui.sample.Main</mainClass>
                <bundleJre>true</bundleJre>
            </configuration>
        </execution>
    </executions>
</plugin>
```
> `customizedJre` is `true` by default, so you don't have to specify it.
## Bundle with a full  JRE
```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{latest-plugin-version-here}</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <mainClass>fvarrui.sample.Main</mainClass>
                <bundleJre>true</bundleJre>
                <customizedJre>false</customizedJre>
            </configuration>
        </execution>
    </executions>
</plugin>
```
## Bundle with an existing JRE
```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{latest-plugin-version-here}</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <mainClass>fvarrui.sample.Main</mainClass>
                <bundleJre>true</bundleJre>
                <jrePath>C:\Program Files\Java\jre1.8.0_311</jrePath>
            </configuration>
        </execution>
    </executions>
</plugin>
```
## Bundle your own fat JAR
```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{latest-plugin-version-here}</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <mainClass>fvarrui.sample.Main</mainClass>
                <bundleJre>true</bundleJre>
                <runnableJar>path/to/your/own/fat.jar</runnableJar>
                <copyDependencies>false</copyDependencies>
            </configuration>
        </execution>
    </executions>
</plugin>
```
## Multiple executions
```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{latest-plugin-version-here}</version>
    <configuration>
        <mainClass>fvarrui.sample.Main</mainClass>
    </configuration>
    <executions>
        <execution>
            <id>bundle-with-jre</id>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <name>Sample</name>
                <bundleJre>true</bundleJre>
            </configuration>
        </execution>
        <execution>
            <id>bundle-without-jre</id>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <name>Sample-nojre</name>
                <bundleJre>false</bundleJre>
            </configuration>
        </execution>
    </executions>
</plugin>
```
E.g. on Windows, last configuration will generate next artifacts:
* `Sample_x.y.z.exe` with a bundled JRE.
* `Sample-nojre_x.y.z.exe` without JRE.
## Bundling for multiple platforms
```xml
<plugin>
    <groupId>io.github.fvarrui</groupId>
    <artifactId>javapackager</artifactId>
    <version>{latest-plugin-version-here}</version>
    <configuration>
        <bundleJre>true</bundleJre>
        <mainClass>fvarrui.sample.Main</mainClass>
        <generateInstaller>false</generateInstaller>
    </configuration>
    <executions>
        <execution>
            <id>bundling-for-windows</id>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <platform>windows</platform>
                <createZipball>true</createZipball>
            </configuration>
        </execution>
        <execution>
            <id>bundling-for-linux</id>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <platform>linux</platform>
                <createTarball>true</createTarball>
                <jdkPath>X:\\path\to\linux\jdk</jdkPath>
            </configuration>
        </execution>
        <execution>
            <id>bundling-for-mac</id>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
                <platform>mac</platform>
                <createTarball>true</createTarball>
                <jdkPath>X:\\path\to\mac\jdk</jdkPath>
            </configuration>
        </execution>
    </executions>
</plugin>
```

E.g. on Windows, last configuration will generate next artifacts:

* `${name}_${version}-linux.tar.gz` with the GNU/Linux application including a customized JRE.
* `${name}_${version}-mac.tar.gz` with the MacOS application including a customized JRE.
* `${name}_${version}-windows.zip` with the Windows application including a customized JRE.

As last sample is running on Windows, it's not necessary to specify a JDK when bundling for Windows (it uses current JDK by default). Otherwise, if running on GNU/Linux or MacOS, you have to specify a JDK for Windows.
