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

    <!-- choose EXE creation tool -->
    <exeCreationTool>launch4j|winrun4j</exeCreationTool>
    <vmLocation>relative/path/to/jvm.dll</vmLocation>

    <!-- setup generation properties -->
    <setupMode>installForAllUsers|installForCurrentUser|askTheUser</setupMode>
    <setupLanguages>
        <english>compiler:Default.isl</english>
        <german>compiler:Languages\German.isl</german>
        <spanish>compiler:Languages\Spanish.isl,${project.basedir}\languages\Custom_es.isl</english>
        [...]
    </setupLanguages>
    <disableDirPage>true|false</disableDirPage>
    <disableProgramGroupPage>true|false</disableProgramGroupPage>
    <disableFinishedPage>true|false</disableFinishedPage>
    <disableWelcomePage>true|false</disableWelcomePage>
    <createDesktopIconTask>true|false</createDesktopIconTask>
    <removeOldLibs>true|false</removeOldLibs>

    <!-- signing properties -->
    <signing>
        <keystore>path/to/keystore</keystore>
        <storepass>password</storepass>
        <alias>cert_alias</alias>            
        [...]
    </signing>

    <!-- windows registry entries added during installation -->
    <registry>
        <entries>
            <entry>
                    <key>root:path/to/my/key</key>
                    <valueName>name</valueName>
                    <valueType>type</valueType>
                    <valueData>data</valueData>
            </entry>
            [...]
        </entries>
    </registry>

