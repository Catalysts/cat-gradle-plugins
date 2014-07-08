package cc.catalysts.gradle.plugins.querydsl

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class QueryDslOutputDirTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    @TaskAction
    def createQueryDslOutputDir() {
        File f = new File(project.projectDir.absolutePath, project.querydsl.destinationDir as String);
        if (!f.exists()) {
            log.lifecycle "Creating '${f.getPath()}'"
            if (!f.mkdirs()) {
                log.failure "Could not create directory '${f.getPath()}'", true
            }
        }
    }
}