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

Grails
------

Applies grails nature to your project by updating the source sets, configure sonar (if available), and adding a
```war``` task for generating the war file. It includes [gradle-grails-wrapper](https://github.com/ConnorWGarvey/gradle-grails-wrapper)
for the grails-* tasks and automatic download of the specified grails version.

**Example:**
```
apply plugin: 'cat-grails'
grails {
    application = project(':demo-web')
    version = '2.1.0'
    warFile = file('build/ROOT.war')
}
```
Now the ```war``` tasks build the war file with the specified grails version and puts it into ```build/ROOT.war``` (relative to the root of the multi project build).

**NOTE: This plugin is intended only for multi-project builds.**