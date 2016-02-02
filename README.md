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

    String catGradleVersion = '0.0.16'

    dependencies {
        classpath "cc.catalysts.gradle:cat-gradle-systemjs-plugin:${catGradleVersion}"
        classpath "cc.catalysts.gradle:cat-gradle-less-plugin:${catGradleVersion}"
        classpath "cc.catalysts.gradle:cat-gradle-buildinfo-plugin:${catGradleVersion}"
    }
}
```

Changelog
=====

The latest stable version is 0.0.16

see [CHANGELOG.md](CHANGELOG.md) for details


List of plugins
===============

* [HIBERNATE](#hibernate)
* [BUILDINFO](#buildinfo)
* [SYSTEMJS](#systemjs)
* [LESS](#less)

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

*Note: As less doesn't support the '.' character in variable names these will be replaced with a '-'.*
*For example `org.webjars.npm:imaginary.lib:1.4.9` will produce the variable `webjars-imaginary-lib`*

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
    // The source directory of your less files
    srcDir = new File(project.projectDir, 'src/main/resources/less')
    // The actual less source files, relative to your srcDir
    srcFiles = ["${project.name}.less"]
    // The destination directory which will be treated as a 'resource' folder when the java plugin is present
    destinationDir = new File(project.buildDir, "generated-resources/cat-less")
    // The base path for all generated css files (default path matches webjar convention)
    cssPath = "META-INF/resources/webjars/${project.name}/${project.rootProject.version}"
    // A map of npm dependencies to use - can be used to change versions and add or remove plugins
    npmDependencies = [
        'less'                  : '2.5.3',
        'less-plugin-autoprefix': '1.5.1',
        'less-plugin-clean-css' : '1.5.1'
    ]
    // A list of less plugins to apply - when null then all npmDependencies starting with 'less-plugin-' will be used
    plugins = null
    // A map where the key corresponds to a less plugin name (eg 'clean-css') and the value are the cli arguments to pass to it (eg. '--s0')
    pluginOptions = [:]
    // A list of additional command line arguments to pass to the lessc
    additionalArguments = [
        '--strict-units=on'
    ]
}
```