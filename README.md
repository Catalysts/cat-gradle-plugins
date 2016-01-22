catalysts-gradle-plugins
========================

A collection of reusable gradle plugins.


Usage
=====
**catalysts-gradle-plugins** is *work-in-progress* and not yet published on maven central.  
If you 're curious, you can try it out by downloading the source and placing it into the ```buildSrc``` directory
of your gradle project.


List of plugins
===============

* [HIBERNATE](#hibernate)
* [BUILDINFO](#buildinfo)
* [LEGACY] (https://github.com/Catalysts/catalysts-gradle-plugins/tree/master/catalysts-gradle-plugins-legacy)

HIBERNATE
------
Generates static metamodel classes for Hibernate JPA. The sources will be generated to (default) 'target/generated-sources/hibernate' (extends compileJava task).  
This folder will be added to the SourceSet.  
The 'target/generated-sources/hibernate' directory will be deleted when running 'gradle clean'.  
To activate the generation for a project, just apply the plugin in the build.gradle file. Additionally you can choose the used hibernate version by defining the hibernateVersion variable on the project (or any parent project).

Example:
```
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
apply plugin: 'cat-buildinfo'

buildinfo {
    packageName = 'cc.catalysts.mproject'
}
```