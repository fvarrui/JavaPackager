# Windows specific properties

```xml
<winConfig>
    
	<!-- general properties -->
	<icoFile>path/to/icon.ico</icoFile>
	<generateSetup>true|false</generateSetup>
	<generateMsi>true|false</generateMsi>
	<generateMsm>true|false</generateMsm>
    
	<!-- exe creation properties -->
	<headerType>gui</headerType>
	<wrapJar>true|false</wrapJar>
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
    
	<!-- setup generation properties -->
	<setupMode>installForAllUsers|installForCurrentUser|askTheUser</setupMode>
	<setupLanguages>
		<english>compiler:Default.isl</english>
		<spanish>compiler:Languages\Spanish.isl</english>
		[...]
	</setupLanguages>
	<disableDirPage>true|false</disableDirPage>
	<disableProgramGroupPage>true|false</disableProgramGroupPage>
	<disableFinishedPage>true|false</disableFinishedPage>
	<createDesktopIconTask>true|false</createDesktopIconTask>
    
</winConfig>
```

| Property        | Mandatory | Default value | Description                                  |
| --------------- | --------- | ------------- | -------------------------------------------- |
| `icoFile`       | :x:       | `null`        | Icon file.                                   |
| `generateSetup` | :x:       | `true`        | Generates Setup installer.                   |
| `generateMsi`   | :x:       | `true`        | Generates MSI installer.                     |
| `generateMsm`   | :x:       | `false`       | Generates MSI merge module. **Coming soon!** |


## Exe creation properties

| Property            | Mandatory | Default value         | Description                          |
| ------------------- | --------- | --------------------- | ------------------------------------ |
| `headerType`        | :x:       | `"gui"`               | EXE header type: `console` or `gui`. |
| `wrapJar`           | :x:       | `true`                | Wrap JAR file in native EXE.         |
| `companyName`       | :x:       | `${organizationName}` | EXE company name.                    |
| `fileVersion`       | :x:       | `"1.0.0.0"`           | EXE file version.                    |
| `txtFileVersion`    | :x:       | `${version}`          | EXE txt file version.                |
| `productVersion`    | :x:       | `"1.0.0.0"`           | EXE product version.                 |
| `txtProductVersion` | :x:       | `${version}`          | EXE txt product version.             |
| `fileDescription`   | :x:       | `${description}`      | EXE file description.                |
| `copyright`         | :x:       | `${organizationName}` | EXE copyright.                       |
| `productName`       | :x:       | `${name}`             | EXE product name.                    |
| `internalName`      | :x:       | `${name}`             | EXE internal name.                   |
| `originalFilename`  | :x:       | `${name}.exe`         | EXE original filename.               |
| `trademark`         | :x:       | `null`                | EXE trademark.                       |
| `language`          | :x:       | `null`                | EXE language.                        |

## Setup generation properties

| Property                  | Mandatory | Default value                                                | Description                                                  |
| ------------------------- | --------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `setupMode`               | :x:       | `installForAllUsers`                                         | Setup installation mode: require administrative privileges or not. [*](#setupmode) |
| `setupLanguages`          | :x:       | `<english>compiler:Default.isl</english><spanish>compiler:Languages\Spanish.isl</spanish>` | Map with setup languages.                                    |
| `disableDirPage`          | :x:       | `true`                                                       | If this is set to `yes`, Setup will not show the **Select Destination Location** wizard page. |
| `disableProgramGroupPage` | :x:       | `true`                                                       | If this is set to `yes`, Setup will not show the **Select Start Menu Folder** wizard page. |
| `disableFinishedPage`     | :x:       | `true`                                                       | If this is set to `yes`, Setup will not show the **Setup Completed** wizard page. |
| `createDesktopIconTask`   | :x:       | `true`                                                       | If this is set to `yes`, Setup will not ask for **desktop icon creation**. |

### SetupMode

Property `winConfig.setupMode` can be set with 3 possible values:

- **installForAllUsers** *(default value)*: installs the app for the all users in `%ProgramFiles%` folder (behaviour can be changed when running setup installer from command-line with `/currentuser` argument). 
- **installForCurrentUser**: installs the app for the current user in `%USERPROFILE%\AppData\Local\Programs` folder (behaviour can be changed when running setup installer from command-line with `/allusers` argument).
- **askTheUser**: asks to the final user if the app has to be installed for all users or only for the current user.