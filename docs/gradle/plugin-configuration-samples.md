# Plugin configuration samples for Gradle

## Minimal config

> :warning: This minimal configuration will not bundle a  JRE, so final user will need one in order to run the app.

### Using your own task

Add next task to your `build.gradle` file:

```groovy
task packageMyApp(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	mainClass = 'fvarrui.sample.Main'
}
```

And run `gradle packageMyApp`.

### Using default task

Default `package` task is configured using `javapackager` extension so, add next to your `build.gradle` file:

```groovy
javapackager {
	mainClass = 'fvarrio.sample.Main'
}
```
And run `gradle package`.

## Bundle with a customized JRE

```groovy
task packageMyApp(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	mainClass = 'fvarrui.sample.Main'
	bundleJre = true
}
```

> `customizedJre` is `true` by default, so you don't have to specify it.

## Bundle with a full  JRE

```groovy 
task packageMyApp(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	mainClass = 'fvarrui.sample.Main'
	bundleJre = true
	customizedJre = false
}
```

## Bundle with an existing JRE

```groovy
task packageMyApp(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	mainClass = 'fvarrui.sample.Main'
	bundleJre = true
	jrePath = file('C:\Program Files\Java\jre1.8.0_231')
}
```

## Bundle your own fat JAR

```groovy
task packageMyApp(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	mainClass = 'fvarrui.sample.Main'
	bundleJre = true
	runnableJar = file('path/to/your/own/fat.jar')
	copyDependencies = false
}
```

## Multiple executions

```groovy
javapackager {
    // common configuration
	mainClass = 'fvarrui.sample.Main'
}
task packageMyAppWithJRE(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	name = 'Sample'
	bundleJre = true
}
task packageMyAppWithoutJRE(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	name = 'Sample-nojre'
	bundleJre = false
}
task packageMyApp(dependsOn: [ 'packageMyAppWithJRE', 'packageMyAppWithoutJRE' ])
```

E.g. on Windows, last configuration will generate next artifacts:
* `Sample_x.y.z.exe` with a bundled JRE.
* `Sample-nojre_x.y.z.exe` without JRE.

## Bundling for multiple platforms

```groovy
javapackager {
	// common configuration
	mainClass = 'fvarrui.sample.Main'
	bundleJre = true
	generateInstaller = false
}
task packageMyAppForLinux(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	platform = linux
	createTarball = true
}
task packageMyAppForMac(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	platform = mac
	createTarball = true
}
task packageMyAppForWindows(type: io.github.fvarrui.javapackager.gradle.PackageTask, dependsOn: build) {
	platform = windows
	createZipball = true
}
task packageMyApp(dependsOn: [ 'packageMyAppForLinux', 'packageMyAppForMac', 'packageMyAppForWindows' ])
```

E.g. on Windows, running `packageMyApp` task will generate next artifacts:

* `${name}_${version}-linux.tar.gz` with the GNU/Linux application including a customized JRE.
* `${name}_${version}-mac.tar.gz` with the Mac OS X application including a customized JRE.
* `${name}_${version}-windows.zip` with the Windows application including a customized JRE.