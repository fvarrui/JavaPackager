# *manifest* property

`manifest` property allows to add additional manifest entries and sections.

## Maven

```xml
<manifest>
    <additionalEntries>
    	<Created-By>Peter</Created-By>
    </additionalEntries>
    <sections>
        <section>
            <name>foo/</name>
            <entries>
            	<Implementation-Version>foo1</Implementation-Version>
            </entries>
        </section>
    </sections>
</manifest>
```

## Gradle

```groovy
manifest {
    additionalEntries = [
    	'Created-By': 'Peter'
    ]
    sections = [
        new io.github.fvarrui.javapackager.model.ManifestSection ([
            name: "foo/",
            entries: [
            	'Implementation-Version': 'foo1'
            ]
        ])
    ]
}
```

## Result

Both produce the following `MANIFEST.MF` file:

```properties
Manifest-Version: 1.0
Created-By: Peter

Name: foo/
Implementation-Version: foo1
```