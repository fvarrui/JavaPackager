# Windows specific properties

```xml
<winConfig>
    <!-- exe creation properties -->
	<headerType>gui</headerType>
	<companyName>${organizationName}</companyName>
	<fileVersion>1.0.0.0</fileVersion>
	<txtFileVersion>${version}</txtFileVersion>
	<productVersion>1.0.0.0</productVersion>
	<txtProductVersion>${version}</txtProductVersion>
	<fileDescription>${description}</fileDescription>
	<copyright>${organizationName}</copyright>
	<productName>${name}</productName>
	<internalName>${name}</internalName>
	<originalFilename>${name}.exe</originalFilename>
	<!-- installer generation properties -->
	<disableDirPage>true|false</disableDirPage>
	<disableProgramGroupPage>true|false</disableProgramGroupPage>
	<disableFinishedPage>true|false</disableFinishedPage>
	<createDesktopIconTask>true|false</createDesktopIconTask>
    <!-- enables/disables installers generation -->
    <generateSetup>true|false</generateSetup>
    <generateMsi>true|false</generateMsi>
</winConfig>
```

| Property        | Mandatory | Default value | Description                |
| --------------- | --------- | ------------- | -------------------------- |
| `generateSetup` | :x:       | `true`        | Generates Setup installer. |
| `generateMsi`   | :x:       | `true`        | Generates MSI installer.   |


## Exe creation properties

| Property                  | Mandatory | Default value         | Description                                                  |
| ------------------------- | --------- | --------------------- | ------------------------------------------------------------ |
| `headerType`              | :x:       | `"gui"`               | EXE header type: `console` or `gui`.                         |
| `companyName`             | :x:       | `${organizationName}` | EXE company name.                                            |
| `fileVersion`             | :x:       | `"1.0.0.0"`           | EXE file version.                                            |
| `txtFileVersion`          | :x:       | `${version}`          | EXE txt file version.                                        |
| `productVersion`          | :x:       | `"1.0.0.0"`           | EXE product version.                                         |
| `txtProductVersion`       | :x:       | `${version}`          | EXE txt product version.                                     |
| `fileDescription`         | :x:       | `${description}`      | EXE file description.                                        |
| `copyright`               | :x:       | `${organizationName}` | EXE copyright.                                               |
| `productName`             | :x:       | `${name}`             | EXE product name.                                            |
| `internalName`            | :x:       | `${name}`             | EXE internal name.                                           |
| `originalFilename`        | :x:       | `${name}.exe`         | EXE original filename.                                       |
| `trademark`               | :x:       | `null`                | EXE trademark.                                               |
| `language`                | :x:       | `null`                | EXE language.                                                |

## Setup generation properties

| Property                  | Mandatory | Default value         | Description                                                  |
| ------------------------- | --------- | --------------------- | ------------------------------------------------------------ |
| `disableDirPage`          | :x:       | `true`                | If this is set to `yes`, Setup will not show the **Select Destination Location** wizard page. |
| `disableProgramGroupPage` | :x:       | `true`                | If this is set to `yes`, Setup will not show the **Select Start Menu Folder** wizard page. |
| `disableFinishedPage`     | :x:       | `true`                | If this is set to `yes`, Setup will not show the **Setup Completed** wizard page. |
| `createDesktopIconTask`   | :x:       | `true`                | If this is set to `yes`, Setup will not ask for **desktop icon creation**. |