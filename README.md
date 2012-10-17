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

Applies grails nature to your project by updating the source sets, configure sonar (if available), and adding test and
build tasks. If the project is an grails application (not a plugin), it adds a war task. It includes
[gradle-grails-wrapper](https://github.com/ConnorWGarvey/gradle-grails-wrapper) for the grails-* tasks and automatic
download of the specified grails version.

**Example:**
```
apply plugin: 'cat-grails'
grails {
    version = '2.1.1'
}
```
Now the ```war``` task on every grails application (usually one) builds the war file with the specified grails version.
If you want to test your app before creating the archive, use the ```build``` task.

You can rename (or move) the produced war file(s) by setting the ```grails.project.war.file``` option in your
```BuildConfig.groovy``` or with a simple gradle task, eg.
```
task build(type: Sync, dependsOn: ':your-grails-app:build') {
    from 'your-grails-app/target'
	into 'build'
	include '*.war'
	rename '(.*).war', 'ROOT.war'
}
```

**NOTE: This plugin is currently intended only for multi-project builds.**

LESS
------

Converts your .less files to .css.
You don't have to install the less executable on your machine - this plugin
includes [lesscss-engine](https://github.com/asual/lesscss-engine).

**Example:**
```
apply plugin: 'cat-less'
less {
   sourceFiles = fileTree(dir: 'less', include: '*.less')
   outputDirectory = file('web-app/css')
}
```
The output gets compressed by default. You can disable this behaviour by setting ```compress = false```.

GWT
------
Compiles GWT (to do so it applies gradle default plugins 'java' and 'eclipse' and mavenCentral() as repository)  
GWT version is configurable in build.gradle via "ext.gwtVersion" (default: '2.4.0')

**Example:**
```
apply plugin: 'cat-gwt'

dependencies {
	gwtCompile 'com.google.gwt:gwt-user:' + ext.gwtVersion
	gwtCompile 'com.google.appengine:appengine:1.7.0'
}

sourceSets {
	main {
		java {
			srcDir 'src'
		}
		resources {
			srcDir 'src'
		}
	}
}
```