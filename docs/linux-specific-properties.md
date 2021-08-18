# GNU/Linux specific properties

```xml
<linuxConfig>
	<pngFile>path/to/icon.png</pngFile>
	<generateDeb>true|false</generateDeb>
	<generateRpm>true|false</generateRpm>
    <wrapJar>true|false</wrapJar>
</linuxConfig>
```



| Property      | Mandatory | Default value | Description                                     |
| ------------- | --------- | ------------- | ----------------------------------------------- |
| `pngFile`     | :x:       | `null`        | Icon file.                                      |
| `generateDeb` | :x:       | `true`        | DEB package will be generated.                  |
| `generateRpm` | :x:       | `true`        | RPM package will be generated.                  |
| `wrapJar`     | :x:       | `true`        | Wraps JAR file inside the executable if `true`. |
