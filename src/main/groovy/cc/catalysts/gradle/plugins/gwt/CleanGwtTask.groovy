package cc.catalysts.gradle.plugins.gwt

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanGwtTask extends DefaultTask {

    CleanGwtTask() {
        dependsOn "jar"
    }

    @TaskAction
    def run() {
        println "Deleting: "
        for (module in project.gwt.modules) {
            println '     ' + project.gwt.warFolder + '/' + module.name
            project.delete project.gwt.warFolder + '/'  + module.name
        }
        println '     ' + project.gwt.warFolder + '/WEB-INF/deploy/'
        project.delete project.gwt.warFolder + '/WEB-INF/deploy/'
    }
}