package cc.catalysts.gradle.plugins.webdeploy

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class WebDeployTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    String revision // revisions are not supported at the moment

    void copyFiles() {
        boolean hasProductionConfiguration = project.webdeploy.productionConfiguration as boolean

        log.lifecycle 'Uploading files...'
        ant.taskdef(name: 'scp2', classname: 'org.apache.tools.ant.taskdefs.optional.ssh.Scp', classpath: project.buildscript.configurations.classpath.asPath)
        ant.scp2(todir: project.webdeploy.destination, keyfile: project.webdeploy.privateKeyPath, trust: true) {
            ant.fileset(dir: '.') {
                if (project.webdeploy.onlyModifiedFiles)
                    modified()

                if (hasProductionConfiguration) {
                    String rootPath = project.file('.').absolutePath

                    exclude(name: "${project.webdeploy.productionConfiguration}/")
                    // exclude config files from root directory too; they get copied later
                    project.file(project.webdeploy.productionConfiguration).eachFileRecurse { File file ->
                        String relativeFile = file.absolutePath.substring(rootPath.length() + (project.webdeploy.productionConfiguration as String).length() + 2)
                        exclude(name: relativeFile)
                    }
                }

                for (String excludeName : project.webdeploy.excludes) {
                    exclude(name: excludeName)
                }

                exclude(name: 'build.gradle')
                exclude(name: '.gradle/')
                exclude(name: 'buildSrc/')
                exclude(name: 'cache.properties')
            }
        }

        if (hasProductionConfiguration) {
            log.lifecycle 'Uploading production configuration...'
            ant.scp2(todir: project.webdeploy.destination, keyfile: project.webdeploy.privateKeyPath, trust: true) {
                ant.fileset(dir: project.webdeploy.productionConfiguration)
            }
        }
    }

    @TaskAction
    void deploy() {
        log.openBlock("deploy")
        log.lifecycle 'Start deployment:'
        copyFiles()
        log.lifecycle 'Finished.'
        log.closeBlock("deploy")
    }

}