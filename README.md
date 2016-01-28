cat-gradle-plugins
========================

A collection of reusable gradle plugins. All the plugins are available on MavenCentral and JCenter, so it's easy to embed
them in your projects. 


Usage
=====
You can utilize every plugin seperately but we recommend to use the same version of every plugin that you are embedding.

So your build.gradle might look like that:

```
buildscript {
    repositories {
        mavenCentral()
        maven { url "https://plugins.gradle.org/m2/" }
    }

    String catGradleVersion = '0.0.11'

    dependencies {
        classpath "cc.catalysts.gradle:cat-gradle-systemjs-plugin:${catGradleVersion}"
        classpath "cc.catalysts.gradle:cat-gradle-less-plugin:0.0.10"
        classpath "cc.catalysts.gradle:cat-gradle-buildinfo-plugin:${catGradleVersion}"
    }
}
```

Changelog
=====
TBD

The latest stable version is 0.0.11


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
    classpath 'cc.catalysts.gradle:cat-gradle-systemjs-plugin:' + catGradleVersion
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

LESS
------

Takes a set of less files and compiles them to css.

Wejbars are supported by extracting all `css` and `less` files from them and by providing special global variables:

`webjars-<artifactId>` contains the base path of the given webjar (`webjars/<artifactId>/<artifactVersion>`)

*Note: As less doesn't support the '.' character in variable names these will be replaced with a '-'.'*
*For example `org.webjars.npm:imagenary.lib:1.4.9` will produce the variables `webjars-imagenary-lib`*

These variables can be used anywhere in your `less` files to import other less or css files from any webjar.

For example you can include the twitter bootstrap styles by adding the following line:

```less
@import "@{webjars-bootstrap}/less/bootstrap";
```

Configuration:

```groovy
buildscript {
  dependencies {
    classpath 'cc.catalysts.gradle:cat-gradle-less-plugin:' + catGradleVersion
  }
}

apply plugin: 'cat-less'

less {
    srcDir = new File(project.projectDir, 'src/main/resources/less')
    srcFiles = ["${project.name}.less"]
    destinationDir = new File(project.buildDir, "generated-resources/cat-less")
    cssPath = "META-INF/resources/webjars/${project.name}/${project.rootProject.version}"

}
```