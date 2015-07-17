package cc.catalysts.gradle.plugins.antlr3

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanAntlr3Task extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    @TaskAction
    def cleanAntlr3() {
        File f = new File(project.projectDir.absolutePath, project.antlr3.destinationDir as String);
        if (f.exists()) {
            log.lifecycle "Deleting '${f.getPath()}'"
            if (!f.deleteDir()) {
                log.failure "Could not delete directory '${f.getPath()}'", true
            }
        }
    }
}