# GNU/Linux specific properties

```xml
<linuxConfig>
	<pngFile>path/to/icon.png</pngFile>
	<xpmFile>path/to/icon.xpm</xpmFile>
	<generateDeb>true|false</generateDeb>
	<generateRpm>true|false</generateRpm>
</linuxConfig>
```

| Property      | Mandatory | Default value | Description                    |
| ------------- | --------- | ------------- | ------------------------------ |
| `pngFile`     | :x:       | `null`        | Icon file.                     |
| `xpmFile`     | :x:       | `null`        | Icon file for RPM generation.  |
| `generateDeb` | :x:       | `true`        | DEB package will be generated. |
| `generateRpm` | :x:       | `true`        | RPM package will be generated. |
