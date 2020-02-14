# Plugin configuration samples

## Minimal config

```xml
<plugin>
    <groupId>fvarrui.maven</groupId>
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
                <name>Sample</name>
                <organizationName>ACME</organizationName>
                <version>1.0.0</version>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Also, JavaPackager plugin is able to get some properties from `pom.xml`, so you don't need to specify them twice:

```xml
<project>
    <name>Sample</name>
    <version>1.0.0</version>
    <organization>
        <name>ACME</name>
    </organization>
    <build>
        <plugins>
            <plugin>
                <groupId>fvarrui.maven</groupId>
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
        </plugins>
    </build>    
</project>
```
> :warning: This minimal configuration will not bundle a  JRE, so final user will need one in order to run the app.

## Bundle with a customized JRE

```xml
<plugin>
    <groupId>fvarrui.maven</groupId>
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
                <name>Sample</name>
                <organizationName>ACME</organizationName>
                <version>1.0.0</version>
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
    <groupId>fvarrui.maven</groupId>
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
                <name>Sample</name>
                <organizationName>ACME</organizationName>
                <version>1.0.0</version>
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
    <groupId>fvarrui.maven</groupId>
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
                <name>Sample</name>
                <organizationName>ACME</organizationName>
                <version>1.0.0</version>
                <bundleJre>true</bundleJre>
                <jrePath>C:\Program Files\Java\jre1.8.0_231</jrePath>
            </configuration>
        </execution>
    </executions>
</plugin>
```
