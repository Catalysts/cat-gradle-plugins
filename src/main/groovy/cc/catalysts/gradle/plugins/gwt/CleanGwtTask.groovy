package cc.catalysts.gradle.plugins.gwt

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanGwtTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    CleanGwtTask() {
        dependsOn "jar"
    }

    @TaskAction
    def run() {
        String warFolder = project.gwt.warFolder as String
        logger.lifecycle "Deleting: "
        for (GwtModule module in project.gwt.modules) {
            log.lifecycle "     ${warFolder}/${module.name}"
            project.delete warFolder + '/' + module.name
        }
        log.lifecycle "     ${warFolder}/WEB-INF/deploy/"
        project.delete warFolder + '/WEB-INF/deploy/'
    }
}