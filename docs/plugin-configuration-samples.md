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

Or:

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

>  This configuration will not bundle JRE, so a JRE is needed in final user host.

## Bundle a customized JRE

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
                <customizedJre>true</customizedJre>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Bundle an existing JRE

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
