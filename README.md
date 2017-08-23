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
        jcenter() // or mavenCentral()
        maven { url "https://plugins.gradle.org/m2/" }
    }

    String catGradleVersion = '0.0.17'

    dependencies {
        classpath "cc.catalysts.gradle:cat-gradle-buildinfo-plugin:${catGradleVersion}"
        classpath "cc.catalysts.gradle:cat-gradle-dmuncle-plugin:${catGradleVersion}"
        classpath "cc.catalysts.gradle:cat-gradle-hibernate-plugin:${catGradleVersion}"
        classpath "cc.catalysts.gradle:cat-gradle-less-plugin:${catGradleVersion}"
        classpath "cc.catalysts.gradle:cat-gradle-sass-plugin:${catGradleVersion}"
        classpath "cc.catalysts.gradle:cat-gradle-systemjs-plugin:${catGradleVersion}"
    }
}
```

Changelog
=====

The latest stable version is 0.0.17

see [CHANGELOG](CHANGELOG.md) for details


List of plugins
===============

* [BUILDINFO](#buildinfo)
* [DMUNCLE](#dmuncle)
* [HIBERNATE](#hibernate)
* [LESS](#less)
* [SASS-SCSS](#sass-scss)
* [SYSTEMJS](#systemjs)
* [WEBJARS](#webjars)

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

apply plugin: 'cc.catalysts.buildinfo'

buildinfo {
    packageName = 'cc.catalysts.mproject'
}
```

DMUNCLE
------
Analyses your project and reports dependency information to a configured server.


```groovy
buildscript {
  dependencies {
    classpath 'cc.catalysts.gradle:cat-gradle-dmuncle-plugin:' + catGradleVersion
  }
}

apply plugin: 'cc.catalysts.dmuncle'

dmuncle {
    server = null
    requestTimeout = 100000
}

```


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

apply plugin: 'cc.catalysts.hibernate'
```

Example: Change destination directory to something else then the default value and use a specific hibernate Version
```
ext.hibernateVersion = '4.3.1.Final'

apply plugin: 'cat-hibernate'

hibernate {
    destinationDir 'PATH'
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

apply plugin: 'cc.catalysts.less'

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
    // A Closure<String> to transform input file names to output file names
    cssFileName = {return it.replace('.less', '.min.css')}
}
```

Custom `Less` tasks can also be configured and have access to the same configuration options. If a option is not used the default value will be assumed.

Example for generating an unminified version into the `static/css` folder:
```groovy
task unminifiedLess(type: Less) {
    cssPath = 'static/css'
    plugins = ['autoprefix']
}

tasks.less.dependsOn('unminifiedLess')
```


SASS-SCSS
------

Takes a set of sass or scss files and compiles them to css.

Wejbars are supported by extracting all `css`, `sass` and `scss` files from them and
providing them to you as importable files.

```sass
@import "~webjars/bootstrap/sass/bootstrap";
```
The import statement must follow a special syntax to be recognized as an importable webjar.

 - First it must begin with `~webjars`
 - After that there is the name of the webjar (with replaced dots as dashes)
 - After that comes the path to the file INSIDE the webjar

Configuration:

```groovy
buildscript {
  dependencies {
    classpath 'cc.catalysts.gradle:cat-gradle-sass-plugin:' + catGradleVersion
  }
}
  
apply plugin: 'cc.catalysts.sass'
 
sass {

    // IF the directory `src/main/resources/sass` exists these two settings will be taken by default,
    srcDir = new File(project.projectDir, 'src/main/resources/sass')
    srcFiles = ["${project.name}.sass"]
    
    // ELSE if the the sass directory does not exist AND the directory `src/main/resources/scss` exists
    // these will be the default values
    srcDir = new File(project.projectDir, 'src/main/resources/scss')
    srcFiles = ["${project.name}.scss"]
        
    
    // The destination directory which will be treated as a 'resource' folder when the java plugin is present
    destinationDir = new File(project.buildDir, "generated-resources/cat-sass")
    
    // The base path for all generated css files (default path matches webjar convention)
    cssPath = "META-INF/resources/webjars/${project.name}/${project.rootProject.version}"
    
    // A map of npm dependencies to use - can be used to change versions and add or remove plugins
    npmDependencies = [
        'node-sass': '4.5.3'
    ]
    
    // A list of additional command line arguments to pass to the node-sass compiler
    // Empty by default
    additionalArguments = [ ]
}
```

SYSTEMJS
------

Takes all configured files of a configured source folder and creates a systemjs bundle for it.

```groovy
buildscript {
  dependencies {
    classpath 'cc.catalysts.gradle:cat-gradle-systemjs-plugin:' + catGradleVersion
  }
}

apply plugin: 'cc.catalysts.systemjs'

buildinfo {
    // The source directory of your js files
    srcDir = new File(project.projectDir, 'src/main/resources')
    // A glob pattern to specify which js files to include
    includePath = "**/*.js"
    // The destination directory which will be treated as a 'resource' folder when the java plugin is present
    destinationDir = new File(project.buildDir, "generated-resources/cat-systemjs")
    // The path to where the bundle file will be located
    bundlePath = "META-INF/resources/webjars/${project.name}/${project.rootProject.version}"
    // A map of npm dependencies to use - can be used to change versions
    npmDependencies = [
        'command-line-args': '2.1.4',
        'systemjs-builder' : '0.15.3'
    ]
}
```

WEBJARS
------

_This feature is considered incubating and may change completely in a future version_

This plugin creates a helper class `Webjars` containing a map which maps from webjar names to artifact information (group, name, version, webjar path).

```groovy
buildscript {
  dependencies {
    classpath 'cc.catalysts.gradle:cat-gradle-webjars-plugin:' + catGradleVersion
  }
}

apply plugin: 'cc.catalysts.webjars'

webjars {
    // The package to use for the generated 'Webjars' class
    packageName = 'cc.catalysts.gradle'
    // The destination directory which will be treated as a source folder
    destinationDir = new File(project.buildDir, "generated-resources/cat-webjars")
    // a filter closure to customize which resolved artifacts should qualify as webjars
    webjarFilter = { ResolvedArtifact it ->
        return it.moduleVersion.id.group.startsWith('org.webjars')
    }
}
```
