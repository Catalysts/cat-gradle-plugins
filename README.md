cat-gradle-plugins
========================

A collection of reusable gradle plugins.


Usage
=====
**cat-gradle-plugins** is *work-in-progress* and not yet published on maven central.  
If you 're curious, you can try it out by downloading the source and placing it into the ```buildSrc``` directory
of your gradle project.


List of plugins
===============

* [HIBERNATE](#hibernate)
* [BUILDINFO](#buildinfo)
* [SYSTEMJS](#systemjs)
* [LEGACY] (https://github.com/Catalysts/cat-gradle-plugins/tree/master/catalysts-gradle-plugins-legacy)

HIBERNATE
------
Generates static metamodel classes for Hibernate JPA. The sources will be generated to (default) 'target/generated-sources/hibernate' (extends compileJava task).  
This folder will be added to the SourceSet.  
The 'target/generated-sources/hibernate' directory will be deleted when running 'gradle clean'.  
To activate the generation for a project, just apply the plugin in the build.gradle file. Additionally you can choose the used hibernate version by defining the hibernateVersion variable on the project (or any parent project).

Example:
```
buildscript {
  dependencies {
    classpath 'cc.catalysts.gradle:cat-gradle-hibernate-plugin:' + catGradleVersion
  }
}


apply plugin: 'cat-hibernate'
```

Example: Change destination directory to something else then the default value and use a specific hibernate Version
```
ext.hibernateVersion = '4.3.1.Final'

apply plugin: 'cat-hibernate'

hibernate {
    destinationDir 'PATH'
}
```


BUILDINFO
------

Generates a class BuildInfo.java that holds information about the current build like version number and build time.

Example:
```
buildscript {
  dependencies {
    classpath 'cc.catalysts.gradle:cat-gradle-buildinfo-plugin:' + catGradleVersion
  }
}

apply plugin: 'cat-buildinfo'

buildinfo {
    packageName = 'cc.catalysts.mproject'
}
```

SYSTEMJS
------

Takes all configured files of a configured source folder and creates a systemjs bundle for it.
Currently the `package.json` and `gulpfile.js` files which perform the actual task need to be part of the module where the plugin is applied.
This will change in a future version.

```groovy
buildscript {
  dependencies {
    classpath 'cc.catalysts.gradle:cat-gradle-buildinfo-plugin:' + catGradleVersion
  }
}

apply plugin: 'cat-systemjs'

buildinfo {
    srcDir = new File(project.projectDir, 'src/main/resources')
    destinationDir = new File(project.buildDir, "generated-resources/cat-systemjs")
    includePath = "**${File.separator}*.js"
    bundlePath = "META-INF/resources/webjars/${project.name}/${project.rootProject.version}"

}
```