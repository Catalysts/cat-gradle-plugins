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
	gwtBuild (
		[project(path: ':', configuration: 'gwtCompile')]
	)
	
	compile 'com.google.gwt:gwt-servlet:' + ext.gwtVersion
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

gwt {
	modules {
		StockWatcher {
			modulename = 'com.google.gwt.sample.stockwatcher.StockWatcher'
		}
	}
	
	eclipse {
		codeSrvStartupUrl 'http://localhost:8888/index.html'
	}
	
	warFolder = 'war'
	workers = '2'
}
```