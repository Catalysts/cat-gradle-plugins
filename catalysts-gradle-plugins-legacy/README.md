catalysts-gradle-plugins-legacy
===============================

A collection of reusable gradle plugins.


Usage
=====
**catalysts-gradle-plugins-legacy** is *work-in-progress* and not yet published on maven central.  
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
* [HIBERNATE](#hibernate)
* [JAVAGEN](#javagen)
* [CODEGEN-JAVA](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/codegen-java)
* [ANTLR3](https://github.com/Catalysts/catalysts-gradle-plugins/wiki/cat-antlr3)
* [WEBDEPLOY](#webdeploy)
* [CLASSYCLE](#classycle)
* [JDEPS](#jdeps)

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
    version '2.3.7'
    allowBuildCompile false
}
```

Now the ```war``` task on every grails application (usually one) builds the war file with the specified grails version.
If you want to test your app before creating the archive, use the ```build``` task.
  
```compileJava, compileTestJava, compileGroovy, compileTestGroovy``` tasks will be disabled to prevent failed compilation outside of grails, due to dependencies being defined in ```BuildConfig.groovy```. 
To run those tasks anyway, set ```allowBuildCompile``` to true.

You can customize the path of the produced war file(s) by setting the ```grails.project.war.file``` option in your ```BuildConfig.groovy```.

In order to generate a code coverage report with the grails [code-coverage](http://grails.org/plugin/code-coverage) plugin, you can use the ``testCoverage`` task or use -PgrailsCoverage=true

*Note: Version 2.1 of 'cat-grails' is no longer compatible with gradle version 2.0 or lower, due to ```org.gradle.api.sonar.runner.SonarRunnerPlugin``` moving to the package ```org.gradle.sonar.runner.plugins``` !*
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
Supports two different deploy scenarios:
* lancopy  
  Stops specified running tomcat service, copy the artifacts into the desired server directory(appends /webapps/, clears log and work directory) and then starts the tomcat service
* upload  
  Upload all files and subfolder from specified directory to target host (using ssh (scp))
  
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


WEBDEPLOY
---------
Deploys files to a remote webserver over SSH (using the SCP protocol).

Example:
```
apply plugin: 'cat-webdeploy'
webdeploy {
	destination 'user@remotehost.com:/var/www/www.example.com'
	privateKeyPath System.getProperty('user.home') + '/ssh/deploykey.key'
	productionConfiguration 'productionconfig'
	exclude 'dir1/'
	exclude 'file.txt'
}
```

All files (except ```build.gradle```, ```.gradle/``` and ```buildSrc/```) will be copied to the remote destination.

**Settings:**

```productionConfiguration```: If this setting is specified, all files of this folder will be transferred to the remote destination,
and the original files will be overwritten. This setting is useful if you need special configuration files
(e.g. ```wp-config.php``` for wordpress) on the remote server.

```onlyModifiedFiles```: (boolean) if true, only local modified files get transferred to the server. Default: true

```exclude```: (String) exclude files and directories. This can be specified multiple times (see the example above).


CLASSYCLE
------

(see [classycle.sourceforge.net](http://classycle.sourceforge.net/))

Static dependency analysis to find cyclic dependencies.

**Example:**
```
apply plugin: 'cat-classycle'

// default settings
classycle {
    definitionFile = "classycle.ddf"
    checkOutputFile = "classycleCheck.xml"
    graphOutputFile = "classycleGraph.xml"
    outputDirectory = "reports"
}
```

Generates the two xml files into the project subfolder "reports" and converts the analysis into readable html.
The classycle task will be triggered automatically before the check task, so gradlew check will trigger the analysis.
Make sure to have a valid ddf file in your project folder, according to the definitionFile parameter. The simplest one looks like this:

```
show allResults
```


JDEPS
------

(see [official oracle docs](http://docs.oracle.com/javase/8/docs/technotes/tools/unix/jdeps.html))

Static dependency analysis to find cyclic dependencies.

**Example:**
```
apply plugin: 'cat-jdeps'

// default settings
jdeps {
    consolePrint    = false
    generateDotFile = false
    generateGraph   = false
    jdepsPath       = null
    outputFile      = "jdeps.txt"
    outputDirectory = "reports"
    packages        = []
    profile         = false
    recursive       = false
    regex           = null
    summary         = false
    verbose         = false
    verboseLevel    = null
}
```
