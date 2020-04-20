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


#### Mac OS X config property

```xml
<macConfig>
	<!-- properties used in DMG disk image generation -->
	<backgroundImage>path/to/png</backgroundImage>
	<windowX>x</windowX>
	<windowY>y</windowY>
	<windowWidth>width</windowWidth>
	<windowHeight>height</windowHeight>
	<iconSize>size</iconSize>
	<textSize>size</textSize>
	<iconX>x</iconX>
	<iconY>y</iconY>
	<appsLinkIconX>x</appsLinkIconX>
	<appsLinkIconY>y</appsLinkIconY>
	<volumeIcon>path/to/icns</volumeIcon>
	<volumeName>${name}</volumeName>
</macConfig>
```

| Property                | Mandatory          | Default value                                                | Description                                                  |
| ----------------------- | ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `additionalModules`     | :x:                | `[]`                                                         | Additional modules to the ones identified by `jdeps` or the specified with `modules` property. |
| `additionalResources`   | :x:                | `[]`                                                         | Additional files and folders to include in the bundled app.  |
| `administratorRequired` | :x:                | `false`                                                      | App will run as administrator (with elevated privileges).    |
| `bundleJre`             | :x:                | `false`                                                      | Embeds a customized JRE with the app.                        |
| `copyDependencies`      | :x:                | `true`                                                       | Bundles all dependencies (JAR files) with the app.           |
| `createTarball`         | :x:                | `false`                                                      | Bundles app folder in tarball.                               |
| `createZipball`         | :x:                | `false`                                                      | Bundles app folder in zipball.                               |
| `customizedJre`         | :x:                | `true`                                                       | Generates a customized JRE, including only identified or specified modules. Otherwise, all modules will be included. |
| `description`           | :x:                | `${project.description}` or `${displayName}`                 | Project description.                                         |
| `displayName`           | :x:                | `${project.name}` or `${name}`                               | App name to show.                                            |
| `envPath`               | :x:                | `null`                                                       | Defines PATH environment variable in GNU/Linux and Mac OS X startup scripts. |
| `generateInstaller`     | :x:                | `true`                                                       | Generates an installer for the app.                          |
| `iconFile`              | :x:                | `null`                                                       | Path to the app icon file (PNG, XPM, ICO or ICNS).           |
| `jreDirectoryName`      | :x:                | `"jre"`                                                      | Bundled JRE directory name.                                  |
| `jrePath`               | :x:                | `""`                                                         | Path to JRE folder. If specified, it will bundle this JRE with the app, and won't generate a customized JRE. For Java 8 version or least. |
| `licenseFile`           | :x:                | `${project.licenses[0].url}` or `${project.basedir}/LICENSE` | Path to project license file.                                |
| `mainClass`             | :heavy_check_mark: | `${exec.mainClass}`                                          | Full path to your app main class.                            |
| `modules`               | :x:                | `[]`                                                         | Defines modules to customize the bundled JRE. Don't use `jdeps` to get module dependencies. |
| `name`                  | :x:                | `${project.name}` or `${project.artifactId}`                 | App name.                                                    |
| `organizationName`      | :x:                | `${project.organization.name}` or `"ACME"`                   | Organization name.                                           |
| `organizationUrl`       | :x:                | `${project.organization.url}`                                | Organization website URL.                                    |
| `organizationEmail`     | :x:                | `null`                                                       | Organization email.                                          |
| `platform`              | :x:                | `auto`                                                       | Defines the target platform, which could be different to the execution platform. Possible values:  `auto`, `mac`, `linux`, `windows`. Use `auto`  for using execution platform as target. |
| `runnableJar`           | :x:                | `null`                                                       | Defines your own JAR file to be bundled. If it's ommited, the plugin packages your code in a runnable JAR and bundle it with the app. |
| `url`                   | :x:                | `null`                                                       | App website URL.                                             |
| ` version`              | :x:                | `${project.version}`                                         | Project version.                                             |
| `versionInfo`           | :x:                | `null`                                                       | Version information for native Windows `.exe` file. :warning: **Deprecated. Use `winConfig` instead**. |
| `vmArgs`                | :x:                | `[]`                                                         | Adds VM arguments.                                           |