# Plugin configuration samples

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
                <jrePath>C:\Program Files\Java\jre1.8.0_231</jrePath>
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
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
            	<name>Sample-with-jre</name>
                <mainClass>fvarrui.sample.Main</mainClass>
                <bundleJre>true</bundleJre>
            </configuration>
        </execution>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>package</goal>
            </goals>
            <configuration>
            	<name>Sample-without-jre</name>
                <mainClass>fvarrui.sample.Main</mainClass>
                <bundleJre>false</bundleJre>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Last configuration, e.g. on Windows,  will generate next artifacts:
* `Sample-with-jre_x.y.z.exe` with a bundled JRE.
* `Sample-without-jre_x.y.z.exe` without JRE.
