package cc.catalysts.boot.gradle.systemjs.task

import cc.catalysts.boot.gradle.systemjs.SystemjsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class CleanSystemjsBundle extends DefaultTask {
    @TaskAction
    void cleanSystemjsBundle() {
        SystemjsExtension config = project.systemjs;

        project.delete(config.destinationDir)
    }
}
