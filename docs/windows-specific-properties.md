# Windows specific properties

```xml
<winConfig>
	<!-- properties used in EXE generation by launch4j -->
	<fileVersion>1.0.0.0</fileVersion>
	<txtFileVersion>${version}</txtFileVersion>
	<productVersion>1.0.0.0</productVersion>
	<txtProductVersion>${version}</txtProductVersion>
	<fileDescription>${description}</fileDescription>
	<copyright>${organizationName}</copyright>
	<productName>${name}</productName>
	<internalName>${name}</internalName>
	<originalFilename>${name}.exe</originalFilename>
</winConfig>
```

| Property            | Mandatory | Default value         | Description |
| ------------------- | --------- | --------------------- | ----------- |
| `fileVersion`       | :x:       | `"1.0.0.0"`           |             |
| `txtFileVersion`    | :x:       | `${version}`          |             |
| `productVersion`    | :x:       | `"1.0.0.0"`           |             |
| `txtProductVersion` | :x:       | `${version}`          |             |
| `fileDescription`   | :x:       | `${description}`      |             |
| `copyright`         | :x:       | `${organizationName}` |             |
| `productName`       | :x:       | `${name}`             |             |
| `internalName`      | :x:       | `${name}`             |             |
| `originalFilename`  | :x:       | `${name}.exe`         |             |
| `trademark`         | :x:       | `null`                |             |
| `language`          | :x:       | `null`                |             |