</winConfig>
```

| Property        | Mandatory | Default value | Description                                  |
| --------------- | --------- | ------------- | -------------------------------------------- |
| `icoFile`       | :x:       | `null`        | Icon file. If `null`, takes the default one. |
| `generateSetup` | :x:       | `true`        | Generates Setup installer.                   |
| `generateMsi`   | :x:       | `true`        | Generates MSI installer.                     |
| `generateMsm`   | :x:       | `false`       | Generates MSI merge module.                  |

## Exe creation properties

| Property            | Mandatory | Default value         | Description                                                                                                          |
| ------------------- | --------- | --------------------- | -------------------------------------------------------------------------------------------------------------------- |
| `headerType`        | :x:       | `"gui"`               | EXE header type: `console` or `gui`.                                                                                 |
| `wrapJar`           | :x:       | `true`                | Wrap JAR file in native EXE.                                                                                         |
| `companyName`       | :x:       | `${organizationName}` | EXE company name.                                                                                                    |
| `fileVersion`       | :x:       | `"1.0.0.0"`           | EXE file version.                                                                                                    |
| `txtFileVersion`    | :x:       | `${version}`          | EXE txt file version.                                                                                                |
| `productVersion`    | :x:       | `"1.0.0.0"`           | EXE product version.                                                                                                 |
| `txtProductVersion` | :x:       | `${version}`          | EXE txt product version.                                                                                             |
| `fileDescription`   | :x:       | `${description}`      | EXE file description.                                                                                                |
| `copyright`         | :x:       | `${organizationName}` | EXE copyright.                                                                                                       |
| `productName`       | :x:       | `${name}`             | EXE product name.                                                                                                    |
| `internalName`      | :x:       | `${name}`             | EXE internal name.                                                                                                   |
| `originalFilename`  | :x:       | `${name}.exe`         | EXE original filename.                                                                                               |
| `trademark`         | :x:       |                       | EXE trademark.                                                                                                       |
| `language`          | :x:       |                       | EXE language.                                                                                                        |
| `exeCreationTool`   | :x:       | `launch4j`            | EXE creation tool: [`launch4j`](http://launch4j.sourceforge.net/) or [`winrun4j`](http://winrun4j.sourceforge.net/). |
| `vmLocation`        | :x:       |                       | `jvm.dll` path relative to the bundled JRE. **Only for WinRun4J**. \*1                                               |

> \*1: If a JRE is bundled and `vmLocation` is not specified, it uses `bin\client\jvm.dll` or `bin\server\jvm.dll` as VM location by default.

## Setup generation properties

| Property                  | Mandatory | Default value                                                                              | Description                                                                                    |
| ------------------------- | --------- | ------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------- |
| `setupMode`               | :x:       | `installForAllUsers`                                                                       | Setup installation mode: require administrative privileges or not. [*](#setupmode)             |
| `setupLanguages`          | :x:       | `<english>compiler:Default.isl</english><spanish>compiler:Languages\Spanish.isl</spanish>` | Map with setup languages.                                                                      |
| `disableDirPage`          | :x:       | `true`                                                                                     | If this is set to `true`, Setup will not show the **Select Destination Location** wizard page. |
| `disableProgramGroupPage` | :x:       | `true`                                                                                     | If this is set to `true`, Setup will not show the **Select Start Menu Folder** wizard page.    |
| `disableFinishedPage`     | :x:       | `true`                                                                                     | If this is set to `true`, Setup will not show the **Setup Completed** wizard page.             |
| `disableWelcomePage`      | :x:       | `true`                                                                                     | If this is set to `true`, Setup will not show **Welcome Page**.                                |
| `createDesktopIconTask`   | :x:       | `true`                                                                                     | If this is set to `true`, Setup will ask for **desktop icon creation**.                        |
| `removeOldLibs`           | :x:       | `false`                                                                                    | If there's a previous installation, removes old `libs` folder before install.                  |

### SetupMode

Property `winConfig.setupMode` can be set with 3 possible values:

- **installForAllUsers** *(default value)*: installs the app for the all users in `%ProgramFiles%` folder (behaviour can be changed when running setup installer from command-line with `/currentuser` argument). 
- **installForCurrentUser**: installs the app for the current user in `%USERPROFILE%\AppData\Local\Programs` folder (behaviour can be changed when running setup installer from command-line with `/allusers` argument).
- **askTheUser**: asks to the final user if the app has to be installed for all users or only for the current user.

## Signing properties

|             | Mandatory                                                                     | Default value | Description                                                                                                                            |
| ----------- | ----------------------------------------------------------------------------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| `storetype` | :x:                                                                           | `JKS`         | The type of the keystore: **JKS** (Java keystore), **PKCS12** (`.p12` or `.pfx` files), **PKCS11**.                                    |
| `keystore`  | :heavy_check_mark:, unless `certfile` and `keyfile` are specified.            |               | The keystore file, or the SunPKCS11 configuration file.                                                                                |
| `certfile`  | :heavy_check_mark:, unless `keystore` is specified.                           |               | The file containing the PKCS#7 certificate chain (`.p7b` or `.spc` files).                                                             |
| `keyfile`   | :heavy_check_mark:, unless `keystore` is specified.                           |               | The file containing the private key. `PEM` and `PVK` files are supported.                                                              |
| `storepass` | :x:                                                                           |               | The password to open the keystore.                                                                                                     |
| `alias`     | :heavy_check_mark:, if `keystore` is specified and more than one alias exist. |               | The alias of the certificate used for signing in the keystore. Java code signing certificates can be used for Authenticode signatures. |
| `keypass`   | :x:                                                                           |               | The password of the private key. When using a keystore, this parameter can be omitted if the keystore shares the same password.        |
| `alg`       | :x:                                                                           | `SHA-256`     | The digest algorithm (`SHA-1`, `SHA-256`, `SHA-384` or `SHA-512`).                                                                     |

### Example using a Java KeyStore

```xml
<signing>
    <keystore>c:\Users\fvarrui\keystore.jks</keystore>
    <storepass>123456</storepass>
    <alias>fvarrui</alias>
</signing>
```

## Add values to the Windows Registry

This property allows to specify Windows Registry values to be added during installation. These values will be removed during uninstallation.

Structure of an entry:

|             | Mandatory          | Default value | Description                                                                                    |
| ----------- | ------------------ | ------------- | ---------------------------------------------------------------------------------------------- |
| `key`       | :heavy_check_mark: |               | Key path, composed by root (`HKCU`, `HKLM`, `HKU`, `HKCC`, `HKCR`), ":" and subkey.            |
| `valueName` | :heavy_check_mark: |               | Value name.                                                                                    |
| `valueType` | :x:                | `REG_SZ`      | Value type: `REG_SZ`, `REG_EXPAND_SZ`, `REG_MULTI_SZ`, `REG_DWORD`, `REG_QWORD`, `REG_BINARY`. |
| `valueData` | :x:                | `""`          | Data to be stored.                                                                             |

### Example

Next configuration will add a value named  `greeting` in `MyApp ` key, under `HKEY_CURRENT_USER (HKCU)`  root, of type `REG_SZ (string)`, with value `hello`:

```xml
<registry>
    <entries>
        <entry>
            <key>HKCU:MyApp</key>
            <valueName>greeting</valueName>
            <valueType>REG_SZ</valueType>
            <valueData>hello</valueData>
        </entry>
    </entries>
</registry>
```
