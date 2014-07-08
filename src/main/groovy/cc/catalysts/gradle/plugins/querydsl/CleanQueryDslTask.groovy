package cc.catalysts.gradle.plugins.querydsl

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanQueryDslTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    @TaskAction
    def cleanQueryDslTargetDir() {
        File f = new File(project.projectDir.absolutePath, project.querydsl.destinationDir as String);
        if (f.exists()) {
            log.lifecycle "Deleting '${f.getPath()}'"
            if (!f.deleteDir()) {
                log.failure "Could not delete directory '${f.getPath()}'", true
            }
        }
    }
}