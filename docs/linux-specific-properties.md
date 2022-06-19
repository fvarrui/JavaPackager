# GNU/Linux specific properties

```xml
<linuxConfig>
    <pngFile>path/to/icon.png</pngFile>
    <generateAppImage>true|false</generateAppImage>
    <generateDeb>true|false</generateDeb>
    <generateRpm>true|false</generateRpm>
    <wrapJar>true|false</wrapJar>
    <categories>
        <category>Utility</category>
        ...
    </categories>
</linuxConfig>
```

| Property           | Mandatory | Default value  | Description                                                                                                                  |
| ------------------ | --------- | -------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| `pngFile`          | :x:       | `null`         | Icon file.                                                                                                                   |
| `generateAppImage` | :x:       | `true`         | [AppImage](https://appimage.org/) package will be generated.                                                                 |
| `generateDeb`      | :x:       | `true`         | DEB package will be generated.                                                                                               |
| `generateRpm`      | :x:       | `true`         | RPM package will be generated.                                                                                               |
| `wrapJar`          | :x:       | `true`         | Wraps JAR file inside the executable if `true`.                                                                              |
| `categories`       | :x:       | `[ "Utility"]` | [Main categories](https://specifications.freedesktop.org/menu-spec/latest/apa.html) in the application's desktop entry file. |
