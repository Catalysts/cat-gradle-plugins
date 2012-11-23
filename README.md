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

* [GRAILS](#grails)
* [LESS](#less)
* [SASS](#sass)
* [GWT](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-gwt)
* [DEPLOY](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-deploy)
* [JAXB](#jaxb)
* [QUERYDSL](#querydsl)
* [JAVAGEN](#javagen)
* [CODEGEN-JAVA](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/codegen-java)
* [ANTLR3](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-antlr3)

------


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

**NOTE:** If you 're using sonar (with the groovy plugin), don't forget to add ```sonar.project.language = 'grvy'``` to your (root) project's ```build.gradle```!

LESS
------

Converts your .less files to .css.
You don't have to install the less executable on your machine - this plugin
includes [lesscss-java](https://github.com/marceloverdijk/lesscss-java).

**Example:**
```
apply plugin: 'cat-less'
less {
   sourceFiles = fileTree(dir: 'less', include: '*.less')
   outputDirectory = file('web-app/css')
}
```
The output gets compressed by default. You can disable this behaviour by setting ```compress = false```.

SASS
------

Converts your .sass files to .css.
You don't have to install the sass executable on your machine - this plugin
includes [sass-gems](https://github.com/dmitrye/sass-gems) and [JRuby](https://github.com/jruby/jruby).

**Example:**
```
apply plugin: 'cat-sass'
sass {
   sourceFiles = fileTree(dir: 'sass', include: '*.s*ss') # .sass and .scss
   outputDirectory = file('web-app/css')
}
```

[GWT](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-gwt)
------
Compiles GWT (to do so it applies gradle default plugins 'java' and 'eclipse' and mavenCentral() as repository)  
GWT version is configurable in build.gradle via "ext.gwtVersion" (default: '2.4.0')  

Example for single / multi project projects [Wiki](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-gwt)

[DEPLOY](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-deploy)
------
Stops specified running tomcat service, copy the artifacts into the desired server directory(appends /webapps/, clears log and work directory) and then starts the tomcat service  
For usage example see [Wiki](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-deploy)

JAXB
------
Generates code from all JAXB sources specified in build.gradle (may be a list)  
WSDL files will be generated to '/target/generated-sources/${sourceSet.name}/wsdl' from schema files from 'src/${sourceSet.name}/wsdl'  
XSD files will be generated to '/target/generated-sources/${sourceSet.name}/xsd' from schema files from 'src/${sourceSet.name}/xsd'  

Example for wsdl:
```
apply plugin: 'cat-jaxb'
jaxb {
	wsdl {
		convert 'FILENAME.wsdl'
	}
}
```

Example for xsd:
```
apply plugin: 'cat-jaxb'
jaxb {
	xsd {
		convert 'FILENAME.xsd'
	}
}
```


QUERYDSL
------
Generates QueryDsl for given Project to (default) 'target/generated-sources/querydsl' (extends compileJava task).  
This folder will be added to the SourceSet.  
The 'target/generated-sources/querydsl' directory will be deleted when running 'gradle clean'.  


Example:
```
apply plugin: 'cat-querydsl'
```

Example: Change destination directory to something else then the default value
```
apply plugin: 'cat-querydsl'

querydsl {
    destinationDir 'PATH'
}
```

JAVAGEN
------
Gradle wrapper plugin for [cdoclet](https://github.com/Catalysts/cdoclet)  
Target path for generated source: "${project.projectDir}/target/generated-sources"  
Example:
```
apply plugin: 'cat-javagen'
dependencies  {
	javagen project(':PROJECTNAME')
}
```

[CODEGEN-JAVA](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/codegen-java)
------

This plugin can be used to create the build file 'Build.java' into '/target/generated-sources/main/' + package path.  
For more details see [codegen-java](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/codegen-java)


[ANTLR3](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-antlr3)
------
Gradle wrapper plugin for [antlr v3](http://www.antlr.org/)  
For extended usage example see [Wiki](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-antlr3)  
Example:
```
apply plugin: 'cat-antlr3'
antlr3 {
    generate 'XYZ.g->cc.catalysts.example.parser'
}
```