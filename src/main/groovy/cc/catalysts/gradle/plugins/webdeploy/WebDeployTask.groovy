package cc.catalysts.gradle.plugins.webdeploy

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class WebDeployTask extends DefaultTask {

    String revision // revisions are not supported at the moment

    void copyFiles() {
        boolean hasProductionConfiguration = project.webdeploy.productionConfiguration as boolean

        println 'Uploading files...'
        ant.taskdef(name: 'scp2', classname: 'org.apache.tools.ant.taskdefs.optional.ssh.Scp', classpath: project.buildscript.configurations.classpath.asPath)
        ant.scp2(todir: project.webdeploy.destination, keyfile: project.webdeploy.privateKeyPath, trust: true) {
            ant.fileset(dir: '.') {
                if (project.webdeploy.onlyModifiedFiles)
                    modified()
                if (hasProductionConfiguration)
                    exclude(name: "${project.webdeploy.productionConfiguration}/")

                exclude(name: 'build.gradle')
                exclude(name: '.gradle/')
                exclude(name: 'buildSrc/')
            }
        }

        if (hasProductionConfiguration) {
            println 'Uploading production configuration...'
            ant.scp2(todir: project.webdeploy.destination, keyfile: project.webdeploy.privateKeyPath, trust: true) {
                ant.fileset(dir: project.webdeploy.productionConfiguration)
            }
        }
    }

    @TaskAction
    void deploy() {
        println 'Start deployment:'
        copyFiles()
        println 'Finished.'
    }

}