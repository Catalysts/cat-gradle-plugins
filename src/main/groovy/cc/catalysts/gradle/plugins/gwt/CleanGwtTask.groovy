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
        logger.lifecycle "Deleting: "
        for (module in project.gwt.modules) {
            logger.lifecycle "     ${project.gwt.warFolder}/${module.name}"
            project.delete project.gwt.warFolder + '/'  + module.name
        }
        logger.lifecycle "     ${project.gwt.warFolder}/WEB-INF/deploy/"
        project.delete project.gwt.warFolder + '/WEB-INF/deploy/'
    }
}