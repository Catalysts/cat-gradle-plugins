package cc.catalysts.gradle.plugins.gwt

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import groovy.xml.MarkupBuilder

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class EclipseLaunchConfigGwtTask extends DefaultTask {

    def exportFileName
    def webAppDir
    def entryPointModules
    def localDepProjects


    EclipseLaunchConfigGwtTask() {
        // default values
        exportFileName = project.name + "-codesrv.launch"
        webAppDir = "src\\main\\webapp"

        // eclipse dependency -> will run at 'gradle eclipse'
        project.tasks.eclipse.dependsOn(this)
    }

    @TaskAction
    def run() {
        if(project.gwt.modules.isEmpty()) return;

        // read all modules and dependencies
        entryPointModules = []
        localDepProjects = new TreeSet()
        def moduleNames = ''

        // get local dependency projects
        (project.configurations.gwtCompile+project.configurations.gwtBuild).each {
            project.rootProject.subprojects.each { prj ->
                def prjName = prj.project.name + '-' + prj.project.version + '.jar'
                if(it.name == prjName) localDepProjects.add(prj)
            }
        }

        // get gwt modules
        for (module in project.gwt.modules) {
            entryPointModules.add(module.modulename)
            moduleNames <<= ' ' + module.modulename
        }

        // constants
        def eol = System.properties.'line.separator'
        def parentPath = project.projectDir.canonicalPath //project.rootProject.projectDir.canonicalPath

        // string output
        def writer = new StringWriter()
        // first line
        writer << '<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n'
        // xml builder
        def xml = new MarkupBuilder(writer)

        // --- build xml
        xml.launchConfiguration(type:'com.google.gdt.eclipse.suite.webapp') {
            booleanAttribute(key:'com.google.gdt.eclipse.core.RUN_SERVER', value:'false')
            stringAttribute(key:'com.google.gdt.eclipse.suiteMainTypeProcessor.PREVIOUSLY_SET_MAIN_TYPE_NAME', value:'com.google.gwt.dev.GWTShell')
            booleanAttribute(key:'com.google.gdt.eclipse.suiteWarArgumentProcessor.IS_WAR_FROM_PROJECT_PROPERTIES', value:'true')
            listAttribute(key:'com.google.gwt.eclipse.core.ENTRY_POINT_MODULES') {
                entryPointModules.each {
                    listEntry(value:it.toString())
                }
            }
            stringAttribute(key:'com.google.gwt.eclipse.core.URL', value:project.gwt.codeSrvStartupUrl)
            listAttribute(key:'org.eclipse.debug.core.MAPPED_RESOURCE_PATHS') {
                listEntry(value:'/' + project.name)
            }
            listAttribute(key:'org.eclipse.debug.core.MAPPED_RESOURCE_TYPES') {
                listEntry(value:'4')
            }
            listAttribute(key:'org.eclipse.debug.ui.favoriteGroups') {
                listEntry(value:'org.eclipse.debug.ui.launchGroup.run')
            }
            listAttribute(key:'org.eclipse.jdt.launching.CLASSPATH') {
                // add paths of local dependency projects
                localDepProjects.each {
                    def prjName = it.name
                    it.convention.plugins.java.sourceSets.main.java.srcDirs.each { File fullPath ->
                        String relPath = fullPath.getPath().substring(it.projectDir.getPath().length())
                        relPath = '/' + prjName + relPath.replace('\\', '/')
                        mkp.yieldUnescaped("\n    <listEntry value='&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;runtimeClasspathEntry internalArchive=&quot;" + relPath + "&quot; path=&quot;3&quot; type=&quot;2&quot;/&gt;&#13;&#10;'/>")
                    }
                    mkp.yieldUnescaped("\n    <listEntry value='&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;runtimeClasspathEntry path=&quot;3&quot; projectName=&quot;${prjName}&quot; type=&quot;1&quot;/&gt;&#13;&#10;'/>")
                }

                // add other dependency files
                (project.configurations.gwtCompile+project.configurations.gwtBuild).each {
                    def depPath = it.toString()
                    def depName = it.name
                    def isLocalProject = false
                    localDepProjects.each { prj ->
                        def prjName = prj.project.name + '-' + prj.project.version + '.jar'
                        if(depName == prjName) isLocalProject = true
                    }
                    // exclude all local projects
                    if (!isLocalProject) {
                        mkp.yieldUnescaped("\n    <listEntry value='&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;runtimeClasspathEntry internalArchive=&quot;${depPath}&quot; path=&quot;3&quot; type=&quot;2&quot;/&gt;&#13;&#10;'/>")
                    }
                }

            }
            stringAttribute(key:'org.eclipse.jdt.launching.CLASSPATH_PROVIDER', value:'com.google.gwt.eclipse.core.moduleClasspathProvider')
            booleanAttribute(key:'org.eclipse.jdt.launching.DEFAULT_CLASSPATH', value:'false')
            stringAttribute(key:'org.eclipse.jdt.launching.MAIN_TYPE', value:'com.google.gwt.dev.DevMode')
            mkp.yieldUnescaped("\n  <stringAttribute key='org.eclipse.jdt.launching.PROGRAM_ARGUMENTS' value='-remoteUI &quot;\${gwt_remote_ui_server_port}:\${unique_id}&quot; -startupUrl " + project.gwt.codeSrvStartupUrl + " -logLevel INFO -noserver -war ${parentPath}\\${webAppDir} -codeServerPort 9997" + moduleNames + "'/>")
            stringAttribute(key:'org.eclipse.jdt.launching.PROJECT_ATTR', value: project.name)
            stringAttribute(key:'org.eclipse.jdt.launching.VM_ARGUMENTS', value:'-Xmx1024M -XX:MaxPermSize=256m')
        }
        // --- build xml

        // format output
        String result = writer.toString()
        result = result.replaceAll(" />", "/>")
        result = result.replaceAll("\n", eol)
        result = result.replaceAll("'", '"')	// replace ' with "

        // create and write into file
        File file= new File(project.projectDir.absolutePath + '/' + exportFileName)
        file.write(result)
    }
}